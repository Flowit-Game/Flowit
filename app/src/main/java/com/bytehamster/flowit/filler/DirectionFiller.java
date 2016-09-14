package com.bytehamster.flowit.filler;

import com.bytehamster.flowit.Converter;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.model.Field;
import com.bytehamster.flowit.model.Level;
import com.bytehamster.flowit.model.Modifier;
import com.bytehamster.flowit.state.State;

public class DirectionFiller {
    private boolean somethingWasFilled = false;
    private Modifier fillFrom = Modifier.EMPTY;
    private Modifier fillTo = Modifier.BLUE;
    private Runnable execAfter;
    private Level levelData;
    private State state;
    private int dx;
    private int dy;

    public DirectionFiller(Level levelData, State state, Runnable execAfter, int dx, int dy) {
        this.levelData = levelData;
        this.state = state;
        this.execAfter = execAfter;
        this.dx = dx;
        this.dy = dy;
    }

    public void fill(final int col, final int row) {
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

                execAfter.run();
            }
        }.start();
    }

    private void doFill(int col, int row) {
        int x = col+dx;
        int y = row+dy;
        while (y>=0 && x >= 0 && y < 6 && x < 5 && levelData.fieldAt(x, y).getModifier() == fillFrom) {
            Field f = levelData.fieldAt(x, y);
            somethingWasFilled = true;
            f.setModifier(fillTo);
            state.playSound(R.raw.fill);

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x += dx;
            y += dy;
        }
    }
}
