package com.bytehamster.flowit.filler;

import com.bytehamster.flowit.Converter;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.model.Field;
import com.bytehamster.flowit.model.Level;
import com.bytehamster.flowit.model.Modifier;
import com.bytehamster.flowit.state.State;

public class BombFiller {
    private Modifier fillTo = Modifier.BLUE;
    private Runnable execAfter;
    private Level levelData;
    private State state;

    public BombFiller(Level levelData, State state, Runnable execAfter) {
        this.levelData = levelData;
        this.state = state;
        this.execAfter = execAfter;
    }

    public void fill(final int col, final int row) {
        new Thread() {
            public void run() {
                fillTo = Converter.convertColor(levelData.fieldAt(col, row).getColor());

                try {
                    Thread.sleep(100);
                    state.playSound(R.raw.fill);

                    doFill(col, row);

                    Thread.sleep(100);

                    doFill(col+1, row);
                    doFill(col, row+1);
                    doFill(col-1, row);
                    doFill(col, row-1);

                    Thread.sleep(100);

                    doFill(col+1, row-1);
                    doFill(col+1, row+1);
                    doFill(col-1, row-1);
                    doFill(col-1, row+1);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                execAfter.run();
            }
        }.start();
    }

    private void doFill(int col, int row) {
        if (row >= 0 && col >= 0 && row < 6 && col < 5) {
            Field f = levelData.fieldAt(col, row);
            if(f.getModifier() != Modifier.TRANSPARENT) {
                f.setModifier(fillTo);
            }
        }
    }
}
