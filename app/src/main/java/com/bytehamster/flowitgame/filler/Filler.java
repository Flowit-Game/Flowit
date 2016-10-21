package com.bytehamster.flowitgame.filler;

import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.state.State;

public abstract class Filler {
    private Runnable onFinished = null;

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
            case UP:
                return new DirectionFiller(levelData, col, row, 0, -1, state);
            case RIGHT:
                return new DirectionFiller(levelData, col, row, 1, 0, state);
            case LEFT:
                return new DirectionFiller(levelData, col, row, -1, 0, state);
            case DOWN:
                return new DirectionFiller(levelData, col, row, 0, 1, state);
            case ROTATE_UP:
                return new DirectionFiller(levelData, col, row, 0, -1, state);
            case ROTATE_RIGHT:
                return new DirectionFiller(levelData, col, row, 1, 0, state);
            case ROTATE_LEFT:
                return new DirectionFiller(levelData, col, row, -1, 0, state);
            case ROTATE_DOWN:
                return new DirectionFiller(levelData, col, row, 0, 1, state);
            default:
                return null;
        }
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }
}
