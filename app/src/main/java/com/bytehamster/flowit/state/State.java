package com.bytehamster.flowit.state;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.SoundPool;

abstract public class State {
    private float screenWidth;
    private float screenHeight;
    private SoundPool soundPool;
    private Activity activity;
    private SharedPreferences playedPrefs;
    private SharedPreferences prefs;

    abstract public void entry();

    abstract public void exit();

    abstract public State next();

    abstract public void onKeyDown(int keyCode, KeyEvent event);

    abstract public void onTouchEvent(MotionEvent event);

    abstract protected void initialize(GLRenderer renderer);

    public void initialize(GLRenderer renderer, SoundPool soundPool, Activity activity) {
        this.screenWidth = renderer.getWidth();
        this.screenHeight = renderer.getHeight();
        this.soundPool = soundPool;
        this.activity = activity;
        this.playedPrefs = activity.getSharedPreferences("playedState", Context.MODE_PRIVATE);
        this.prefs = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE);

        initialize(renderer);
    }

    void makePlayed(int level) {
        playedPrefs.edit().putBoolean("l"+level, true).commit();
    }

    boolean isPlayed(int level) {
        return playedPrefs.getBoolean("l"+level, false);
    }

    SharedPreferences getPreferences() {
        return prefs;
    }

    boolean isPlayable(int level) {
        if (level%25 < 3) {
            // One of the first three levels in pack
            if(level < 3) {
                return true;
            } else {
                int pack = level / 25;
                // Loop over previous pack
                for (int i = (pack-1)*25; i < pack*25; i++) {
                    if(!isPlayed(i)) {
                        return false;
                    }
                }
            }
        }
        return isPlayed(level - 1) || isPlayed(level - 2) || isPlayed(level - 3);
    }

    float getScreenWidth() {
        return screenWidth;
    }

    float getScreenHeight() {
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
