package com.bytehamster.flowit.object;

import javax.microedition.khronos.opengles.GL10;

public interface Drawable {
    void draw(GL10 gl);
    void setX(float x);
    void setY(float y);
    void setScale(float scale);
    void setVisible(boolean visible);
}
