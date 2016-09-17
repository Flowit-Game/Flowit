package com.bytehamster.flowit.object;

import android.annotation.SuppressLint;

import com.bytehamster.flowit.model.Color;
import com.bytehamster.flowit.model.Field;
import com.bytehamster.flowit.model.Level;
import com.bytehamster.flowit.model.Modifier;

import javax.microedition.khronos.opengles.GL10;

public class LevelDrawer implements Drawable{
    @SuppressLint("StaticFieldLeak")
    private static LevelDrawer instance;

    private Level level;
    private Plane[] colors;
    private Plane[] modifiers;
    private float boxSize = 50f;
    private float screenWidth = 0;

    private float screenHeight = 0;
    private float x = 0;
    private float y = 0;
    private boolean visible = true;

    public static LevelDrawer getInstance() {
        if (instance == null) {
            instance = new LevelDrawer();
        }
        return instance;
    }

    public void initialize() {
        this.boxSize = this.screenWidth / 6f;

        colors = new Plane[6];
        colors[0] = createSingleBox(8, 0);
        colors[1] = createSingleBox(10, 0);
        colors[2] = createSingleBox(12, 0);
        colors[3] = createSingleBox(14, 0);
        colors[4] = createSingleBox(8, 1);
        colors[5] = createSingleBox(11, 1);

        modifiers = new Plane[13];
        modifiers[0] = createSingleBox(9, 0);
        modifiers[1] = createSingleBox(11, 0);
        modifiers[2] = createSingleBox(13, 0);
        modifiers[3] = createSingleBox(15, 0);
        modifiers[4] = createSingleBox(9, 1);
        modifiers[5] = createSingleBox(8, 2);
        modifiers[6] = createSingleBox(10, 1);
        modifiers[7] = createSingleBox(11, 1);
        modifiers[8] = createSingleBox(10, 2);
        modifiers[9] = createSingleBox(9, 2);
        modifiers[10] = createSingleBox(11, 2);
        modifiers[11] = createSingleBox(12, 2);
        modifiers[12] = createSingleBox(13, 2);
    }

    @Override
    public void draw(GL10 gl) {
        if (level == null || !visible) {
            return;
        }

        for(int col = 0; col < 5; col++) {
            for(int row = 0; row < 6; row++) {
                Field field = level.fieldAt(col, row);

                Plane color = getColorPlane(field.getColor());
                color.setX(x + (col + 0.5f)*boxSize);
                color.setY(y - row*boxSize);
                color.draw(gl);

                Plane modifier = getModifierPlane(field.getModifier());
                modifier.setX(x + (col + 0.5f)*boxSize);
                modifier.setY(y - row*boxSize);
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
            case BOMB:
                return modifiers[12];
            default:
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

    private Plane createSingleBox(int texX, int texY) {
        TextureCoordinates coordinates = TextureCoordinates.getFromBlocks(texX, texY, texX+1, texY+1);
        return new Plane(0, 0, boxSize, boxSize, coordinates);
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }

    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }

    public float getBoxSize() {
        return boxSize;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setScale(float scale) {

    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
