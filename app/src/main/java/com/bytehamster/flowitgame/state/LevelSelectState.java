package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;

public class LevelSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelSelectState instance;
    private State nextState = this;

    private int pack = 1;
    private Plane selectLevelText;
    private final Plane[] levelIcons = new Plane[25];

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
        selectLevelText = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLogo);
        selectLevelText.setVisible(false);
        glRenderer.addDrawable(selectLevelText);


        float boxSize = getScreenWidth() / (5 + 2 + 2);
        float availableSpace = getScreenHeight() - getAdHeight() - selectLevelText.getHeight();
        float boxStart = getScreenHeight() - selectLevelText.getHeight() - (availableSpace - boxSize * 6.5f) / 2;
        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col] = new Plane((col * 1.5f + 1) * boxSize,
                        boxStart - (row * 1.5f) * boxSize, boxSize, boxSize, coordinatesLevel);
                levelIcons[row * 5 + col].setVisible(false);
                levelIcons[row * 5 + col].setScale(0);
                glRenderer.addDrawable(levelIcons[row * 5 + col]);
            }
        }
    }

    @Override
    public void entry() {
        nextState = this;

        selectLevelText.cancelAnimations();
        selectLevelText.setY(getScreenHeight());
        selectLevelText.setVisible(true);
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setTo(0, getScreenHeight() - selectLevelText.getHeight());
        logoAnimation.start();

        TextureCoordinates coordinatesLevel;
        TextureCoordinates coordinatesLevelDone;
        TextureCoordinates coordinatesLevelLocked;
        if (pack == 4) {
            coordinatesLevel = TextureCoordinates.getFromBlocks(11, 1, 12, 2);
            coordinatesLevelDone = TextureCoordinates.getFromBlocks(12, 1, 13, 2);
            coordinatesLevelLocked = TextureCoordinates.getFromBlocks(13, 1, 14, 2);
        } else {
            coordinatesLevel = TextureCoordinates.getFromBlocks(6, pack - 1, 7, pack);
            coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, pack - 1, 8, pack);
            coordinatesLevelLocked = TextureCoordinates.getFromBlocks(5 + pack, 3, 6 + pack, 4);
        }

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int levelID = (pack - 1) * 25 + row * 5 + col;
                if (isSolved(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelDone);
                } else if (!isPlayable(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelLocked);
                } else {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevel);
                }
                levelIcons[row * 5 + col].setVisible(true);
                levelIcons[row * 5 + col].setScale(0);
                levelIcons[row * 5 + col].cancelAnimations();

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row) + Animation.DURATION_LONG));
                scaleAnimation.setTo(1);
                scaleAnimation.start();
            }
        }
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_SHORT, 0);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col].setVisible(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row)));
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
    public void onBackPressed() {
        nextState = LevelPackSelectState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            for (int i = 0; i < levelIcons.length; i++) {
                if (levelIcons[i].collides(event, getScreenHeight())) {
                    GameState.getInstance().setLevel((pack - 1) * 25 + i);
                    nextState = GameState.getInstance();
                    playSound(R.raw.click);
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
