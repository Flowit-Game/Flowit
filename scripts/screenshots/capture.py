#!/usr/bin/env python3
"""
Starts an emulator with a fixed screen size, plays Flowit in all languages and saves raw screenshots
to raw/<language>/. Afterwards, compose.py turns them into the store screenshots.
Because of the fixed screen size, taps can use coordinates derived from the layout code.
Usage: capture.py [language...]  (default: all languages)
"""
import os
import subprocess
import sys
import time
import xml.etree.ElementTree as ElementTree
from collections import namedtuple

PACKAGE = "com.bytehamster.flowitgame"
LANGUAGES = ["en-US", "de-DE", "fr-FR", "it-IT", "hu-HU"]

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ROOT_DIR = os.path.join(SCRIPT_DIR, "..", "..")
RAW_DIR = os.path.join(SCRIPT_DIR, "raw")

ANDROID_HOME = os.environ["ANDROID_HOME"]
AVDMANAGER = os.path.join(ANDROID_HOME, "cmdline-tools", "latest", "bin", "avdmanager")
AVD = "FlowitScreenshots"

# Size of the game area on the emulator (1080x2400 screen minus the navigation bar)
GAME_WIDTH, GAME_HEIGHT = 1080, 2274

# Menu buttons, see MainMenuState and LevelPackSelectState
BUTTON_X = GAME_WIDTH * 0.375
BUTTON_HEIGHT = GAME_WIDTH / 8
TITLE_HEIGHT = GAME_WIDTH / 3
START_BUTTON_Y = TITLE_HEIGHT + (GAME_HEIGHT - TITLE_HEIGHT - 4 * BUTTON_HEIGHT) / 2 - BUTTON_HEIGHT / 2

Level = namedtuple("Level", "number rows cols solution")


def load_levels(file_name):
    root = ElementTree.parse(os.path.join(ROOT_DIR, "app", "src", "main", "assets", file_name)).getroot()
    levels = []
    for element in root:
        rows = element.get("modifier").split()
        solution = element.get("solution")
        levels.append(Level(number=int(element.get("number")), rows=len(rows), cols=len(rows[0]),
                            solution=solution.split(",") if solution else []))
    return levels


EASY, MEDIUM, HARD, COMMUNITY = range(4)
PACKS = [load_levels(name) for name in ["levelsEasy.xml", "levelsMedium.xml", "levelsHard.xml", "levelsCommunity.xml"]]


def adb(*args, **kwargs):
    kwargs.setdefault("check", True)
    return subprocess.run(["adb", *args], **kwargs)


def start_emulator():
    subprocess.run([AVDMANAGER, "create", "avd", "--force", "--name", AVD, "--abi", "default/x86_64",
                    "--package", "system-images;android-35;default;x86_64"], input=b"no\n", check=True)
    with open(os.path.expanduser(f"~/.android/avd/{AVD}.avd/config.ini"), "a") as config:
        config.write("hw.lcd.width=1080\n"
                     "hw.lcd.height=2400\n"
                     "hw.lcd.density=420\n"
                     "hw.ramSize=2048\n"
                     "showDeviceFrame=no\n")
    os.makedirs(RAW_DIR, exist_ok=True)
    with open(os.path.join(RAW_DIR, "emulator.log"), "w") as log:
        emulator = subprocess.Popen([os.path.join(ANDROID_HOME, "emulator", "emulator"),
                                     "-avd", AVD, "-no-snapshot", "-no-audio", "-no-boot-anim"],
                                    stdin=subprocess.DEVNULL, stdout=log, stderr=subprocess.STDOUT)
    adb("wait-for-device")
    while adb("shell", "getprop", "sys.boot_completed",
              check=False, capture_output=True, text=True).stdout.strip() != "1":
        time.sleep(1)
    return emulator


def stop_emulator(emulator):
    emulator.terminate()
    emulator.wait()
    subprocess.run([AVDMANAGER, "delete", "avd", "--name", AVD], check=True)


def wait_until_still():
    """Takes screenshots until two in a row are identical, so all animations are done."""
    image, previous = None, b""
    while image != previous:
        previous = image
        image = adb("exec-out", "screencap", "-p", capture_output=True).stdout
    return image


def tap(x, y):
    adb("shell", f"input tap {int(x)} {int(y)}")
    time.sleep(0.3)
    wait_until_still()


def screenshot(language, name):
    os.makedirs(os.path.join(RAW_DIR, language), exist_ok=True)
    with open(os.path.join(RAW_DIR, language, name + ".png"), "wb") as f:
        f.write(wait_until_still())


def write_preferences(name, entries):
    xml = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\n<map>\n" + "\n".join(entries) + "\n</map>\n"
    adb("shell", f"run-as {PACKAGE} sh -c 'mkdir -p shared_prefs; cat > shared_prefs/{name}.xml'", input=xml.encode())


def start_app(level_list_scroll=0):
    """Restarts the app with (almost) all levels solved and the default colors."""
    adb("shell", "am", "force-stop", PACKAGE)
    played = []
    for pack, levels in enumerate(PACKS):
        for index, level in enumerate(levels):
            if pack == EASY and index > 23:
                continue  # Unsolved, so the level list shows open and then locked levels
            steps = len(level.solution) or 99
            if index % 5 == 2:
                steps += 3  # Solved, but not with the optimal number of steps
            played.append(f'<boolean name="l{level.number}" value="true" />')
            played.append(f'<int name="s{level.number}" value="{steps}" />')
    write_preferences("playedState", played)
    write_preferences("preferences", [
        '<boolean name="screenshotMode" value="true" />',  # No "level complete" message
        '<int name="colorschemeIndex" value="0" />',
        f'<float name="scroll_state_1" value="{level_list_scroll:f}" />',
    ])
    adb("shell", "am", "start", "-W", "-n", PACKAGE + "/.Main", stdout=subprocess.DEVNULL)
    time.sleep(0.5)
    wait_until_still()


def play_level(pack, index, moves):
    """Opens a level from the level list (see LevelList) and plays the first moves of its solution."""
    tap(BUTTON_X, START_BUTTON_Y)
    tap(BUTTON_X, TITLE_HEIGHT + (2 * pack + 1.5) * BUTTON_HEIGHT)
    list_row, list_col = divmod(index, 3)
    tap(list_col * GAME_WIDTH / 3 + GAME_WIDTH / 9, TITLE_HEIGHT + list_row * GAME_WIDTH / 6 + GAME_WIDTH / 18)

    # Board layout, see GameState and LevelDrawer
    level = PACKS[pack][index]
    box = GAME_WIDTH / (level.cols + 1)
    board_top = GAME_WIDTH / 6 + (GAME_HEIGHT - GAME_WIDTH / 6 - level.rows * box) / 2
    for move in level.solution[:moves]:
        col = ord(move[0]) - ord("A")
        row = int(move[1:]) - 1
        tap((col + 1) * box, board_top + (row + 0.5) * box)


def capture(language):
    for name, pack, index, solution_moves in [
        ("before", EASY, 10, 0),
        ("after", EASY, 10, 8),
        ("gameplay", MEDIUM, 22, 9),
        ("special", HARD, 8, 5),
        ("community", COMMUNITY, 15, 3),
        ("card1", MEDIUM, 12, 8),
        ("card2", COMMUNITY, 8, 6),
        ("card3", EASY, 18, 10),
        ("card4", HARD, 1, 8),
        ("card5", COMMUNITY, 1, 6),
        ("card6", MEDIUM, 5, 5),
    ]:
        start_app()
        play_level(pack, index, solution_moves)
        screenshot(language, name)

    start_app(level_list_scroll=GAME_HEIGHT - TITLE_HEIGHT + 5 * GAME_WIDTH / 6)  # Scrolled down by 5 rows
    tap(BUTTON_X, START_BUTTON_Y)
    tap(BUTTON_X, TITLE_HEIGHT + 1.5 * BUTTON_HEIGHT)  # Easy
    screenshot(language, "levels")

    start_app()
    tap(BUTTON_X, START_BUTTON_Y)
    screenshot(language, "packs")


def main(languages):
    subprocess.run(["./gradlew", ":app:assembleDebug"], cwd=ROOT_DIR, check=True)
    emulator = start_emulator()
    try:
        adb("install", "-r", os.path.join(ROOT_DIR, "app", "build", "outputs", "apk", "debug", "app-debug.apk"))
        for language in languages:
            adb("shell", "cmd", "locale", "set-app-locales", PACKAGE, "--locales", language)
            capture(language)
    finally:
        stop_emulator(emulator)


if __name__ == "__main__":
    main(sys.argv[1:] or LANGUAGES)
