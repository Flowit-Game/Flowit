package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.object.LevelList;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;
import com.bytehamster.flowitgame.util.ScrollHelper;

public class LevelSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelSelectState instance;
    private State nextState = this;

    private int pack = 1;
    private Plane selectLevelText;
    private LevelList levelList;
    private ScrollHelper scrollHelper;

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

        float boxSize = getScreenWidth() / (5 + 2 + 2);
        levelList = new LevelList(boxSize);
        scrollHelper = new ScrollHelper(levelList, false, true);

        glRenderer.addDrawable(levelList);
        glRenderer.addDrawable(selectLevelText);
    }

    @Override
    public void entry() {
        nextState = this;

        float boxSize = getScreenWidth() / (5 + 2 + 2);
        float availableSpace = getScreenHeight() - getAdHeight() - selectLevelText.getHeight();
        float boxStart = getScreenHeight() - selectLevelText.getHeight() - (availableSpace - boxSize * 6.5f) / 2;
        levelList.setY(boxStart);

        selectLevelText.cancelAnimations();
        selectLevelText.setY(getScreenHeight());
        selectLevelText.setVisible(true);
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        logoAnimation.setTo(0, getScreenHeight() - selectLevelText.getHeight());
        logoAnimation.start();

        levelList.entry(pack, this);
    }

    @Override
    public void exit() {
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelText, Animation.DURATION_SHORT, 0);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        levelList.exit();
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
        if (event.getAction() == MotionEvent.ACTION_UP && !scrollHelper.isScrolling()) {
            if (levelList.collides(event, getScreenHeight())) {
                GameState.getInstance().setLevel((pack - 1) * 25 + levelList.getCollision(event, getScreenHeight()));
                nextState = GameState.getInstance();
                playSound(R.raw.click);
            }
        }
        scrollHelper.onTouchEvent(event);
    }

    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }
}
