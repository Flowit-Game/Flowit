package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.AnimationFactory;
import com.bytehamster.flowit.animation.TranslateAnimation;
import com.bytehamster.flowit.objects.Plane;
import com.bytehamster.flowit.objects.TextureCoordinates;
import com.bytehamster.flowit.GLRenderer;

public class MainMenuState extends State {
    @SuppressLint("StaticFieldLeak")
    private static MainMenuState instance;
    private State nextState = this;

    private Plane logo;
    private Plane startButton;
    private Plane settingsButton;
    private Plane exitButton;

    private float logoHeight;
    private float startButtonY;
    private float settingsButtonY;
    private float exitButtonY;
    private float menuEntriesWidth;
    private float menuEntriesHeight;

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
        logoHeight = glRenderer.getWidth() / 3;
        logo = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), logoHeight, coordinatesLogo);
        glRenderer.addDrawable(logo);

        menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        menuEntriesHeight = menuEntriesWidth / 6;

        TextureCoordinates coordinatesStart = TextureCoordinates.getFromBlocks(0, 2, 6, 3);
        startButtonY = glRenderer.getHeight() - logoHeight * 1.5f - menuEntriesHeight;
        startButton = new Plane(-menuEntriesWidth, startButtonY, menuEntriesWidth, menuEntriesHeight, coordinatesStart);
        glRenderer.addDrawable(startButton);

        TextureCoordinates coordinatesSettings = TextureCoordinates.getFromBlocks(0, 3, 6, 4);
        settingsButtonY = glRenderer.getHeight() - logoHeight * 1.5f - 3 * menuEntriesHeight;
        settingsButton = new Plane(-menuEntriesWidth, settingsButtonY, menuEntriesWidth, menuEntriesHeight, coordinatesSettings);
        glRenderer.addDrawable(settingsButton);

        TextureCoordinates coordinatesExit = TextureCoordinates.getFromBlocks(0, 4, 6, 5);
        exitButtonY = glRenderer.getHeight() - logoHeight * 1.5f - 5 * menuEntriesHeight;
        exitButton = new Plane(-menuEntriesWidth, exitButtonY, menuEntriesWidth, menuEntriesHeight, coordinatesExit);
        glRenderer.addDrawable(exitButton);
    }

    @Override
    public void entry() {
        nextState = this;

        TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setFrom(0, getScreenHeight());
        logoAnimation.setTo(0, getScreenHeight() - logoHeight);
        logoAnimation.start();

        AnimationFactory.startMenuAnimationEnter(startButton, menuEntriesWidth, startButtonY, 2 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(settingsButton, menuEntriesWidth, settingsButtonY, 3 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(exitButton, menuEntriesWidth, exitButtonY, 4 * Animation.DURATION_SHORT);
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_SHORT, 0);
        logoAnimation.setFrom(0, getScreenHeight() - logoHeight);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.start();

        if (nextState == LevelPackSelectState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(startButton, menuEntriesWidth, startButtonY);
        } else {
            AnimationFactory.startMenuAnimationOut(startButton, menuEntriesWidth, startButtonY);
        }

        if (nextState == SettingsState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(settingsButton, menuEntriesWidth, settingsButtonY);
        } else {
            AnimationFactory.startMenuAnimationOut(settingsButton, menuEntriesWidth, settingsButtonY);
        }

        if (nextState == ExitState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(exitButton, menuEntriesWidth, exitButtonY);
        } else {
            AnimationFactory.startMenuAnimationOut(exitButton, menuEntriesWidth, exitButtonY);
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nextState = ExitState.getInstance();
            playSound(R.raw.click);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() < getScreenHeight() - startButtonY
                    && event.getY() > getScreenHeight() - (startButtonY + menuEntriesHeight)) {
                nextState = LevelPackSelectState.getInstance();
                playSound(R.raw.click);
            } else if (event.getY() < getScreenHeight() - settingsButtonY
                    && event.getY() > getScreenHeight() - (settingsButtonY + menuEntriesHeight)) {
                nextState = SettingsState.getInstance();
                playSound(R.raw.click);
            } else if (event.getY() < getScreenHeight() - exitButtonY
                    && event.getY() > getScreenHeight() - (exitButtonY + menuEntriesHeight)) {
                nextState = ExitState.getInstance();
                playSound(R.raw.click);
            }
        }
    }
}
