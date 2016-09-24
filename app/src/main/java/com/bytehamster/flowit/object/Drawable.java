package com.bytehamster.flowit.object;

import javax.microedition.khronos.opengles.GL10;

public abstract class Drawable {
    private float x = 0;
    private float y  = 0;
    private float scale = 1;
    private boolean visible = true;

    public abstract void draw(GL10 gl);

    public final float getX() {
        return x;
    }

    public final void setX(float x) {
        this.x = x;
    }

    public final float getY() {
        return y;
    }

    public final void setY(float y) {
        this.y = y;
    }

    public final float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }
}
