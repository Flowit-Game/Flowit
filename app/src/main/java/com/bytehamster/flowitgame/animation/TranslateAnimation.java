package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;

public class TranslateAnimation extends AnimationSingle {
    private float fromX;
    private float fromY;
    private float toX;
    private float toY;
    private boolean hideAfter = false;

    public TranslateAnimation(Drawable mesh, int duration, int startIn) {
        super(mesh, duration, startIn);
    }

    public void setTo(float x, float y) {
        this.toX = x;
        this.toY = y;
    }

    public void setHideAfter(boolean hideAfter) {
        this.hideAfter = hideAfter;
    }

    @Override
    void tick(double percentage) {
        getSubject().setX((float) (fromX + (toX - fromX) * percentage));
        getSubject().setY((float) (fromY + (toY - fromY) * percentage));
    }

    @Override
    void firstTick() {
        this.fromX = getSubject().getX();
        this.fromY = getSubject().getY();
    }

    @Override
    void finalTick() {
        getSubject().setX(toX);
        getSubject().setY(toY);

        if (hideAfter) {
            getSubject().setVisible(false);
        }
    }

    @Override
    TranslateAnimation reverse() {
        TranslateAnimation reversed = new TranslateAnimation(getSubject(), getDuration(), getDelay());
        reversed.setTo(fromX, fromY);
        return reversed;
    }
}
