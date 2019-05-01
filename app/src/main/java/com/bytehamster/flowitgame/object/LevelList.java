package com.bytehamster.flowitgame.object;

import android.view.MotionEvent;
import com.bytehamster.flowitgame.BuildConfig;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.LevelPack;
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
    private LevelPack pack;

    public LevelList(float boxSize, State context) {
        boxHeight = boxSize;
        boxWidth = boxSize;
        this.context = context;

        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        TextureCoordinates coordinatesLevelDone = TextureCoordinates.getFromBlocks(7, 0, 8, 1);
        TextureCoordinates coordinatesLevelLocked = TextureCoordinates.getFromBlocks(6, 3, 7, 4);
        planeLevel = new Plane(0, 0, boxSize, boxSize, coordinatesLevel);
        planeLevelDone = new Plane(0, 0, boxSize, boxSize, coordinatesLevelDone);
        planeLevelLocked = new Plane(0, 0, boxSize, boxSize, coordinatesLevelLocked);
        number = new Number();
        number.setFontSize(boxSize / 3);
    }

    public float getHeight() {
        int num = pack.size();
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

    private void drawButton(int indexInPack, Level level, GL10 gl) {
        Plane draw;
        if (context.isSolved(level.getNumber())) {
            draw = planeLevelDone;
        } else if (!context.isPlayable(level)) {
            draw = planeLevelLocked;
        } else {
            draw = planeLevel;
        }

        draw.setX(getXFor(indexInPack));
        draw.setY(getYFor(indexInPack));
        draw.draw(gl);

        if (BuildConfig.DEBUG_LEVELS) {
            number.setValue(level.getNumber());
        } else {
            number.setValue(indexInPack + 1);
        }
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

        if (pack != null) {
            for (int i = 0; i < pack.size(); i++) {
                drawButton(i, pack.getLevel(i), gl);
            }
        }

        gl.glPopMatrix();
    }

    public void setPack(LevelPack pack) {
        this.pack = pack;
    }

    public boolean collides(MotionEvent event, float height) {
        return getCollision(event, height) != null;
    }

    public Level getCollision(MotionEvent event, float height) {
        for (int i = 0; i < pack.size(); i++) {
            planeLevel.setX(getXFor(i));
            planeLevel.setY(getYFor(i));
            if (planeLevel.collides(event.getX(), event.getY() + getY(), height)) {
                return pack.getLevel(i);
            }
        }
        return null;
    }
}
