package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.animation.TranslateAnimation;
import com.bytehamster.flowitgame.model.LevelPack;
import com.bytehamster.flowitgame.object.Container;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;
import com.bytehamster.flowitgame.util.ScrollHelper;

public class LevelPackSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelPackSelectState instance;
    private State nextState = this;

    private Plane pack1;
    private Plane pack2;
    private Plane pack3;
    private Plane pack4;
    private Plane selectLevelPackText;
    private Container container;
    private boolean pressed = false;
    private ScrollHelper scrollHelper;

    private LevelPackSelectState() {

    }

    public static LevelPackSelectState getInstance() {
        if (instance == null) {
            instance = new LevelPackSelectState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRenderer) {
        TextureCoordinates coordinatesLogo = TextureCoordinates.getFromBlocks(6, 10, 12, 12);
        selectLevelPackText = new Plane(0, glRenderer.getHeight(), glRenderer.getWidth(), glRenderer.getWidth() / 3, coordinatesLogo);
        selectLevelPackText.setVisible(false);

        float menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        float menuEntriesHeight = menuEntriesWidth / 6;

        container = new Container();
        scrollHelper = new ScrollHelper(container, false, false);

        TextureCoordinates coordinatesPack1 = TextureCoordinates.getFromBlocks(0, 5, 6, 6);
        pack1 = new Plane(-menuEntriesWidth, -2*menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack1);
        container.addDrawable(pack1);

        TextureCoordinates coordinatesPack2 = TextureCoordinates.getFromBlocks(0, 6, 6, 7);
        pack2 = new Plane(-menuEntriesWidth, pack1.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack2);
        container.addDrawable(pack2);

        TextureCoordinates coordinatesPack3 = TextureCoordinates.getFromBlocks(0, 7, 6, 8);
        pack3 = new Plane(-menuEntriesWidth, pack2.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack3);
        container.addDrawable(pack3);

        TextureCoordinates coordinatesPack4 = TextureCoordinates.getFromBlocks(6, 14, 12, 15);
        pack4 = new Plane(-menuEntriesWidth, pack3.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack4);
        container.addDrawable(pack4);

        glRenderer.addDrawable(container);
        glRenderer.addDrawable(selectLevelPackText);
    }

    @Override
    public void entry() {
        nextState = this;
        pressed = false;

        selectLevelPackText.cancelAnimations();
        selectLevelPackText.setY(getScreenHeight());
        selectLevelPackText.setVisible(true);
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelPackText, Animation.DURATION_LONG, Animation.DURATION_LONG);
        logoAnimation.setTo(0, getScreenHeight() - selectLevelPackText.getHeight());
        logoAnimation.start();

        container.setY(getScreenHeight() - selectLevelPackText.getHeight());
        scrollHelper.setMaxima(-Float.MAX_VALUE, -Float.MAX_VALUE, container.getY(), 0);

        AnimationFactory.startMenuAnimationEnter(pack1, (int) (3.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack2, (int) (3.5f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack3, (int) (4.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack4, (int) (4.5f * Animation.DURATION_SHORT));
    }

    @Override
    public void exit() {
        selectLevelPackText.cancelAnimations();
        TranslateAnimation logoAnimation = new TranslateAnimation(selectLevelPackText, Animation.DURATION_SHORT, 0);
        logoAnimation.setTo(0, getScreenHeight());
        logoAnimation.setHideAfter(true);
        logoAnimation.start();

        if (LevelSelectState.getInstance().getPack() == LevelPack.EASY) {
            AnimationFactory.startMenuAnimationOutPressed(pack1);
        } else {
            AnimationFactory.startMenuAnimationOut(pack1);
        }

        if (LevelSelectState.getInstance().getPack() == LevelPack.MEDIUM) {
            AnimationFactory.startMenuAnimationOutPressed(pack2);
        } else {
            AnimationFactory.startMenuAnimationOut(pack2);
        }

        if (LevelSelectState.getInstance().getPack() == LevelPack.HARD) {
            AnimationFactory.startMenuAnimationOutPressed(pack3);
        } else {
            AnimationFactory.startMenuAnimationOut(pack3);
        }

        if (LevelSelectState.getInstance().getPack() == LevelPack.COMMUNITY) {
            AnimationFactory.startMenuAnimationOutPressed(pack4);
        } else {
            AnimationFactory.startMenuAnimationOut(pack4);
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onBackPressed() {
        nextState = MainMenuState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressed = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP && !scrollHelper.isScrolling() && pressed) {
            if (pack1.collides(event.getX(), event.getY() + container.getY(), getScreenHeight())) {
                openSelectState(LevelPack.EASY);
            } else if (pack2.collides(event.getX(), event.getY() + container.getY(), getScreenHeight())) {
                openSelectState(LevelPack.MEDIUM);
            } else if (pack3.collides(event.getX(), event.getY() + container.getY(), getScreenHeight())) {
                openSelectState(LevelPack.HARD);
            } else if (pack4.collides(event.getX(), event.getY() + container.getY(), getScreenHeight())) {
                openSelectState(LevelPack.COMMUNITY);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            pressed = false;
        }
        scrollHelper.onTouchEvent(event);
    }

    private void openSelectState(LevelPack pack) {
        nextState = LevelSelectState.getInstance();
        LevelSelectState.getInstance().setPack(pack);
        playSound(R.raw.click);
    }
}
