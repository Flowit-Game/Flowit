package com.bytehamster.flowitgame.object;

import javax.microedition.khronos.opengles.GL10;

public class Number extends Drawable {
    private static final float LETTER_SIZE = 100;
    private static final Plane[] LETTERS = {
            ObjectFactory.createSingleBox(11, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(12, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(13, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(14, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(15, 8, LETTER_SIZE),
            ObjectFactory.createSingleBox(11, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(12, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(13, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(14, 9, LETTER_SIZE),
            ObjectFactory.createSingleBox(15, 9, LETTER_SIZE),
    };

    private int value = 0;
    private float fontSize = 10;

    @Override
    public void draw(GL10 gl) {
        if(!isVisible()) {
            return;
        }
        processAnimations();

        gl.glPushMatrix();
        gl.glTranslatef(getX(), getY(), 0);
        gl.glScalef(getScale() * fontSize * 1.5f / LETTER_SIZE,
                getScale() * fontSize * 1.5f / LETTER_SIZE,
                getScale() * fontSize * 1.5f / LETTER_SIZE);

        String valueString = String.valueOf(value);
        for (int i = 0; i < valueString.length(); i++) {
            LETTERS[valueString.charAt(i) - '0'].draw(gl);
            gl.glTranslatef(LETTER_SIZE * 0.6f, 0, 0);
        }

        gl.glPopMatrix();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void increment() {
        this.value++;
    }

    public void setFontSize(float size) {
        this.fontSize = size;
    }
}
