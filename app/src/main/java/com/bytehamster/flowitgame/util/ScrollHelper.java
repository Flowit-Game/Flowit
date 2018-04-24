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
    private static final float MIN_DISTANCE = 10;

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
                    drawable.setX(Math.min(Math.max(oldX + delta, minX), maxX));
                }
                if (vertical) {
                    float delta = downY - currentY;
                    drawable.setY(Math.min(Math.max(oldY + delta, minY), maxY));
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            reset();
        }
    }
}
