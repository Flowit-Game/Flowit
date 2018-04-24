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
            instance.initialize();
        }
        return instance;
    }

    private void initialize() {
        colors = new Plane[6];
        colors[0] = ObjectFactory.createSingleBox(8, 0, 1);
        colors[1] = ObjectFactory.createSingleBox(10, 0, 1);
        colors[2] = ObjectFactory.createSingleBox(12, 0, 1);
        colors[3] = ObjectFactory.createSingleBox(14, 0, 1);
        colors[4] = ObjectFactory.createSingleBox(8, 1, 1);
        colors[5] = ObjectFactory.createSingleBox(15, 15, 1);

        modifiers = new Plane[17];
        modifiers[0] = ObjectFactory.createSingleBox(9, 0, 1);
        modifiers[1] = ObjectFactory.createSingleBox(11, 0, 1);
        modifiers[2] = ObjectFactory.createSingleBox(13, 0, 1);
        modifiers[3] = ObjectFactory.createSingleBox(15, 0, 1);
        modifiers[4] = ObjectFactory.createSingleBox(9, 1, 1);
        modifiers[5] = ObjectFactory.createSingleBox(8, 2, 1);
        modifiers[6] = ObjectFactory.createSingleBox(10, 1, 1);
        modifiers[7] = ObjectFactory.createSingleBox(15, 15, 1);
        modifiers[8] = ObjectFactory.createSingleBox(10, 2, 1);
        modifiers[9] = ObjectFactory.createSingleBox(9, 2, 1);
        modifiers[10] = ObjectFactory.createSingleBox(11, 2, 1);
        modifiers[11] = ObjectFactory.createSingleBox(12, 2, 1);
        modifiers[12] = ObjectFactory.createSingleBox(13, 2, 1);
        modifiers[13] = ObjectFactory.createSingleBox(10, 3, 1);
        modifiers[14] = ObjectFactory.createSingleBox(9, 3, 1);
        modifiers[15] = ObjectFactory.createSingleBox(11, 3, 1);
        modifiers[16] = ObjectFactory.createSingleBox(12, 3, 1);
    }

    @Override
    public synchronized void draw(GL10 gl) {
        if (level == null || !isVisible()) {
            return;
        }

        processAnimations();

        float startY = getY() - boxSize;
        for (int col = 0; col < level.getWidth(); col++) {
            for (int row = 0; row < level.getHeight(); row++) {
                Field field = level.fieldAt(col, row);

                Plane color = getColorPlane(field.getColor());
                color.setX(getX() + (col + 0.5f) * boxSize);
                color.setY(startY - row * boxSize);
                color.draw(gl);

                Plane modifier = getModifierPlane(field.getModifier());
                modifier.setX(getX() + (col + 0.5f) * boxSize);
                modifier.setY(startY - row * boxSize);
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
        switch (color) {
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
        this.level = level;
        recalculateSizes();
    }

    private void recalculateSizes() {
        if (level == null) {
            return;
        }

        this.boxSize = this.screenWidth / (float) (level.getWidth() + 1);
        for (Plane color : colors) {
            color.setScale(boxSize);
        }
        for (Plane modifier : modifiers) {
            modifier.setScale(boxSize);
        }
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
        recalculateSizes();
    }

    public float getBoxSize() {
        return boxSize;
    }

    public float getHeight() {
        return level.getHeight() * getBoxSize();
    }
}
