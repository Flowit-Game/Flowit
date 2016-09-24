package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Drawable;

public class ScaleAnimation extends Animation {
    private final int steps;

    private float from;
    private float to;
    private boolean hideAfter = false;

    public ScaleAnimation(Drawable mesh, int duration, int startIn) {
        super(mesh, duration / STEP_DELAY, startIn);
        steps = duration / STEP_DELAY;
        setFrom(mesh.getScale());
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public void setHideAfter(boolean hideAfter) {
        this.hideAfter = hideAfter;
    }

    @Override
    void doStep(int step) {
        float perStep = (to - from) / (float) steps;

        getSubject().setScale(from + perStep * (float) step);
    }

    @Override
    void finalStep() {
        getSubject().setScale(to);

        if (hideAfter) {
            getSubject().setVisible(false);
        }
    }
}
