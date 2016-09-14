package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;

public class SettingsState extends State {
    @SuppressLint("StaticFieldLeak")
    private static SettingsState instance;

    private State nextState = this;

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

    }

    @Override
    public void entry() {
        nextState = this;
    }

    @Override
    public void exit() {

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

    }
}
