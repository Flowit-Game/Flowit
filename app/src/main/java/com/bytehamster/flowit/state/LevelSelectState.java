package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.ScaleAnimation;
import com.bytehamster.flowit.animation.TranslateAnimation;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

public class LevelSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelSelectState instance;
    private State nextState = this;

    private int pack = 0;
    private Plane selectLevelText;
    private Plane[] levelIcons = new Plane[25];
    private float boxSize = 0;
    private float boxStart = 6;
    private float logoHeight;

    private LevelSelectState() {

    }

    public static LevelSelectState getInstance() {
        if (instance == null) {
            instance = new LevelSelectState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        TextureCoordinates coordinatesLogo = TextureCoordinates.getFromBlocks(0, 11, 6, 13);
        logoHeight = glRenderer.getWidth() / 3;
        selectLevelText = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), logoHeight, coordinatesLogo);
        glRenderer.addDrawable(selectLevelText);


        boxSize = getScreenWidth() / (5 + 2 + 2);
        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col] = new Plane((col * 1.5f + 1) * boxSize,
                        getScreenHeight() - (row * 1.5f + boxStart) * boxSize, boxSize, boxSize, coordinatesLevel);
                levelIcons[row * 5 + col].setVisible(false);
                levelIcons[row * 5 + col].setScale(0);
                glRenderer.addDrawable(levelIcons[row * 5 + col]);
            }
        }
    }

    @Override
    public void entry() {
        nextState = this;

        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setFrom(0, getScreenHeight());
        logoAnimation.setTo(0, getScreenHeight() - logoHeight);
        logoAnimation.start();

        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, pack-1, 7, pack);
        TextureCoordinates coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, pack-1, 8, pack);
        TextureCoordinates coordinatesLevelLocked = TextureCoordinates.getFromBlocks(5+pack, 3, 6+pack, 4);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int levelID = (pack-1)*25 + row * 5 + col;
                if(isPlayed(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelDone);
                } else if(!isPlayable(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelLocked);
                } else {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevel);
                }
                levelIcons[row * 5 + col].setVisible(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row) + Animation.DURATION_LONG));
                scaleAnimation.setFrom(0);
                scaleAnimation.setTo(1);
                scaleAnimation.start();
            }
        }
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_SHORT, 0);
        logoAnimation.setFrom(0, getScreenHeight() - logoHeight);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.start();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col].setVisible(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row)));
                scaleAnimation.setFrom(1);
                scaleAnimation.setTo(0);
                scaleAnimation.setHideAfter(true);
                scaleAnimation.start();
            }
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nextState = LevelPackSelectState.getInstance();
            playSound(R.raw.click);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    if(event.getY() > (row * 1.5f + boxStart - 1) * boxSize - 0.5*boxSize
                            && event.getY() < (row * 1.5f + boxStart) * boxSize + 0.5*boxSize
                            && event.getX() > (col * 1.5f + 1) * boxSize - 0.5*boxSize
                            && event.getX() < (col * 1.5f + 2) * boxSize + 0.5*boxSize) {
                        GameState.getInstance().setLevel((pack - 1)*25 + (row * 5 + col));
                        nextState = GameState.getInstance();
                        playSound(R.raw.click);
                    }
                }
            }
        }
    }

    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }
}
