package com.bytehamster.flowitgame.object;

import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class Container extends Drawable {
    private ArrayList<Drawable> children = new ArrayList<>();

    public void addDrawable(Drawable d) {
        children.add(d);
    }

    @Override
    public void draw(GL10 gl) {
        if (!isVisible()) {
            return;
        }
        processAnimations();

        gl.glPushMatrix();
        gl.glTranslatef(getX(), getY(), 0);
        gl.glScalef(getScale(), getScale(), getScale());

        for (Drawable child : children) {
            child.draw(gl);
        }

        gl.glPopMatrix();
    }
}
