package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;

public class ScaleAnimation extends AnimationSingle {
    private float from;
    private float to;
    private boolean hideAfter = false;

    public ScaleAnimation(Drawable mesh, int duration, int startIn) {
        super(mesh, duration, startIn);
    }

    public void setTo(float to) {
        this.to = to;
    }

    public void setHideAfter(boolean hideAfter) {
        this.hideAfter = hideAfter;
    }

    @Override
    void firstTick() {
        this.from = getSubject().getScale();
    }

    @Override
    void tick(double percentage) {
        getSubject().setScale((float) (from + (to - from) * percentage));
    }

    @Override
    void finalTick() {
        getSubject().setScale(to);

        if (hideAfter) {
            getSubject().setVisible(false);
        }
    }

    @Override
    ScaleAnimation reverse() {
        ScaleAnimation reversed = new ScaleAnimation(getSubject(), getDuration(), getDelay());
        reversed.setTo(from);
        return reversed;
    }
}
