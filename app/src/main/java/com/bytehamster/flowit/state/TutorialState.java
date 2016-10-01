package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.AnimationFactory;
import com.bytehamster.flowit.animation.ScaleAnimation;
import com.bytehamster.flowit.animation.TranslateAnimation;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

public class TutorialState extends State {
    @SuppressLint("StaticFieldLeak")
    private static TutorialState instance;
    private State nextState = this;

    private Plane logo;
    private Plane screen1;
    private Plane screen2;
    private int screenNumber = 1;

    private TutorialState() {

    }

    public static TutorialState getInstance() {
        if (instance == null) {
            instance = new TutorialState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        TextureCoordinates coordinatesLogo = TextureCoordinates.getFromBlocks(6, 4, 11, 6);
        float logoHeight = glRenderer.getWidth() * (2f / 5f);
        logo = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), logoHeight, coordinatesLogo);
        glRenderer.addDrawable(logo);

        float tutScreenWidth = glRenderer.getWidth() * (5f / 6f);
        float tutScreenHeight = tutScreenWidth * (4f / 5f);
        float tutScreenX = glRenderer.getWidth() * (1f / 12f);
        float tutScreenY = glRenderer.getHeight() - logoHeight - (glRenderer.getHeight() - getAdHeight() + tutScreenHeight - logoHeight)/2;
        TextureCoordinates coordinatesScreen1 = TextureCoordinates.getFromBlocks(6, 6, 11, 10);
        screen1 = new Plane(tutScreenX, tutScreenY, tutScreenWidth, tutScreenHeight, coordinatesScreen1);
        screen1.setVisible(false);
        glRenderer.addDrawable(screen1);

        TextureCoordinates coordinatesScreen2 = TextureCoordinates.getFromBlocks(11, 4, 16, 8);
        screen2 = new Plane(tutScreenX, tutScreenY, tutScreenWidth, tutScreenHeight, coordinatesScreen2);
        screen2.setVisible(false);
        glRenderer.addDrawable(screen2);
    }

    @Override
    public void entry() {
        nextState = this;

        if(screenNumber == 1) {
            TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_LONG, Animation.DURATION_SHORT);
            logoAnimation.setFrom(0, getScreenHeight());
            logoAnimation.setTo(0, getScreenHeight() - logo.getHeight());
            logoAnimation.start();
        }

        if(screenNumber == 1) {
            AnimationFactory.startScaleShow(screen1);
        } else {
            AnimationFactory.startScaleShow(screen2, 0);
        }
    }

    @Override
    public void exit() {
        if(screenNumber == 3) {
            screenNumber = 2;
            TranslateAnimation logoAnimation = new TranslateAnimation(logo, Animation.DURATION_SHORT, 0);
            logoAnimation.setFrom(0, getScreenHeight() - logo.getHeight());
            logoAnimation.setTo(0, getScreenHeight());
            logoAnimation.start();
        }

        if(screenNumber == 1) {
            AnimationFactory.startScaleHide(screen1, 0);
        } else {
            AnimationFactory.startScaleHide(screen2);
            screenNumber = 1;
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (screenNumber == 1) {
                playSound(R.raw.click);
                exit();
                screenNumber = 2;
                entry();
            } else {
                screenNumber = 3;
                playSound(R.raw.click);
                LevelSelectState.getInstance().setPack(1);
                GameState.getInstance().setLevel(0);
                nextState = GameState.getInstance();
            }
        }
    }
}
