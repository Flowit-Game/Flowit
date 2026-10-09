#!/usr/bin/env python3
"""
Turns the raw screenshots from capture.py into the store screenshots and the feature graphic.
Usage: compose.py [language...]  (default: all captured languages)
"""
import glob
import os
import sys

from PIL import Image, ImageChops, ImageColor, ImageDraw, ImageFilter, ImageFont, ImageOps

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ROOT_DIR = os.path.join(SCRIPT_DIR, "..", "..")
RAW_DIR = os.path.join(SCRIPT_DIR, "raw")
LISTINGS_DIR = os.path.join(ROOT_DIR, "app", "src", "main", "play", "listings")

BLUE = "#488dd4"
GREEN = "#689f38"
RED = "#e53935"
YELLOW = "#f6ca18"
DARK = "#3c3c3c"

# How visible the background pattern is
PATTERN_OPACITY = {BLUE: 13, YELLOW: 10, GREEN: 13, RED: 20, DARK: 13}

WIDTH, HEIGHT = 1080, 1920  # Store screenshot size
GAME_WIDTH, GAME_HEIGHT = 1080, 2274  # Raw screenshot without the navigation bar, see capture.py

HEADERS = {
    "rules": {
        "en-US": "Fill every box with the color of its border",
        "de-DE": "Fülle jedes Feld mit der Farbe seines Rahmens",
        "fr-FR": "Remplis chaque case de la couleur de sa bordure",
        "it-IT": "Riempi ogni casella con il colore del suo bordo",
        "hu-HU": "Töltsd ki a mezőket a keretük színével",
    },
    "gameplay": {
        "en-US": "Easy to learn, hard to master",
        "de-DE": "Leicht zu lernen, schwer zu meistern",
        "fr-FR": "Facile à apprendre, difficile à maîtriser",
        "it-IT": "Facile da imparare, difficile da padroneggiare",
        "hu-HU": "Könnyű megtanulni, nehéz mesterré válni",
    },
    "special": {
        "en-US": "Master tricky special tiles",
        "de-DE": "Meistere knifflige Spezialfelder",
        "fr-FR": "Maîtrise des cases spéciales astucieuses",
        "it-IT": "Padroneggia caselle speciali insidiose",
        "hu-HU": "Ismerd ki a trükkös különleges mezőket",
    },
    "levels": {
        "en-US": "Hundreds of handmade levels",
        "de-DE": "Hunderte handgemachte Levels",
        "fr-FR": "Des centaines de niveaux faits main",
        "it-IT": "Centinaia di livelli fatti a mano",
        "hu-HU": "Több száz kézzel készített pálya",
    },
    "community": {
        "en-US": "Play levels made by the community",
        "de-DE": "Spiele Levels aus der Community",
        "fr-FR": "Joue aux niveaux créés par la communauté",
        "it-IT": "Gioca ai livelli creati dalla community",
        "hu-HU": "Játssz a közösség pályáival",
    },
}


def background(size, color, motif):
    """Plain color with a faint pattern of the game's symbols: "squares", "circles" or "arrows"."""
    scale = 2
    overlay = Image.new("RGBA", (size[0] * scale, size[1] * scale), (0, 0, 0, 0))
    draw = ImageDraw.Draw(overlay)
    pattern_color = (255, 255, 255) if text_color(color) == "white" else (0, 0, 0)
    faint = pattern_color + (PATTERN_OPACITY[color],)
    cell, gap, line = 220 * scale, 60 * scale, 20 * scale
    for row in range(-1, overlay.height // (cell + gap) + 2):
        for col in range(-1, overlay.width // (cell + gap) + 2):
            x = col * (cell + gap) + (row % 2) * (cell + gap) // 2 - 100 * scale
            y = row * (cell + gap) - 120 * scale
            if motif == "squares":
                draw.rectangle((x, y, x + cell, y + cell), outline=faint, width=line)
            elif motif == "circles":
                draw.ellipse((x, y, x + cell, y + cell), outline=faint, width=line)
            elif motif == "arrows":
                w, h, t = cell * 0.55, cell, cell * 0.22
                x += (cell - w) / 2
                draw.polygon([(x, y), (x + t, y), (x + w, y + h / 2),
                              (x + t, y + h), (x, y + h), (x + w - t, y + h / 2)], fill=faint)
    image = Image.new("RGBA", size, color)
    return Image.alpha_composite(image, overlay.resize(size, Image.LANCZOS))


def text_color(background_color):
    r, g, b = ImageColor.getrgb(background_color)
    return "#202124" if 0.299 * r + 0.587 * g + 0.114 * b > 180 else "white"


def header(image, text, color):
    """Centered uppercase text, as large as possible within three lines."""
    draw = ImageDraw.Draw(image)
    header_height, max_width = 520, WIDTH - 2 * 80
    for size in range(80, 30, -2):
        font = ImageFont.truetype(os.path.join(ROOT_DIR, "assets", "good-times-rg.ttf"), size)
        lines = wrap(draw, text.upper(), font, max_width)
        if len(lines) <= 3 and all(draw.textlength(line, font=font) <= max_width for line in lines):
            break
    line_height = round(size * 1.35)
    y = (header_height - len(lines) * line_height) / 2 + 20
    for line in lines:
        draw.text((WIDTH / 2, y + line_height / 2), line, font=font, fill=text_color(color), anchor="mm")
        y += line_height


def wrap(draw, text, font, max_width):
    lines = []
    for word in text.split():
        if lines and draw.textlength(lines[-1] + " " + word, font=font) <= max_width:
            lines[-1] += " " + word
        else:
            lines.append(word)
    return lines


def rounded_mask(size, radius):
    big = Image.new("L", (size[0] * 4, size[1] * 4), 0)
    ImageDraw.Draw(big).rounded_rectangle((0, 0, big.width - 1, big.height - 1), radius * 4, fill=255)
    return big.resize(size, Image.LANCZOS)


def phone(raw, screen_width):
    """The screenshot (without navigation bar) inside a simple flat phone frame."""
    bezel, radius = round(screen_width * 0.028), round(screen_width * 0.06)
    screen = raw.crop((0, 0, GAME_WIDTH, GAME_HEIGHT))
    screen = screen.resize((screen_width, round(screen.height * screen_width / screen.width)), Image.LANCZOS)
    frame = Image.new("RGBA", (screen.width + 2 * bezel, screen.height + 2 * bezel), (0, 0, 0, 0))
    frame.paste("#202124", (0, 0), rounded_mask(frame.size, radius + bezel))
    frame.paste(screen, (bezel, bezel), rounded_mask(screen.size, radius))
    return frame


def tile(raw, col, row, size):
    """One box of a 5x6 board, cut out of a raw screenshot. Layout see GameState and LevelDrawer."""
    box = GAME_WIDTH / 6
    top = GAME_WIDTH / 6 + (GAME_HEIGHT - GAME_WIDTH / 6 - 6 * box) / 2
    left = (col + 0.5) * box
    crop = raw.crop((round(left), round(top + row * box), round(left + box), round(top + (row + 1) * box)))
    return crop.convert("RGBA").resize((size, size), Image.LANCZOS)


def board_card(raw, height):
    """The game board of a raw screenshot on a rounded card."""
    game = raw.crop((0, 300, GAME_WIDTH, GAME_HEIGHT))  # Below the header of the game screen
    left, top, right, bottom = ImageChops.difference(game, Image.new("RGB", game.size, "#e5e5e5")).getbbox()
    board = game.crop((left - 40, top - 40, right + 40, bottom + 40))
    board = board.resize((round(board.width * height / board.height), height), Image.LANCZOS)
    card = Image.new("RGBA", board.size, (0, 0, 0, 0))
    card.paste(board, (0, 0), rounded_mask(board.size, height // 16))
    return card


def place(image, layer, center, angle=0):
    """Pastes a layer rotated around its center, with a soft drop shadow."""
    layer = ImageOps.expand(layer, border=4, fill=(0, 0, 0, 0)).convert("RGBa")
    layer = layer.rotate(angle, resample=Image.BILINEAR, expand=True).convert("RGBA")
    x, y = round(center[0] - layer.width / 2), round(center[1] - layer.height / 2)
    blur = max(12, layer.width // 25)
    shadow = Image.new("RGBA", image.size, (0, 0, 0, 0))
    shadow.paste((0, 0, 0, 255), (x, y + blur), layer.getchannel("A").point(lambda a: a * 0.45))
    image.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(blur)))
    image.alpha_composite(layer, (max(x, 0), max(y, 0)), (max(-x, 0), max(-y, 0)))


def arrow_badge(size, color):
    big = Image.new("RGBA", (size * 4, size * 4), (0, 0, 0, 0))
    draw = ImageDraw.Draw(big)
    draw.ellipse((0, 0, big.width - 1, big.height - 1), fill="white")
    s = big.width / 100
    for dx in (-12, 12):
        draw.line([((38 + dx) * s, 28 * s), ((60 + dx) * s, 50 * s), ((38 + dx) * s, 72 * s)],
                  fill=color, width=round(9 * s), joint="curve")
    return big.resize((size, size), Image.LANCZOS)


def slide_rules(image, raw, color):
    place(image, phone(raw["before"], screen_width=510), center=(290, 1270), angle=5)
    place(image, phone(raw["after"], screen_width=510), center=(790, 1350), angle=-5)
    place(image, arrow_badge(size=160, color=color), center=(540, 1300))


def slide_gameplay(image, raw, color):
    place(image, phone(raw["gameplay"], screen_width=720), center=(560, 1420), angle=-4)
    place(image, tile(raw["gameplay"], col=4, row=0, size=210), center=(920, 760), angle=14)  # Bomb
    place(image, tile(raw["gameplay"], col=0, row=1, size=180), center=(150, 1080), angle=-12)  # Arrow
    place(image, tile(raw["gameplay"], col=0, row=5, size=170), center=(940, 1720), angle=8)  # Circle


def slide_special(image, raw, color):
    place(image, phone(raw["special"], screen_width=680), center=(540, 1450))
    place(image, tile(raw["special"], col=3, row=3, size=250), center=(170, 820), angle=-10)  # Bomb
    place(image, tile(raw["special"], col=4, row=1, size=250), center=(910, 960), angle=12)  # Rotating arrow
    place(image, tile(raw["special"], col=0, row=3, size=210), center=(150, 1560), angle=8)  # Arrow
    place(image, tile(raw["special"], col=3, row=0, size=190), center=(930, 1700), angle=-8)  # Circle


def slide_levels(image, raw, color):
    for card, center, angle in [
        ("card1", (180, 800), 10), ("card2", (150, 1250), -6), ("card3", (190, 1700), 8),
        ("card4", (900, 760), -9), ("card5", (930, 1210), 7), ("card6", (890, 1670), -5),
    ]:
        place(image, board_card(raw[card], height=400), center=center, angle=angle)
    place(image, phone(raw["levels"], screen_width=600), center=(540, 1480), angle=3)


def slide_community(image, raw, color):
    place(image, phone(raw["packs"], screen_width=540), center=(320, 1260), angle=-8)
    place(image, phone(raw["community"], screen_width=560), center=(770, 1440), angle=6)


def feature_graphic(raw):
    """Game logo next to a tilted phone with tiles flying out of it."""
    image = background((1024, 500), BLUE, "circles")
    texture = os.path.join(ROOT_DIR, "app", "src", "main", "res", "drawable-nodpi", "texture_colorscheme_0.png")
    logo_alpha = Image.open(texture).convert("RGBA").crop((50, 82, 715, 180)).getchannel("A")
    logo_alpha = logo_alpha.resize((480, round(logo_alpha.height * 480 / logo_alpha.width)), Image.LANCZOS)
    image.paste("white", (60, (image.height - logo_alpha.height) // 2), logo_alpha)

    place(image, phone(raw["gameplay"], screen_width=300), center=(790, 400), angle=-12)
    place(image, tile(raw["special"], col=3, row=3, size=110), center=(630, 110), angle=-14)  # Bomb
    place(image, tile(raw["special"], col=4, row=1, size=120), center=(960, 90), angle=10)  # Rotating arrow
    place(image, tile(raw["gameplay"], col=0, row=1, size=90), center=(600, 420), angle=12)  # Arrow
    return image.convert("RGB")


def load_raw(language):
    paths = glob.glob(os.path.join(RAW_DIR, language, "*.png"))
    return {os.path.splitext(os.path.basename(path))[0]: Image.open(path).convert("RGB") for path in paths}


def main(languages):
    for language in languages:
        raw = load_raw(language)
        out_dir = os.path.join(LISTINGS_DIR, language, "graphics", "phone-screenshots")
        os.makedirs(out_dir, exist_ok=True)
        for old in glob.glob(os.path.join(out_dir, "*.png")):
            os.remove(old)
        for number, (name, color, motif, content) in enumerate([
            ("rules", BLUE, "squares", slide_rules),
            ("gameplay", YELLOW, "circles", slide_gameplay),
            ("special", GREEN, "arrows", slide_special),
            ("levels", RED, "squares", slide_levels),
            ("community", DARK, "circles", slide_community),
        ], start=1):
            image = background((WIDTH, HEIGHT), color, motif)
            header(image, HEADERS[name][language], color)
            content(image, raw, color)
            image.convert("RGB").save(os.path.join(out_dir, f"{number:02d}.png"), optimize=True)
        print(f"Composed screenshots for {language}")

    if "en-US" in languages:
        feature_graphic(load_raw("en-US")).save(
            os.path.join(LISTINGS_DIR, "en-US", "graphics", "feature-graphic", "banner.png"), optimize=True)


if __name__ == "__main__":
    captured = [language for language in HEADERS["rules"] if os.path.isdir(os.path.join(RAW_DIR, language))]
    main(sys.argv[1:] or captured)
