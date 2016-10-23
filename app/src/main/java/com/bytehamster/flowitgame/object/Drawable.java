package com.bytehamster.flowitgame.object;

import com.bytehamster.flowitgame.animation.Animation;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

public abstract class Drawable {
    private float x = 0;
    private float y  = 0;
    private float scale = 1;
    private boolean visible = true;
    private final ArrayList<Animation> animations = new ArrayList<>();

    public void addAnimation(Animation anim) {
        if (!animations.contains(anim)) {
            animations.add(anim);
        }
    }

    void processAnimations() {
        synchronized (animations) {
            Iterator<Animation> i = animations.iterator();
            while (i.hasNext()) {
                Animation anim = i.next();

                if (anim.shouldBeDeleted()) {
                    i.remove();
                } else {
                    anim.tick();
                }
            }
        }
    }

    public void cancelAnimations() {
        synchronized (animations) {
            animations.clear();
        }
    }

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
