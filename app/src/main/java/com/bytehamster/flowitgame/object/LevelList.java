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
    private final int levelCount = 25;
    private int pack = 0;

    public LevelList(float boxSize, State context) {
        boxHeight = boxSize;
        boxWidth = boxSize;
        this.context = context;
        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        planeLevel = new Plane(0, 0, boxSize, boxSize, coordinatesLevel);
        planeLevelDone = new Plane(0, 0, boxSize, boxSize, coordinatesLevel);
        planeLevelLocked = new Plane(0, 0, boxSize, boxSize, coordinatesLevel);
        number = new Number();
        number.setFontSize(boxSize / 3);
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

    private void drawButton(int num, GL10 gl) {
        int levelID = (pack - 1) * 25 + num;
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

        for (int i = 0; i < levelCount; i++) {
            drawButton(i, gl);
        }

        gl.glPopMatrix();
    }

    public void entry(int pack) {
        this.pack = pack;

        TextureCoordinates coordinatesLevel;
        TextureCoordinates coordinatesLevelDone;
        TextureCoordinates coordinatesLevelLocked;
        if (pack == 4) {
            coordinatesLevel = TextureCoordinates.getFromBlocks(11, 1, 12, 2);
            coordinatesLevelDone = TextureCoordinates.getFromBlocks(12, 1, 13, 2);
            coordinatesLevelLocked = TextureCoordinates.getFromBlocks(13, 1, 14, 2);
        } else {
            coordinatesLevel = TextureCoordinates.getFromBlocks(6, pack - 1, 7, pack);
            coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, pack - 1, 8, pack);
            coordinatesLevelLocked = TextureCoordinates.getFromBlocks(5 + pack, 3, 6 + pack, 4);
        }
        planeLevel.updateTextureCoordinates(coordinatesLevel);
        planeLevelDone.updateTextureCoordinates(coordinatesLevelDone);
        planeLevelLocked.updateTextureCoordinates(coordinatesLevelLocked);
    }

    public boolean collides(MotionEvent event, float height) {
        return getCollision(event, height) != -1;
    }

    public int getCollision(MotionEvent event, float height) {
        for (int i = 0; i < levelCount; i++) {
            planeLevel.setX(getXFor(i));
            planeLevel.setY(getYFor(i));
            if (planeLevel.collides(event.getX(), event.getY() + getY(), height)) {
                return i;
            }
        }
        return -1;
    }
}
