package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Drawable;

public class TranslateAnimation extends Animation {
    private final int steps;

    private float fromX;
    private float fromY;
    private float toX;
    private float toY;
    private boolean hideAfter = false;

    public TranslateAnimation(Drawable mesh, int duration, int startIn) {
        super(mesh, duration / STEP_DELAY, startIn);
        steps = duration / STEP_DELAY;
    }

    public void setFrom(float x, float y) {
        this.fromX = x;
        this.fromY = y;
    }

    public void setTo(float x, float y) {
        this.toX = x;
        this.toY = y;
    }

    public void setHideAfter(boolean hideAfter) {
        this.hideAfter = hideAfter;
    }

    @Override
    void doStep(int step) {
        float stepX = (toX - fromX) / (float) steps;
        float stepY = (toY - fromY) / (float) steps;

        getSubject().setX(fromX + stepX * (float) step);
        getSubject().setY(fromY + stepY * (float) step);
    }

    @Override
    void finalStep() {
        getSubject().setX(toX);
        getSubject().setY(toY);

        if (hideAfter) {
            getSubject().setVisible(false);
        }
    }
}
