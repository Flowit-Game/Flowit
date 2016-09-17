package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.Converter;
import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.ScaleAnimation;
import com.bytehamster.flowit.animation.TranslateAnimation;
import com.bytehamster.flowit.filler.BombFiller;
import com.bytehamster.flowit.filler.DirectionFiller;
import com.bytehamster.flowit.filler.FloodFiller;
import com.bytehamster.flowit.model.Field;
import com.bytehamster.flowit.model.Level;
import com.bytehamster.flowit.object.LevelDrawer;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

public class GameState extends State {
    @SuppressLint("StaticFieldLeak")
    private static GameState instance;

    private State nextState = this;
    private int level = 0;
    private Level levelData = null;
    private float boardStartY = 0;
    private final LevelDrawer levelDrawer = LevelDrawer.getInstance();
    private Plane winMessage;
    private Plane lockedMessage;
    private Plane left;
    private Plane right;
    private boolean isFilling = false;
    private boolean won = false;

    private GameState() {

    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        boardStartY = 0.5f * getScreenWidth();
        levelDrawer.setVisible(false);
        levelDrawer.setScreenHeight(getScreenHeight());
        levelDrawer.setScreenWidth(getScreenWidth());
        levelDrawer.initialize();
        glRenderer.addDrawable(levelDrawer);

        TextureCoordinates coordinatesWin = TextureCoordinates.getFromBlocks(0, 8, 6, 10);
        winMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesWin);
        winMessage.setVisible(false);
        glRenderer.addDrawable(winMessage);

        TextureCoordinates coordinatesLocked = TextureCoordinates.getFromBlocks(0, 13, 6, 15);
        lockedMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLocked);
        lockedMessage.setVisible(false);
        lockedMessage.setY(getScreenHeight() - getScreenWidth()*1.3f);
        glRenderer.addDrawable(lockedMessage);

        float size = glRenderer.getWidth() / 8f;
        TextureCoordinates coordinatesLeft = TextureCoordinates.getFromBlocks(0, 10, 1, 11);
        left = new Plane(size * 0.5f, glRenderer.getHeight() - size * 1.5f, size, size, coordinatesLeft);
        left.setVisible(false);
        glRenderer.addDrawable(left);

        TextureCoordinates coordinatesRight = TextureCoordinates.getFromBlocks(1, 10, 2, 11);
        right = new Plane(glRenderer.getWidth() - size * 1.5f, glRenderer.getHeight() - size * 1.5f, size, size, coordinatesRight);
        right.setVisible(false);
        glRenderer.addDrawable(right);
    }

    @Override
    public void entry() {
        nextState = this;
        reloadLevel();

        levelDrawer.setVisible(true);
        levelDrawer.setY(-levelDrawer.getBoxSize());
        TranslateAnimation logoAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setFrom(0, -levelDrawer.getBoxSize());
        logoAnimation.setTo(0, getScreenHeight() - boardStartY);
        logoAnimation.start();

        left.setScale(0);
        left.setVisible(true);
        ScaleAnimation leftAnimation = new ScaleAnimation(left, Animation.DURATION_LONG, Animation.DURATION_LONG);
        leftAnimation.setFrom(0);
        leftAnimation.setTo(1);
        leftAnimation.start();

        right.setScale(0);
        right.setVisible(true);
        ScaleAnimation rightAnimation = new ScaleAnimation(right, Animation.DURATION_LONG, Animation.DURATION_LONG);
        rightAnimation.setFrom(0);
        rightAnimation.setTo(1);
        rightAnimation.start();
    }

    private void reloadLevel() {
        won = false;
        isFilling = false;
        levelData = new Level(level, getActivity());
        levelDrawer.setLevel(levelData);

        if(!isPlayable(level)) {
            if(!lockedMessage.isVisible()) {
                lockedMessage.setY(-getScreenWidth() * 0.5f);
                lockedMessage.setVisible(true);
                TranslateAnimation inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, Animation.DURATION_SHORT);
                inAnimation.setFrom(0, -getScreenWidth() * 0.5f);
                inAnimation.setTo(0, getScreenHeight() - getScreenWidth() * 1.3f);
                inAnimation.start();
            }
        } else {
            TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
            outAnimation.setFrom(0, getScreenHeight() - getScreenWidth()*1.3f);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setFrom(0, getScreenHeight() - boardStartY);
        logoAnimation.setTo(0, -levelDrawer.getBoxSize());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        ScaleAnimation leftAnimation = new ScaleAnimation(left, Animation.DURATION_LONG, Animation.DURATION_LONG);
        leftAnimation.setFrom(1);
        leftAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        leftAnimation.start();

        ScaleAnimation rightAnimation = new ScaleAnimation(right, Animation.DURATION_LONG, Animation.DURATION_LONG);
        rightAnimation.setFrom(1);
        rightAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        rightAnimation.start();

        TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
        outAnimation.setFrom(0, getScreenHeight() - getScreenWidth()*1.3f);
        outAnimation.setTo(0, -getScreenWidth() * 0.5f);
        outAnimation.setHideAfter(true);
        outAnimation.start();
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nextState = LevelSelectState.getInstance();
            playSound(R.raw.click);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (event.getY() < getScreenWidth() / 8f * 2f) {
                if (event.getX() < getScreenWidth() / 8f * 2f) {
                    playSound(R.raw.click);
                    if (level % 25 == 0) {
                        nextState = LevelSelectState.getInstance();
                    } else {
                        level--;
                        reloadLevel();
                    }
                } else if (event.getX() > getScreenWidth() - getScreenWidth() / 8f * 2f) {
                    playSound(R.raw.click);
                    level++;
                    if (level % 25 == 0) {
                        nextState = LevelSelectState.getInstance();
                    } else {
                        reloadLevel();
                    }
                }
            }

            if (isFilling || won || !isPlayable(level)) {
                return;
            }

            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < 5; col++) {
                    if (event.getY() > boardStartY + (row - 1) * levelDrawer.getBoxSize()
                            && event.getY() < boardStartY + row * levelDrawer.getBoxSize()
                            && event.getX() > (col + 0.5) * levelDrawer.getBoxSize()
                            && event.getX() < (col + 1.5) * levelDrawer.getBoxSize()) {

                        switch (levelData.fieldAt(col, row).getModifier()) {
                            case FLOOD:
                                playSound(R.raw.click);
                                isFilling = true;
                                new FloodFiller(levelData, this, new Runnable() {
                                    @Override
                                    public void run() {
                                        isFilling = false;
                                        checkWon();
                                    }
                                }).fill(col, row);
                                break;
                            case BOMB:
                                playSound(R.raw.click);
                                isFilling = true;
                                new BombFiller(levelData, this, new Runnable() {
                                    @Override
                                    public void run() {
                                        isFilling = false;
                                        checkWon();
                                    }
                                }).fill(col, row);
                                break;
                            case UP:
                                doFill(0, -1);
                                break;
                            case RIGHT:
                                doFill(1, 0);
                                break;
                            case LEFT:
                                doFill(-1, 0);
                                break;
                            case DOWN:
                                doFill(0, 1);
                                break;
                            default:
                                //Ignore
                                break;
                        }
                    }
                }
            }
        }
    }

    private void doFill(int col, int row) {
        playSound(R.raw.click);
        isFilling = true;
        new DirectionFiller(levelData, this, new Runnable() {
            @Override
            public void run() {
                isFilling = false;
                checkWon();
            }
        }, col, row).fill(col, row);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private void checkWon() {
        won = true;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 5; c++) {
                Field f = levelData.fieldAt(c, r);
                if (Converter.convertColor(f.getModifier()) != null // Is not a color
                        && f.getColor() != Converter.convertColor(f.getModifier())) {
                    won = false;
                }
            }
        }

        if (won) {
            playSound(R.raw.won);
            makePlayed(level);

            winMessage.setY(-getScreenWidth() * 0.5f);
            winMessage.setVisible(true);
            TranslateAnimation inAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0);
            inAnimation.setFrom(0, -getScreenWidth() * 0.5f);
            inAnimation.setTo(0, getScreenHeight() - getScreenWidth()*1.3f);
            inAnimation.start();

            TranslateAnimation outAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 4 * Animation.DURATION_SHORT);
            outAnimation.setFrom(0, getScreenHeight() - getScreenWidth()*1.3f);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }
    }
}
