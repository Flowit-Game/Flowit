package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.Converter;
import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.animation.AnimationRepeated;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.filler.BombFiller;
import com.bytehamster.flowitgame.filler.DirectionFiller;
import com.bytehamster.flowitgame.filler.FloodFiller;
import com.bytehamster.flowitgame.model.Field;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.Modifier;
import com.bytehamster.flowitgame.object.LevelDrawer;
import com.bytehamster.flowitgame.object.ObjectFactory;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;

public class GameState extends State {
    @SuppressLint("StaticFieldLeak")
    private static GameState instance;
    private State nextState = this;

    private int level = 0;
    private Level levelData = null;
    private float boardStartY = 0;
    private int stepsUsed = 0;
    private final LevelDrawer levelDrawer = LevelDrawer.getInstance();
    private Plane winMessage;
    private Plane lockedMessage;
    private Plane left;
    private Plane right;
    private Plane restart;
    private AnimationRepeated rightButtonGlow;
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
        float topButtonSize = glRenderer.getWidth() / 8f;
        left = ObjectFactory.createSingleBox(0, 10, topButtonSize);
        left.setX(topButtonSize * 0.5f);
        left.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        left.setVisible(false);
        glRenderer.addDrawable(left);

        right = ObjectFactory.createSingleBox(1, 10, topButtonSize);
        right.setX(glRenderer.getWidth() - topButtonSize * 1.5f);
        right.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        right.setVisible(false);
        glRenderer.addDrawable(right);

        restart = ObjectFactory.createSingleBox(2, 10, topButtonSize);
        restart.setX((glRenderer.getWidth() - topButtonSize) / 2);
        restart.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        restart.setVisible(false);
        glRenderer.addDrawable(restart);

        levelDrawer.setVisible(false);
        levelDrawer.setScreenHeight(getScreenHeight());
        levelDrawer.setScreenWidth(getScreenWidth());
        levelDrawer.initialize();
        glRenderer.addDrawable(levelDrawer);
        boardStartY = 2 * topButtonSize + (getScreenHeight() - getAdHeight() - levelDrawer.getHeight())/2;

        TextureCoordinates coordinatesWin = TextureCoordinates.getFromBlocks(0, 8, 6, 10);
        winMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesWin);
        winMessage.setVisible(false);
        glRenderer.addDrawable(winMessage);

        TextureCoordinates coordinatesLocked = TextureCoordinates.getFromBlocks(0, 13, 6, 15);
        lockedMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLocked);
        lockedMessage.setVisible(false);
        glRenderer.addDrawable(lockedMessage);

        ScaleAnimation leftAnimation = new ScaleAnimation(right, Animation.DURATION_SHORT, 0);
        leftAnimation.setFrom(1);
        leftAnimation.setTo(1.2f);
        rightButtonGlow = new AnimationRepeated(leftAnimation);
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

        AnimationFactory.startScaleShow(left);
        AnimationFactory.startScaleShow(right);
        AnimationFactory.startScaleShow(restart);
    }

    private void reloadLevel() {
        rightButtonGlow.stop();
        won = false;
        stepsUsed = 0;
        isFilling = false;
        levelData = new Level(level, getActivity());
        levelDrawer.setLevel(levelData);

        if (!isPlayable(level)) {
            if (!lockedMessage.isVisible()) {
                float availableSpace = getScreenHeight() - getAdHeight();
                lockedMessage.setY(-getScreenWidth() * 0.5f);
                lockedMessage.setVisible(true);
                TranslateAnimation inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, Animation.DURATION_SHORT);
                inAnimation.setTo(0, (availableSpace-lockedMessage.getHeight())/2 + getAdHeight());
                inAnimation.start();
            }
        } else {
            TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }
    }

    @Override
    public void exit() {
        rightButtonGlow.stop();

        TranslateAnimation logoAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setFrom(0, getScreenHeight() - boardStartY);
        logoAnimation.setTo(0, -levelDrawer.getBoxSize());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        AnimationFactory.startScaleHide(left);
        AnimationFactory.startScaleHide(right);
        AnimationFactory.startScaleHide(restart);

        TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
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

            if (left.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                if (level % 25 == 0) {
                    nextState = LevelSelectState.getInstance();
                } else {
                    level--;
                    reloadLevel();
                }
            } else if (right.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                level++;
                if (level % 25 == 0) {
                    nextState = LevelSelectState.getInstance();
                } else {
                    reloadLevel();
                }
            } else if (restart.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                reloadLevel();
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

                        triggerField(col, row);
                    }
                }
            }
        }
    }

    private void triggerField(int col, int row) {
        stepsUsed++; // Subtracting one in default case (=> no action was done)
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
                doFill(col, row, 0, -1);
                break;
            case RIGHT:
                doFill(col, row, 1, 0);
                break;
            case LEFT:
                doFill(col, row, -1, 0);
                break;
            case DOWN:
                doFill(col, row, 0, 1);
                break;
            case ROTATE_UP:
                doFill(col, row, 0, -1);
                levelData.fieldAt(col, row).setModifier(Modifier.ROTATE_RIGHT);
                break;
            case ROTATE_RIGHT:
                doFill(col, row, 1, 0);
                levelData.fieldAt(col, row).setModifier(Modifier.ROTATE_DOWN);
                break;
            case ROTATE_LEFT:
                doFill(col, row, -1, 0);
                levelData.fieldAt(col, row).setModifier(Modifier.ROTATE_UP);
                break;
            case ROTATE_DOWN:
                doFill(col, row, 0, 1);
                levelData.fieldAt(col, row).setModifier(Modifier.ROTATE_LEFT);
                break;
            default:
                // No action was done
                stepsUsed--;
                break;
        }
    }

    private void doFill(int col, int row, int dx, int dy) {
        playSound(R.raw.click);
        isFilling = true;
        new DirectionFiller(levelData, this, new Runnable() {
            @Override
            public void run() {
                isFilling = false;
                checkWon();
            }
        }, dx, dy).fill(col, row);
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
            saveSteps(level, stepsUsed);

            float availableSpace = getScreenHeight() - getAdHeight();
            winMessage.setY(-getScreenWidth() * 0.5f);
            winMessage.setVisible(true);
            TranslateAnimation inAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0);
            inAnimation.setTo(0, (availableSpace-winMessage.getHeight())/2 + getAdHeight());
            inAnimation.start();

            TranslateAnimation outAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 5 * Animation.DURATION_SHORT);
            outAnimation.setFrom(0, (availableSpace-winMessage.getHeight())/2 + getAdHeight());
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();

            rightButtonGlow.start();
        }
    }
}
