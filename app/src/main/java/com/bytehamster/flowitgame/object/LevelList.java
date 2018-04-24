package com.bytehamster.flowitgame.object;

import android.view.MotionEvent;
import com.bytehamster.flowitgame.state.State;

import javax.microedition.khronos.opengles.GL10;

public class LevelList extends Drawable {
    private final Plane planeLevel;
    private final Plane planeLevelDone;
    private final Plane planeLevelLocked;
    private final Number number;
    private final float boxHeight;
    private final float boxWidth;
    private final State context;
    private int[][] displayRange = new int[0][0];

    public LevelList(float boxSize, State context) {
        boxHeight = boxSize;
        boxWidth = boxSize;
        this.context = context;

        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        TextureCoordinates coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, 0, 8, 1);
        TextureCoordinates coordinatesLevelLocked = TextureCoordinates.getFromBlocks(0, 3, 7, 4);
        planeLevel = new Plane(0, 0, boxSize, boxSize, coordinatesLevel);
        planeLevelDone = new Plane(0, 0, boxSize, boxSize, coordinatesLevelDone);
        planeLevelLocked = new Plane(0, 0, boxSize, boxSize, coordinatesLevelLocked);
        number = new Number();
        number.setFontSize(boxSize / 3);
    }

    public float getHeight() {
        int num = 0;
        for (int[] currentRange : displayRange) {
            int from = currentRange[0];
            int to = currentRange[1];
            num += to - from + 1;
        }
        return boxHeight * num/3 * 1.5f + boxHeight;
    }

    private float getXFor(int num) {
        if (num % 3 == 0) {
            return boxWidth / 2;
        } else if (num % 3 == 1) {
            return context.getScreenWidth() / 3 + boxWidth / 2;
        } else {
            return (context.getScreenWidth() / 3)*2 + boxWidth / 2;
        }
    }

    private float getYFor(int num) {
        return - (num/3) * boxHeight * 1.5f - boxHeight;
    }

    private void drawButton(int num, int levelID, GL10 gl) {
        Plane draw;
        if (context.isSolved(levelID)) {
            draw = planeLevelDone;
        } else if (!context.isPlayable(levelID)) {
            draw = planeLevelLocked;
        } else {
            draw = planeLevel;
        }

        draw.setX(getXFor(num));
        draw.setY(getYFor(num));
        draw.draw(gl);

        number.setValue(num + 1);
        number.setX(draw.getX() + boxWidth + boxWidth / 4);
        number.setY(draw.getY() + boxHeight / 3);
        number.draw(gl);
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

        int num = 0;
        for (int[] currentRange : displayRange) {
            int from = currentRange[0];
            int to = currentRange[1];
            for (int levelId = from; levelId <= to; levelId++) {
                drawButton(num, levelId, gl);
                num++;
            }
        }

        gl.glPopMatrix();
    }

    public void setDisplayRange(int[][] displayRange) {
        this.displayRange = displayRange;
    }

    public boolean collides(MotionEvent event, float height) {
        return getCollision(event, height) != -1;
    }

    public int getCollision(MotionEvent event, float height) {
        int num = 0;
        for (int[] currentRange : displayRange) {
            int from = currentRange[0];
            int to = currentRange[1];
            for (int levelId = from; levelId <= to; levelId++) {
                planeLevel.setX(getXFor(num));
                planeLevel.setY(getYFor(num));
                if (planeLevel.collides(event.getX(), event.getY() + getY(), height)) {
                    return levelId;
                }
                num++;
            }
        }
        return -1;
    }
}
