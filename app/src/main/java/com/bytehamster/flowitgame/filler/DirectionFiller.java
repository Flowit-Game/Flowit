package com.bytehamster.flowitgame.filler;

import com.bytehamster.flowitgame.Converter;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.model.Field;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.Modifier;
import com.bytehamster.flowitgame.state.State;

public class DirectionFiller extends Filler {
    private boolean somethingWasFilled = false;
    private Modifier fillFrom = Modifier.EMPTY;
    private Modifier fillTo = Modifier.BLUE;
    private final Level levelData;
    private final State state;
    private final int dx;
    private final int dy;
    private final int col, row;

    DirectionFiller(Level levelData, int col, int row, int dx, int dy, State state) {
        this.levelData = levelData;
        this.state = state;
        this.col = col;
        this.row = row;
        this.dx = dx;
        this.dy = dy;
    }

    public void fill() {
        new Thread() {
            public void run() {
                somethingWasFilled = false;

                fillFrom = Modifier.EMPTY;
                fillTo = Converter.convertColor(levelData.fieldAt(col, row).getColor());

                doFill(col, row);
                if (!somethingWasFilled) {
                    fillFrom = Converter.convertColor(levelData.fieldAt(col, row).getColor());
                    fillTo = Modifier.EMPTY;
                    doFill(col, row);
                }

                runOnFinished();
            }
        }.start();
    }

    private void doFill(int col, int row) {
        int x = col + dx;
        int y = row + dy;

        while (y >= 0 && x >= 0 && y < levelData.getHeight() && x < levelData.getWidth()
                && levelData.fieldAt(x, y).getModifier() == fillFrom) {
            Field f = levelData.fieldAt(x, y);
            somethingWasFilled = true;
            f.setModifier(fillTo);
            state.playSound(R.raw.fill);
            sleep(40);

            x += dx;
            y += dy;
        }
    }
}
