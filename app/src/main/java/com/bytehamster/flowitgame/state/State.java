package com.bytehamster.flowitgame.state;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.SoundPool;
import com.bytehamster.flowitgame.util.PackRanges;

abstract public class State {
    static final int STEPS_NOT_SOLVED = 999;
    private static final int UNLOCK_NEXT_LEVELS = 5;

    private float screenWidth;
    private float screenHeight;
    private float adHeight;
    private SoundPool soundPool;
    private Activity activity;
    private SharedPreferences playedPrefs;
    private SharedPreferences prefs;

    abstract public void entry();

    abstract public void exit();

    abstract public State next();

    abstract public void onBackPressed();

    abstract public void onTouchEvent(MotionEvent event);

    abstract protected void initialize(GLRenderer renderer);

    public void initialize(GLRenderer renderer, SoundPool soundPool, Activity activity, float adHeight) {
        this.screenWidth = renderer.getWidth();
        this.screenHeight = renderer.getHeight();
        this.soundPool = soundPool;
        this.activity = activity;
        this.playedPrefs = activity.getSharedPreferences("playedState", Context.MODE_PRIVATE);
        this.prefs = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.adHeight = adHeight;

        initialize(renderer);
    }

    void makePlayed(int level) {
        playedPrefs.edit().putBoolean("l"+level, true).apply();
    }

    void makeUnPlayed(int level) {
        playedPrefs.edit().putBoolean("l"+level, false).apply();
    }

    void saveSteps(int level, int steps) {
        if (playedPrefs.getInt("s"+level, STEPS_NOT_SOLVED) > steps) {
            playedPrefs.edit().putInt("s"+level, steps).apply();
        }
    }

    int loadSteps(int level) {
        return playedPrefs.getInt("s"+level, STEPS_NOT_SOLVED);
    }

    public boolean isSolved (int level) {
        return playedPrefs.getBoolean("l"+level, false);
    }

    SharedPreferences getPreferences() {
        return prefs;
    }

    public boolean isPlayable(int level) {
        if (PackRanges.isFirstInPack(level)) {
            return true;
        }
        for (int i = 0; i <= UNLOCK_NEXT_LEVELS; i++) {
            if (isSolved(level)) {
                return true;
            }
            level = PackRanges.previousLevel(level);
        }
        return false;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    float getAdHeight() {
        return adHeight;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public void playSound(int resId) {
        if(getPreferences().getBoolean("volumeOn", true)) {
            soundPool.playSound(resId);
        }
    }

    Activity getActivity() {
        return activity;
    }
}
