package com.bytehamster.flowitgame.util;

import android.view.MotionEvent;
import com.bytehamster.flowitgame.object.Drawable;

public class ScrollHelper {
    private final Drawable drawable;
    private final boolean horizontal;
    private final boolean vertical;
    private float downX, downY;
    private float oldX, oldY;
    private float minX, minY, maxX, maxY;
    private boolean isScrolling;
    private boolean pressed = false;
    private static final float MIN_DISTANCE = 5;

    public ScrollHelper(Drawable drawable, boolean horizontal, boolean vertical) {
        this.drawable = drawable;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public void setMaxima(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public float clampX(float pos) {
        return Math.max(Math.min(pos, maxX), minX);
    }

    public float clampY(float pos) {
        return Math.max(Math.min(pos, maxY), minY);
    }

    private void setChildX(float pos) {
        drawable.setX(clampX(pos));
    }

    private void setChildY(float pos) {
        drawable.setY(clampY(pos));
    }

    public boolean isScrolling() {
        return isScrolling;
    }

    private void reset() {
        isScrolling = false;
        pressed = false;
    }

    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
            oldX = drawable.getX();
            oldY = drawable.getY();
            pressed = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && pressed) {
            float currentX = event.getX();
            float currentY = event.getY();

            if (Math.abs(downX - currentX) > MIN_DISTANCE && Math.abs(downY - currentY) > MIN_DISTANCE) {
                isScrolling = true;
            }

            if (isScrolling()) {
                if (horizontal) {
                    float delta = downX - currentX;
                    setChildX(oldX + delta);
                }
                if (vertical) {
                    float delta = downY - currentY;
                    setChildY(oldY + delta);
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            reset();
        }
    }
}
