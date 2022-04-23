package com.bytehamster.flowitgame.filler;

import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.state.State;

public abstract class Filler {
    private Runnable onFinished = null;
    private long lastAction = 0;

    public abstract void fill();

    void runOnFinished() {
        onFinished.run();
    }

    public static Filler get(Level levelData, int col, int row, State state) {
        switch (levelData.fieldAt(col, row).getModifier()) {
            case FLOOD:
                return new FloodFiller(levelData, col, row, state);
            case BOMB:
                return new BombFiller(levelData, col, row, state);
            case UP: // Fall-through
            case ROTATE_UP:
                return new DirectionFiller(levelData, col, row, 0, -1, state);
            case RIGHT: // Fall-through
            case ROTATE_RIGHT:
                return new DirectionFiller(levelData, col, row, 1, 0, state);
            case LEFT: // Fall-through
            case ROTATE_LEFT:
                return new DirectionFiller(levelData, col, row, -1, 0, state);
            case DOWN: // Fall-through
            case ROTATE_DOWN:
                return new DirectionFiller(levelData, col, row, 0, 1, state);
            default:
                return null;
        }
    }

    protected void sleep(long milliseconds) {
        long timePassed = System.currentTimeMillis() - lastAction;
        long timeLeft = milliseconds - timePassed;
        if (lastAction == 0) {
            timeLeft = milliseconds;
        }
        lastAction = System.currentTimeMillis();
        if (timeLeft <= 0) {
            return;
        }
        try {
            Thread.sleep(timeLeft);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }
}
