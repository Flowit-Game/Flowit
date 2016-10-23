package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.object.ObjectFactory;
import com.bytehamster.flowitgame.object.Plane;

public class SettingsState extends State {
    @SuppressLint("StaticFieldLeak")
    private static SettingsState instance;
    private State nextState = this;

    private Plane volumeOff;
    private Plane volumeOn;

    private SettingsState() {

    }

    public static SettingsState getInstance() {
        if (instance == null) {
            instance = new SettingsState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        volumeOn  = ObjectFactory.createSingleBox(0, 15, getScreenWidth()/4);
        volumeOn.setVisible(false);
        volumeOn.setX(getScreenWidth()/8*3);
        volumeOn.setY((getScreenHeight() + getAdHeight() - volumeOn.getHeight())/2);
        glRenderer.addDrawable(volumeOn);
        volumeOff = ObjectFactory.createSingleBox(1, 15, getScreenWidth()/4);
        volumeOff.setVisible(false);
        volumeOff.setX(getScreenWidth()/8*3);
        volumeOff.setY((getScreenHeight() + getAdHeight() - volumeOff.getHeight())/2);
        glRenderer.addDrawable(volumeOff);
    }

    @Override
    public void entry() {
        nextState = this;

        Plane objShown = getPreferences().getBoolean("volumeOn", true) ? volumeOn : volumeOff;
        objShown.setVisible(true);
        objShown.setScale(0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(objShown,
                Animation.DURATION_LONG, Animation.DURATION_LONG);
        scaleAnimation.setTo(1);
        scaleAnimation.start();
    }

    @Override
    public void exit() {
        Plane objShown = getPreferences().getBoolean("volumeOn", true) ? volumeOn : volumeOff;
        ScaleAnimation scaleAnimation = new ScaleAnimation(objShown,
                Animation.DURATION_LONG, 0);
        scaleAnimation.setTo(0);
        scaleAnimation.setHideAfter(true);
        scaleAnimation.start();
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onBackPressed() {
        nextState = MainMenuState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (volumeOn.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                boolean newVolume = ! getPreferences().getBoolean("volumeOn", true);
                getPreferences().edit().putBoolean("volumeOn", newVolume).apply();
                volumeOff.setVisible(!newVolume);
                volumeOn.setVisible(newVolume);
            }
        }
    }
}
