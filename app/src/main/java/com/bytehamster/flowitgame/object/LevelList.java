package com.bytehamster.flowitgame.object;

import android.view.MotionEvent;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.state.State;

import javax.microedition.khronos.opengles.GL10;

public class LevelList extends Drawable {

    private final Plane[] levelIcons = new Plane[25];
    private float positionY;

    public LevelList(float boxSize, float positionY) {
        this.positionY = positionY;

        TextureCoordinates coordinatesLevel = TextureCoordinates.getFromBlocks(6, 0, 7, 1);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col] = new Plane((col * 1.5f + 1) * boxSize,
                        positionY - (row * 1.5f) * boxSize, boxSize, boxSize, coordinatesLevel);
                levelIcons[row * 5 + col].setVisible(false);
                levelIcons[row * 5 + col].setScale(0);
            }
        }
    }

    @Override
    public void draw(GL10 gl) {
        gl.glPushMatrix();
        //gl.glTranslatef(0, positionY, 0);

        for (Plane levelIcon : levelIcons) {
            levelIcon.draw(gl);
        }

        gl.glPopMatrix();
    }

    public void entry(int pack, State context) {
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

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int levelID = (pack - 1) * 25 + row * 5 + col;
                if (context.isSolved(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelDone);
                } else if (!context.isPlayable(levelID)) {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevelLocked);
                } else {
                    levelIcons[row * 5 + col].updateTextureCoordinates(coordinatesLevel);
                }
                levelIcons[row * 5 + col].setVisible(true);
                levelIcons[row * 5 + col].setScale(0);
                levelIcons[row * 5 + col].cancelAnimations();

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row) + Animation.DURATION_LONG));
                scaleAnimation.setTo(1);
                scaleAnimation.start();
            }
        }
    }

    public void exit() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                levelIcons[row * 5 + col].setVisible(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(levelIcons[row * 5 + col],
                        Animation.DURATION_SHORT, (int) (Animation.DURATION_SHORT * 0.3f * (col + row)));
                scaleAnimation.setTo(0);
                scaleAnimation.setHideAfter(true);
                scaleAnimation.start();
            }
        }
    }

    public boolean collides(MotionEvent event, float height) {
        return getCollision(event, height) != -1;
    }

    public int getCollision(MotionEvent event, float height) {
        for (int i = 0; i < levelIcons.length; i++) {
            if (levelIcons[i].collides(event.getX(), event.getY(), height)) {
                return i;
            }
        }
        return -1;
    }
}
