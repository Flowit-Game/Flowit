package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;

public class LevelPackSelectState extends State {
    @SuppressLint("StaticFieldLeak")
    private static LevelPackSelectState instance;
    private State nextState = this;

    private Plane pack1;
    private Plane pack2;
    private Plane pack3;
    private Plane pack4;

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
        float menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        float menuEntriesHeight = menuEntriesWidth / 6;
        float menuEntriesAvailableSpace = getScreenHeight() - getAdHeight();
        float menuEntriesStartY = getScreenHeight() - (menuEntriesAvailableSpace - 6 * menuEntriesHeight) / 2;

        TextureCoordinates coordinatesPack1 = TextureCoordinates.getFromBlocks(0, 5, 6, 6);
        pack1 = new Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesPack1);
        glRenderer.addDrawable(pack1);

        TextureCoordinates coordinatesPack4 = TextureCoordinates.getFromBlocks(6, 10, 12, 11);
        pack4 = new Plane(-menuEntriesWidth, pack1.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack4);
        glRenderer.addDrawable(pack4);

        TextureCoordinates coordinatesPack2 = TextureCoordinates.getFromBlocks(0, 6, 6, 7);
        pack2 = new Plane(-menuEntriesWidth, pack4.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack2);
        glRenderer.addDrawable(pack2);

        TextureCoordinates coordinatesPack3 = TextureCoordinates.getFromBlocks(0, 7, 6, 8);
        pack3 = new Plane(-menuEntriesWidth, pack2.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesPack3);
        glRenderer.addDrawable(pack3);

    }

    @Override
    public void entry() {
        nextState = this;
        AnimationFactory.startMenuAnimationEnter(pack1, (int) (3.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack4, (int) (3.5f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack2, (int) (4.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(pack3, (int) (4.5f * Animation.DURATION_SHORT));
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

        if (LevelSelectState.getInstance().getPack() == 4) {
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
            if (pack1.collides(event, getScreenHeight())) {
                openSelectState(1);
            } else if (pack2.collides(event, getScreenHeight())) {
                openSelectState(2);
            } else if (pack3.collides(event, getScreenHeight())) {
                openSelectState(3);
            } else if (pack4.collides(event, getScreenHeight())) {
                openSelectState(4);
            }
        }
    }

    private void openSelectState(int pack) {
        nextState = LevelSelectState.getInstance();
        LevelSelectState.getInstance().setPack(pack);
        LevelSelectState.getInstance().resetScrollPosition();
        playSound(R.raw.click);
    }
}
