package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.ScaleAnimation;
import com.bytehamster.flowit.object.ObjectFactory;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

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
        volumeOn.setY((getScreenHeight()-volumeOn.getHeight())/2);
        glRenderer.addDrawable(volumeOn);
        volumeOff = ObjectFactory.createSingleBox(1, 15, getScreenWidth()/4);
        volumeOff.setVisible(false);
        volumeOff.setX(getScreenWidth()/8*3);
        volumeOff.setY((getScreenHeight()-volumeOff.getHeight())/2);
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
        scaleAnimation.setFrom(0);
        scaleAnimation.setTo(1);
        scaleAnimation.start();
    }

    @Override
    public void exit() {
        Plane objShown = getPreferences().getBoolean("volumeOn", true) ? volumeOn : volumeOff;
        ScaleAnimation scaleAnimation = new ScaleAnimation(objShown,
                Animation.DURATION_LONG, 0);
        scaleAnimation.setFrom(1);
        scaleAnimation.setTo(0);
        scaleAnimation.setHideAfter(true);
        scaleAnimation.start();
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nextState = MainMenuState.getInstance();
            playSound(R.raw.click);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (volumeOn.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                boolean newVolume = ! getPreferences().getBoolean("volumeOn", true);
                getPreferences().edit().putBoolean("volumeOn", newVolume).commit();
                volumeOff.setVisible(!newVolume);
                volumeOn.setVisible(newVolume);
            }
        }
    }
}
