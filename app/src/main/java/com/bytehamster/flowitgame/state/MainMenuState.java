package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;
import com.bytehamster.flowitgame.GLRenderer;

public class MainMenuState extends State {
    @SuppressLint("StaticFieldLeak")
    private static MainMenuState instance;
    private State nextState = this;

    private Plane logo;
    private Plane startButton;
    private Plane settingsButton;
    private Plane exitButton;

    private MainMenuState() {

    }

    public static MainMenuState getInstance() {
        if (instance == null) {
            instance = new MainMenuState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        TextureCoordinates coordinatesLogo = TextureCoordinates.getFromBlocks(0, 0, 6, 2);
        float logoHeight = glRenderer.getWidth() / 3;
        logo = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), logoHeight, coordinatesLogo);
        glRenderer.addDrawable(logo);

        float menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        float menuEntriesHeight = menuEntriesWidth / 6;
        float menuEntriesAvailableSpace = getScreenHeight() - getAdHeight() - logoHeight;
        float menuEntriesStartY = getScreenHeight() - logoHeight - (menuEntriesAvailableSpace - 4 * menuEntriesHeight) / 2;

        TextureCoordinates coordinatesStart = TextureCoordinates.getFromBlocks(0, 2, 6, 3);
        startButton = new Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesStart);
        glRenderer.addDrawable(startButton);

        TextureCoordinates coordinatesSettings = TextureCoordinates.getFromBlocks(0, 3, 6, 4);
        settingsButton = new Plane(-menuEntriesWidth, startButton.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesSettings);
        glRenderer.addDrawable(settingsButton);

        TextureCoordinates coordinatesExit = TextureCoordinates.getFromBlocks(0, 4, 6, 5);
        exitButton = new Plane(-menuEntriesWidth, settingsButton.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesExit);
        glRenderer.addDrawable(exitButton);
    }

    @Override
    public void entry() {
        nextState = this;

        TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setFrom(0, getScreenHeight());
        logoAnimation.setTo(0, getScreenHeight() - logo.getHeight());
        logoAnimation.start();

        AnimationFactory.startMenuAnimationEnter(startButton, 2 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(settingsButton, 3 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(exitButton, 4 * Animation.DURATION_SHORT);
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_SHORT, 0);
        logoAnimation.setFrom(0, getScreenHeight() - logo.getHeight());
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.start();

        if (nextState == LevelPackSelectState.getInstance() || nextState == TutorialState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(startButton);
        } else {
            AnimationFactory.startMenuAnimationOut(startButton);
        }

        if (nextState == SettingsState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(settingsButton);
        } else {
            AnimationFactory.startMenuAnimationOut(settingsButton);
        }

        if (nextState == ExitState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(exitButton);
        } else {
            AnimationFactory.startMenuAnimationOut(exitButton);
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onBackPressed() {
        nextState = ExitState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (startButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                if(getPreferences().getBoolean("tutorialDisplayed", false)) {
                    nextState = LevelPackSelectState.getInstance();
                } else {
                    nextState = TutorialState.getInstance();
                    getPreferences().edit().putBoolean("tutorialDisplayed", true).apply();
                }
            } else if (settingsButton.collides(event, getScreenHeight())) {
                nextState = SettingsState.getInstance();
                playSound(R.raw.click);
            } else if (exitButton.collides(event, getScreenHeight())) {
                nextState = ExitState.getInstance();
                playSound(R.raw.click);
            }
        }
    }
}
