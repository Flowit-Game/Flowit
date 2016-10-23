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
    private Plane solved;
    private AnimationRepeated rightButtonGlow;
    private boolean isFilling = false;
    private boolean won = false;
    private float topButtonSize;
    private LastLevelState lastLevelState = LastLevelState.NO_LEVEL;

    private enum LastLevelState {
        SOLVED, NO_LEVEL, NOT_SOLVED
    }

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
        topButtonSize = glRenderer.getWidth() / 7.5f;
        left = ObjectFactory.createSingleBox(0, 10, topButtonSize);
        left.setX(topButtonSize * 0.5f);
        left.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        left.setVisible(false);
        left.setScale(0);
        glRenderer.addDrawable(left);

        right = ObjectFactory.createSingleBox(1, 10, topButtonSize);
        right.setX(topButtonSize * 6f);
        right.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        right.setVisible(false);
        right.setScale(0);
        glRenderer.addDrawable(right);

        restart = ObjectFactory.createSingleBox(2, 10, topButtonSize);
        restart.setX(topButtonSize * 4.5f);
        restart.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        restart.setVisible(false);
        restart.setScale(0);
        glRenderer.addDrawable(restart);

        TextureCoordinates coordinatesSolved = TextureCoordinates.getFromBlocks(3, 10, 5, 11);
        solved = new Plane(0, 0, 2*topButtonSize, topButtonSize, coordinatesSolved);
        solved.setX(topButtonSize * 2f);
        solved.setY(glRenderer.getHeight() - topButtonSize * 1.5f);
        solved.setVisible(false);
        solved.setScale(0);
        glRenderer.addDrawable(solved);

        levelDrawer.setVisible(false);
        levelDrawer.setScreenWidth(getScreenWidth());
        levelDrawer.initialize();
        levelDrawer.setX(0);
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

        right.setScale(1); // To generate correctly repeated animation at this point
        ScaleAnimation rightAnimation = new ScaleAnimation(right, Animation.DURATION_LONG, 0);
        rightAnimation.setTo(1.2f);
        rightButtonGlow = new AnimationRepeated(rightAnimation);
        right.setScale(0);
    }

    @Override
    public void entry() {
        nextState = this;
        lastLevelState = LastLevelState.NO_LEVEL;
        reloadLevel();

        levelDrawer.cancelAnimations();
        levelDrawer.setVisible(true);
        levelDrawer.setY(-levelDrawer.getBoxSize());
        TranslateAnimation drawerAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        drawerAnimation.setTo(0, getScreenHeight() - boardStartY);
        drawerAnimation.start();

        AnimationFactory.startScaleShow(left);
        AnimationFactory.startScaleShow(right);
        AnimationFactory.startScaleShow(restart);
    }

    private void reloadLevel() {
        rightButtonGlow.stopWhenFinished();
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
                TranslateAnimation inAnimation;
                if (lastLevelState == LastLevelState.NO_LEVEL) {
                    inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_LONG, Animation.DURATION_LONG);
                } else {
                    inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
                }
                inAnimation.setTo(0, (availableSpace-lockedMessage.getHeight())/2 + getAdHeight());
                inAnimation.start();
            }
        } else {
            TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }

        if (isSolved(level)) {
            if (lastLevelState == LastLevelState.NO_LEVEL) {
                // solved.setScale(1); // Destroys in animation
                solved.setVisible(true);
                restart.setX(topButtonSize * 4.5f);
                AnimationFactory.startScaleShow(solved);
            } else if (lastLevelState == LastLevelState.NOT_SOLVED) {
                showSolved(Animation.DURATION_SHORT/2);
            }
            lastLevelState = LastLevelState.SOLVED;
        } else {
            if (!isSolved(level) && lastLevelState == LastLevelState.NO_LEVEL) {
                solved.setVisible(false);
                restart.setX(topButtonSize * 3.25f);
            } else if (!isSolved(level) && lastLevelState == LastLevelState.SOLVED) {
                hideSolved();
            }
            lastLevelState = LastLevelState.NOT_SOLVED;
        }
    }

    @Override
    public void exit() {
        rightButtonGlow.pause();

        levelDrawer.cancelAnimations();
        TranslateAnimation logoAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setTo(0, -levelDrawer.getBoxSize());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        AnimationFactory.startScaleHide(left);
        AnimationFactory.startScaleHide(right);
        AnimationFactory.startScaleHide(restart);
        AnimationFactory.startScaleHide(solved);

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
    public void onBackPressed() {
        nextState = LevelSelectState.getInstance();
        playSound(R.raw.click);
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
            lastLevelState = LastLevelState.SOLVED;

            float availableSpace = getScreenHeight() - getAdHeight();
            winMessage.setY(-getScreenWidth() * 0.5f);
            winMessage.setVisible(true);
            TranslateAnimation inAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0);
            inAnimation.setTo(0, (availableSpace-winMessage.getHeight())/2 + getAdHeight());
            inAnimation.start();

            TranslateAnimation outAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 5 * Animation.DURATION_SHORT);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();

            if (!solved.isVisible()) {
                showSolved(Animation.DURATION_LONG);
            }

            right.addAnimation(rightButtonGlow);
            rightButtonGlow.start();
        }
    }

    private void showSolved(int speed) {
        solved.setScale(0);
        solved.setVisible(true);
        ScaleAnimation leftAnimation = new ScaleAnimation(solved, speed, 0);
        leftAnimation.setTo(1);
        leftAnimation.start();

        TranslateAnimation rightAnimation = new TranslateAnimation(restart, speed, 0);
        rightAnimation.setTo(4.5f * topButtonSize, getScreenHeight() - 1.5f * topButtonSize);
        rightAnimation.setHideAfter(false);
        rightAnimation.start();
    }

    private void hideSolved() {
        ScaleAnimation leftAnimation = new ScaleAnimation(solved, Animation.DURATION_SHORT/2, 0);
        leftAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        leftAnimation.start();

        restart.cancelAnimations();
        TranslateAnimation rightAnimation = new TranslateAnimation(restart, Animation.DURATION_SHORT/2, 0);
        rightAnimation.setTo(3.25f * topButtonSize, getScreenHeight() - 1.5f * topButtonSize);
        rightAnimation.setHideAfter(false);
        rightAnimation.start();
    }
}
