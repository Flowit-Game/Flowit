package com.bytehamster.flowit.state;

import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.bytehamster.flowit.GLRenderer;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.animation.Animation;
import com.bytehamster.flowit.animation.AnimationFactory;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

public class LevelPackSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelPackSelectState instance;
    private State nextState = this;

    private Plane pack1;
    private Plane pack2;
    private Plane pack3;

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
        float logoHeight = glRenderer.getWidth() / 3;
        float menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        float menuEntriesHeight = menuEntriesWidth / 6;
        float menuEntriesAvailableSpace = getScreenHeight() - getAdHeight() - logoHeight;
        float menuEntriesStartY = getScreenHeight() - logoHeight - (menuEntriesAvailableSpace - 4 * menuEntriesHeight) / 2;

        TextureCoordinates coordinatesPack1 = TextureCoordinates.getFromBlocks(0, 5, 6, 6);
        pack1 = new Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesPack1);
        glRenderer.addDrawable(pack1);

        TextureCoordinates coordinatesPack2 = TextureCoordinates.getFromBlocks(0, 6, 6, 7);
        pack2 = new Plane(-menuEntriesWidth, pack1.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack2);
        glRenderer.addDrawable(pack2);

        TextureCoordinates coordinatesPack3 = TextureCoordinates.getFromBlocks(0, 7, 6, 8);
        pack3 = new Plane(-menuEntriesWidth, pack2.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack3);
        glRenderer.addDrawable(pack3);
    }

    @Override
    public void entry() {
        nextState = this;
        AnimationFactory.startMenuAnimationEnter(pack1, 3 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(pack2, 4 * Animation.DURATION_SHORT);
        AnimationFactory.startMenuAnimationEnter(pack3, 5 * Animation.DURATION_SHORT);
    }

    @Override
    public void exit() {
        if (LevelSelectState.getInstance().getPack() == 1) {
            AnimationFactory.startMenuAnimationOutPressed(pack1);
        } else {
            AnimationFactory.startMenuAnimationOut(pack1);
        }

        if (LevelSelectState.getInstance().getPack() == 2) {
            AnimationFactory.startMenuAnimationOutPressed(pack2);
        } else {
            AnimationFactory.startMenuAnimationOut(pack2);
        }

        if (LevelSelectState.getInstance().getPack() == 3) {
            AnimationFactory.startMenuAnimationOutPressed(pack3);
        } else {
            AnimationFactory.startMenuAnimationOut(pack3);
        }
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            nextState = MainMenuState.getInstance();
            playSound(R.raw.click);
        }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (pack1.collides(event, getScreenHeight())) {
                nextState = LevelSelectState.getInstance();
                LevelSelectState.getInstance().setPack(1);
                playSound(R.raw.click);
            } else if (pack2.collides(event, getScreenHeight())) {
                nextState = LevelSelectState.getInstance();
                LevelSelectState.getInstance().setPack(2);
                playSound(R.raw.click);
            } else if (pack3.collides(event, getScreenHeight())) {
                nextState = LevelSelectState.getInstance();
                LevelSelectState.getInstance().setPack(3);
                playSound(R.raw.click);
            }
        }
    }
}
