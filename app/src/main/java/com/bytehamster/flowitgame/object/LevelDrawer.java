package com.bytehamster.flowitgame.object;

import android.annotation.SuppressLint;

import com.bytehamster.flowitgame.model.Color;
import com.bytehamster.flowitgame.model.Field;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.Modifier;

import javax.microedition.khronos.opengles.GL10;

public class LevelDrawer extends Drawable {
    @SuppressLint("StaticFieldLeak")
    private static LevelDrawer instance;

    private Level level;
    private Plane[] colors;
    private Plane[] modifiers;
    private float boxSize = 50f;
    private float screenWidth = 0;

    public static LevelDrawer getInstance() {
        if (instance == null) {
            instance = new LevelDrawer();
        }
        return instance;
    }

    private void initialize() {
        if (level == null) {
            this.boxSize = this.screenWidth / 6f;
        } else {
            this.boxSize = this.screenWidth / (float) (level.getWidth() + 1);
        }

        colors = new Plane[6];
        colors[0] = ObjectFactory.createSingleBox(8, 0, boxSize);
        colors[1] = ObjectFactory.createSingleBox(10, 0, boxSize);
        colors[2] = ObjectFactory.createSingleBox(12, 0, boxSize);
        colors[3] = ObjectFactory.createSingleBox(14, 0, boxSize);
        colors[4] = ObjectFactory.createSingleBox(8, 1, boxSize);
        colors[5] = ObjectFactory.createSingleBox(15, 15, boxSize);

        modifiers = new Plane[17];
        modifiers[0] = ObjectFactory.createSingleBox(9, 0, boxSize);
        modifiers[1] = ObjectFactory.createSingleBox(11, 0, boxSize);
        modifiers[2] = ObjectFactory.createSingleBox(13, 0, boxSize);
        modifiers[3] = ObjectFactory.createSingleBox(15, 0, boxSize);
        modifiers[4] = ObjectFactory.createSingleBox(9, 1, boxSize);
        modifiers[5] = ObjectFactory.createSingleBox(8, 2, boxSize);
        modifiers[6] = ObjectFactory.createSingleBox(10, 1, boxSize);
        modifiers[7] = ObjectFactory.createSingleBox(15, 15, boxSize);
        modifiers[8] = ObjectFactory.createSingleBox(10, 2, boxSize);
        modifiers[9] = ObjectFactory.createSingleBox(9, 2, boxSize);
        modifiers[10] = ObjectFactory.createSingleBox(11, 2, boxSize);
        modifiers[11] = ObjectFactory.createSingleBox(12, 2, boxSize);
        modifiers[12] = ObjectFactory.createSingleBox(13, 2, boxSize);
        modifiers[13] = ObjectFactory.createSingleBox(10, 3, boxSize);
        modifiers[14] = ObjectFactory.createSingleBox(9, 3, boxSize);
        modifiers[15] = ObjectFactory.createSingleBox(11, 3, boxSize);
        modifiers[16] = ObjectFactory.createSingleBox(12, 3, boxSize);
    }

    @Override
    public synchronized void draw(GL10 gl) {
        if (level == null || !isVisible()) {
            return;
        }

        processAnimations();

        for (int col = 0; col < level.getWidth(); col++) {
            for (int row = 0; row < level.getHeight(); row++) {
                Field field = level.fieldAt(col, row);

                Plane color = getColorPlane(field.getColor());
                color.setX(getX() + (col + 0.5f)*boxSize);
                color.setY(getY() - row*boxSize);
                color.draw(gl);

                Plane modifier = getModifierPlane(field.getModifier());
                modifier.setX(getX() + (col + 0.5f)*boxSize);
                modifier.setY(getY() - row*boxSize);
                modifier.draw(gl);
            }
        }
    }

    private Plane getModifierPlane(Modifier modifier) {
        switch (modifier) {
            case DARK:
                return modifiers[0];
            case GREEN:
                return modifiers[1];
            case BLUE:
                return modifiers[2];
            case ORANGE:
                return modifiers[3];
            case RED:
                return modifiers[4];
            case FLOOD:
                return modifiers[5];
            case EMPTY:
                return modifiers[6];
            case UP:
                return modifiers[8];
            case RIGHT:
                return modifiers[9];
            case LEFT:
                return modifiers[10];
            case DOWN:
                return modifiers[11];
            case ROTATE_UP:
                return modifiers[13];
            case ROTATE_RIGHT:
                return modifiers[14];
            case ROTATE_LEFT:
                return modifiers[15];
            case ROTATE_DOWN:
                return modifiers[16];
            case BOMB:
                return modifiers[12];
            default: // empty
                return modifiers[7];
        }
    }

    private Plane getColorPlane(Color color) {
        switch(color) {
            case DARK:
                return colors[0];
            case GREEN:
                return colors[1];
            case BLUE:
                return colors[2];
            case ORANGE:
                return colors[3];
            case RED:
                return colors[4];
            default: // empty
                return colors[5];
        }
    }

    public synchronized void setLevel(Level level) {
        if (this.level == null || level.getWidth() != this.level.getWidth()) {
            this.level = level;
            initialize();
        } else {
            this.level = level;
        }
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public float getBoxSize() {
        return boxSize;
    }

    public float getHeight() {
        return level.getHeight()*getBoxSize();
    }
}
