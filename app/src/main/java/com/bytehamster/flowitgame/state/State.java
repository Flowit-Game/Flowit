package com.bytehamster.flowitgame.state;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.BuildConfig;
import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.SoundPool;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.LevelPack;

abstract public class State {
    static final int STEPS_NOT_SOLVED = 999;
    private static final int UNLOCK_NEXT_LEVELS;
    static {
        if (BuildConfig.DEBUG_LEVELS) {
            UNLOCK_NEXT_LEVELS = 100;
        } else {
            UNLOCK_NEXT_LEVELS = 5;
        }
    }

    private float screenWidth;
    private float screenHeight;
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

    public boolean isPlayable(Level level) {
        for (int i = 0; i <= UNLOCK_NEXT_LEVELS; i++) {
            if (level.getIndexInPack() == 0) {
                if (level.getPack() == LevelPack.EASY) {
                    return true;
                } else if (level.getPack() == LevelPack.MEDIUM) {
                    return isSolved(LevelPack.EASY.getLevel(0).getNumber());
                } else if (level.getPack() == LevelPack.HARD) {
                    return isSolved(LevelPack.MEDIUM.getLevel(0).getNumber());
                } else if (level.getPack() == LevelPack.COMMUNITY) {
                    return isSolved(LevelPack.MEDIUM.getLevel(0).getNumber());
                }
                return false;
            } else if (isSolved(level.getNumber())) {
                return true;
            }
            level = level.getPack().getLevel(level.getIndexInPack() - 1);
        }
        return false;
    }

    public float getScreenWidth() {
        return screenWidth;
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
