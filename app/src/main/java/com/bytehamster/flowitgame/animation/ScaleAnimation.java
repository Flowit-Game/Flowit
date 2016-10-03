package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;

public class ScaleAnimation extends AnimationSingle {
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

    @Override
    ScaleAnimation reverse() {
        ScaleAnimation reversed = new ScaleAnimation(getSubject(), getSteps()*STEP_DELAY, getStartIn());
        reversed.setFrom(to);
        reversed.setTo(from);
        return reversed;
    }
}
