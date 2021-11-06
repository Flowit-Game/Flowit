package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.BuildConfig;
import com.bytehamster.flowitgame.Converter;
import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.animation.AnimationRepeated;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.filler.Filler;
import com.bytehamster.flowitgame.model.Field;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.Modifier;
import com.bytehamster.flowitgame.object.LevelDrawer;
import com.bytehamster.flowitgame.object.Number;
import com.bytehamster.flowitgame.object.ObjectFactory;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;

public class GameState extends State {
    @SuppressLint("StaticFieldLeak")
    private static GameState instance;
    private State nextState = this;

    private Level level;
    private float boardStartY = 0;
    private final LevelDrawer levelDrawer = LevelDrawer.getInstance();
    private Plane winMessage;
    private Plane lockedMessage;
    private Plane left;
    private Plane right;
    private Plane restart;
    private Plane stepsLabel;
    private Plane stepsImproved;
    private Plane solved;
    private Plane headerBackground;
    private Number stepsUsed;
    private Number stepsBest;
    private boolean isFilling = false;
    private boolean won = false;
    private float topBarHeight;
    private float topButtonSize;
    private float topButtonY;
    private float topBarPadding;
    private float stepsUsedCurrentYDelta;
    private float stepsUsedBestYDelta;
    private LastLevelState lastLevelState = LastLevelState.NO_LEVEL;
    private Filler filler;

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
        topBarHeight = glRenderer.getWidth() / (8 * 0.6f + 6 * 0.2f);
        topButtonSize = 0.6f * topBarHeight;
        topBarPadding = 0.2f * topBarHeight;
        topButtonY = glRenderer.getHeight() - topButtonSize - topBarPadding;
        stepsUsedCurrentYDelta = topButtonSize * 0.6f;
        stepsUsedBestYDelta = topButtonSize * 0.1f;

        TextureCoordinates coordinatesHeader = TextureCoordinates.getFromBlocks(2, 15, 3, 16);
        headerBackground = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), topBarHeight, coordinatesHeader);
        headerBackground.setVisible(false);
        glRenderer.addDrawable(headerBackground);

        left = ObjectFactory.createSingleBox(0, 10, topButtonSize);
        left.setX(topBarPadding);
        left.setY(glRenderer.getHeight() + topBarPadding);
        left.setVisible(false);
        glRenderer.addDrawable(left);

        right = ObjectFactory.createSingleBox(1, 10, topButtonSize);
        right.setX(glRenderer.getWidth() - topBarPadding - topButtonSize);
        right.setY(glRenderer.getHeight() + topBarPadding);
        right.setVisible(false);
        glRenderer.addDrawable(right);

        restart = ObjectFactory.createSingleBox(2, 10, topButtonSize);
        restart.setX(topButtonSize + 2 * topBarPadding);
        restart.setY(glRenderer.getHeight() + topBarPadding);
        restart.setVisible(false);
        glRenderer.addDrawable(restart);

        stepsImproved = ObjectFactory.createSingleBox(4, 10, topBarHeight);
        stepsImproved.setX(5 * topButtonSize + 1.5f * topBarPadding);
        stepsImproved.setY(getScreenHeight() - topBarHeight);
        stepsImproved.setVisible(false);
        stepsImproved.setScale(0);
        glRenderer.addDrawable(stepsImproved);

        stepsUsed = new Number();
        stepsUsed.setFontSize(topButtonSize * 0.35f);
        stepsUsed.setX(5 * topButtonSize + 3 * topBarPadding);
        stepsUsed.setY(glRenderer.getHeight() + topBarPadding + stepsUsedCurrentYDelta);
        glRenderer.addDrawable(stepsUsed);

        stepsBest = new Number();
        stepsBest.setFontSize(topButtonSize * 0.35f);
        stepsBest.setX(5 * topButtonSize + 3 * topBarPadding);
        stepsBest.setY(glRenderer.getHeight() + topBarPadding + stepsUsedBestYDelta);
        glRenderer.addDrawable(stepsBest);

        TextureCoordinates coordinateSteps = TextureCoordinates.getFromBlocks(12, 10, 15, 11);
        stepsLabel = new Plane(0, 0, 3 * topButtonSize, topButtonSize, coordinateSteps);
        stepsLabel.setX(2 * topButtonSize + 3 * topBarPadding);
        stepsLabel.setY(glRenderer.getHeight() + topBarPadding);
        stepsLabel.setVisible(false);
        glRenderer.addDrawable(stepsLabel);

        solved = ObjectFactory.createSingleBox(3, 10, topButtonSize);
        solved.setX(6 * topButtonSize + 4 * topBarPadding);
        solved.setY(glRenderer.getHeight() + topBarPadding);
        solved.setVisible(false);
        glRenderer.addDrawable(solved);

        levelDrawer.setVisible(false);
        levelDrawer.setScreenWidth(getScreenWidth());
        levelDrawer.setX(0);
        glRenderer.addDrawable(levelDrawer);

        TextureCoordinates coordinatesWin = TextureCoordinates.getFromBlocks(0, 8, 6, 10);
        winMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesWin);
        winMessage.setVisible(false);
        glRenderer.addDrawable(winMessage);

        TextureCoordinates coordinatesLocked = TextureCoordinates.getFromBlocks(0, 13, 6, 15);
        lockedMessage = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLocked);
        lockedMessage.setVisible(false);
        lockedMessage.setY(-getScreenWidth() * 0.5f);
        glRenderer.addDrawable(lockedMessage);

        ScaleAnimation rightAnimation = new ScaleAnimation(right, Animation.DURATION_LONG, 0);
        rightAnimation.setTo(1.2f);
    }

    @Override
    public void entry() {
        nextState = this;
        lastLevelState = LastLevelState.NO_LEVEL;
        reloadLevel();

        AnimationFactory.startMoveYTo(left, topButtonY);
        AnimationFactory.startMoveYTo(right, topButtonY);
        AnimationFactory.startMoveYTo(restart, topButtonY);
        AnimationFactory.startMoveYTo(stepsLabel, topButtonY);
        AnimationFactory.startMoveYTo(stepsBest, topButtonY + stepsUsedBestYDelta);
        AnimationFactory.startMoveYTo(stepsUsed, topButtonY + stepsUsedCurrentYDelta);
        AnimationFactory.startMoveYTo(headerBackground, getScreenHeight() - topBarHeight);
    }

    private void reloadLevel() {
        won = false;
        stepsUsed.setValue(0);
        if (loadSteps(level.getNumber()) == STEPS_NOT_SOLVED) {
            stepsBest.setValue(Number.VALUE_NAN);
        } else {
            stepsBest.setValue(loadSteps(level.getNumber()));
        }
        AnimationFactory.startScaleHide(stepsImproved, 0);
        isFilling = false;
        level.reset();
        levelDrawer.setLevel(level);

        float remainingSpace = getScreenHeight() - topBarHeight - levelDrawer.getHeight();
        final float horizontalPaddingDelta = levelDrawer.getBoxSize() / 2;
        float horizontalPadding = horizontalPaddingDelta;
        while (remainingSpace < 0) {
            levelDrawer.setScreenWidth(getScreenWidth() - 2 * horizontalPadding);
            levelDrawer.setX(horizontalPadding);
            remainingSpace = getScreenHeight() - topBarHeight - levelDrawer.getHeight();
            horizontalPadding += horizontalPaddingDelta;
        }
        boardStartY = topBarHeight + remainingSpace / 2;

        if (levelDrawer.getY() != getScreenHeight() - boardStartY) {
            levelDrawer.cancelAnimations();
            levelDrawer.setVisible(true);
            TranslateAnimation drawerAnimation;
            if (lastLevelState == LastLevelState.NO_LEVEL) {
                levelDrawer.setY(-levelDrawer.getBoxSize());
                drawerAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
            } else {
                drawerAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_SHORT, 0);
            }
            drawerAnimation.setTo(levelDrawer.getX(), getScreenHeight() - boardStartY);
            drawerAnimation.start();
        }

        if (!isPlayable(level)) {
            float availableSpace = getScreenHeight();
            lockedMessage.cancelAnimations();
            lockedMessage.setVisible(true);
            TranslateAnimation inAnimation;
            if (lastLevelState == LastLevelState.NO_LEVEL) {
                inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_LONG, Animation.DURATION_LONG);
            } else {
                inAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
            }
            inAnimation.setTo(0, (availableSpace-lockedMessage.getHeight())/2);
            inAnimation.start();
        } else {
            TranslateAnimation outAnimation = new TranslateAnimation(lockedMessage, Animation.DURATION_SHORT, 0);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }

        if (isSolved(level.getNumber())) {
            if (lastLevelState == LastLevelState.NO_LEVEL) {
                solved.setScale(1);
                AnimationFactory.startMoveYTo(solved, topButtonY);
            } else if (lastLevelState == LastLevelState.NOT_SOLVED) {
                showSolved(Animation.DURATION_SHORT / 2);
            }
            lastLevelState = LastLevelState.SOLVED;
        } else {
            if (!isSolved(level.getNumber()) && lastLevelState == LastLevelState.NO_LEVEL) {
                solved.setVisible(false);
            } else if (!isSolved(level.getNumber()) && lastLevelState == LastLevelState.SOLVED) {
                hideSolved();
            }
            lastLevelState = LastLevelState.NOT_SOLVED;
        }

        if (winMessage.isVisible()) {
            winMessage.cancelAnimations();
            TranslateAnimation outAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0);
            outAnimation.setTo(0, -getScreenWidth() * 0.5f);
            outAnimation.setHideAfter(true);
            outAnimation.start();
        }
    }

    @Override
    public void exit() {
        levelDrawer.cancelAnimations();
        TranslateAnimation logoAnimation = new TranslateAnimation(levelDrawer, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setTo(levelDrawer.getX(), -levelDrawer.getBoxSize());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        AnimationFactory.startMoveYTo(left, getScreenHeight() + topBarPadding);
        AnimationFactory.startMoveYTo(right, getScreenHeight() + topBarPadding);
        AnimationFactory.startMoveYTo(restart, getScreenHeight() + topBarPadding);
        AnimationFactory.startMoveYTo(solved, getScreenHeight() + topBarPadding);
        AnimationFactory.startMoveYTo(stepsLabel, getScreenHeight() + topBarPadding);
        AnimationFactory.startMoveYTo(stepsBest, getScreenHeight() + topBarPadding + stepsUsedBestYDelta);
        AnimationFactory.startMoveYTo(stepsUsed, getScreenHeight() + topBarPadding + stepsUsedCurrentYDelta);
        AnimationFactory.startMoveYTo(headerBackground, getScreenHeight());
        AnimationFactory.startMoveYTo(winMessage, -getScreenWidth() * 0.5f);
        AnimationFactory.startScaleHide(stepsImproved, 0);

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
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return;
        }

        if (left.collides(event, getScreenHeight())) {
            playSound(R.raw.click);
            if (level.getIndexInPack() == 0) {
                nextState = LevelSelectState.getInstance();
            } else {
                level = level.getPack().getLevel(level.getIndexInPack() - 1);
                reloadLevel();
            }
        } else if (right.collides(event, getScreenHeight())
                || winMessage.collides(event, getScreenHeight())) {
            playSound(R.raw.click);
            if (level.getPack().size() == level.getIndexInPack() + 1) {
                nextState = LevelSelectState.getInstance();
            } else {
                level = level.getPack().getLevel(level.getIndexInPack() + 1);
                reloadLevel();
            }
        } else if (restart.collides(event, getScreenHeight())) {
            playSound(R.raw.click);
            if (BuildConfig.DEBUG_LEVELS) {
                makeUnPlayed(level.getNumber());
            }
            if (stepsUsed.getValue() != 0) {
                wiggle();
            }
            if (isFilling) {
                filler.setOnFinished(this::reloadLevel);
            } else {
                reloadLevel();
            }
        } else if (!isFilling && !won && isPlayable(level)) {
            checkFieldTouched(event);
        }
    }

    private void wiggle() {
        new ScaleAnimation(levelDrawer, Animation.DURATION_SHORT/2, 0)
                .setTo(0.95f).start();
        new ScaleAnimation(levelDrawer, Animation.DURATION_SHORT/2, Animation.DURATION_SHORT/2)
                .setTo(1f).start();
    }

    private void checkFieldTouched(MotionEvent event) {
        for (int row = 0; row < level.getHeight(); row++) {
            for (int col = 0; col < level.getWidth(); col++) {
                if (event.getY() > boardStartY + row * levelDrawer.getBoxSize()
                        && event.getY() < boardStartY + (row + 1) * levelDrawer.getBoxSize()
                        && event.getX() > levelDrawer.getX() + (col + 0.5) * levelDrawer.getBoxSize()
                        && event.getX() < levelDrawer.getX() + (col + 1.5) * levelDrawer.getBoxSize()) {

                    triggerField(col, row);
                }
            }
        }
    }

    private void triggerField(final int col, final int row) {
        filler = Filler.get(level, col, row, this);
        if (filler != null) {
            stepsUsed.increment();
            playSound(R.raw.click);
            isFilling = true;
            if (level.fieldAt(col, row).getModifier().isRotating()) {
                Modifier rotated = level.fieldAt(col, row).getModifier().rotate();
                level.fieldAt(col, row).setModifier(rotated);
            }
            filler.setOnFinished(() -> {
                isFilling = false;
                checkWon();
            });
            filler.fill();
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    private void checkWon() {
        won = true;
        for (int r = 0; r < level.getHeight(); r++) {
            for (int c = 0; c < level.getWidth(); c++) {
                Field f = level.fieldAt(c, r);
                if (Converter.convertColor(f.getModifier()) != null // Is not a color
                        && f.getColor() != Converter.convertColor(f.getModifier())) {
                    won = false;
                }
            }
        }

        if (won) {
            playSound(R.raw.won);
            makePlayed(level.getNumber());
            saveSteps(level.getNumber(), stepsUsed.getValue());
            lastLevelState = LastLevelState.SOLVED;

            float availableSpace = getScreenHeight();
            winMessage.setY(-getScreenWidth() * 0.5f);
            winMessage.setVisible(true);
            TranslateAnimation inAnimation = new TranslateAnimation(winMessage, Animation.DURATION_SHORT, 0);
            inAnimation.setTo(0, (availableSpace - winMessage.getHeight()) / 2);
            inAnimation.start();

            if (!solved.isVisible()) {
                showSolved(Animation.DURATION_LONG);
            }

            if (stepsUsed.getValue() < stepsBest.getValue() && stepsBest.getValue() < STEPS_NOT_SOLVED) {
                AnimationFactory.startScaleShow(stepsImproved, 0);
            }
        }
    }

    private void showSolved(int speed) {
        solved.cancelAnimations();
        solved.setScale(0);
        solved.setY(topButtonY);
        solved.setVisible(true);
        ScaleAnimation leftAnimation = new ScaleAnimation(solved, speed, 0);
        leftAnimation.setTo(1);
        leftAnimation.start();
    }

    private void hideSolved() {
        ScaleAnimation leftAnimation = new ScaleAnimation(solved, Animation.DURATION_SHORT / 2, 0);
        leftAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        leftAnimation.start();
    }
}
