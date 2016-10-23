package com.bytehamster.flowitgame.state;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.SoundPool;

abstract public class State {
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
        /* // Debug only!
        playedPrefs.edit().putBoolean("l0", false).apply();
        playedPrefs.edit().putBoolean("l1", false).apply();
        playedPrefs.edit().putBoolean("l2", false).apply();
        playedPrefs.edit().putBoolean("l3", false).apply();
        playedPrefs.edit().putBoolean("l4", false).apply();
        */
    }

    void saveSteps(int level, int steps) {
        if (playedPrefs.getInt("s"+level, 999999) > steps) {
            playedPrefs.edit().putInt("s"+level, steps).apply();
        }
    }

    boolean isSolved (int level) {
        return playedPrefs.getBoolean("l"+level, false);
    }

    SharedPreferences getPreferences() {
        return prefs;
    }

    private int getPlayedInPack(int pack) {
        int num = 0;
        for (int i = (pack-1)*25; i < pack*25; i++) {
            if(isSolved(i)) {
                num++;
            }
        }
        return num;
    }

    boolean enoughUnlockedInPack(int pack) {
        return getPlayedInPack(pack) >= 23;
    }

    boolean isPlayable(int level) {
        if (level%25 < 3) {
            // One of the first three levels in pack
            if (level < 3) {
                return true;
            } else {
                int pack = level / 25 + 1;

                if (pack == 4 && enoughUnlockedInPack(1)) {
                    return true;
                } else if (pack == 2 && enoughUnlockedInPack(1)) {
                    return true;
                } else if (pack == 3 && enoughUnlockedInPack(2)) {
                    return true;
                }
            }
        }
        return isSolved(level - 1) || isSolved(level - 2) || isSolved(level - 3);
    }

    float getScreenWidth() {
        return screenWidth;
    }

    float getAdHeight() {
        return adHeight;
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
