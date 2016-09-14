package com.bytehamster.flowit.filler;

import com.bytehamster.flowit.Converter;
import com.bytehamster.flowit.R;
import com.bytehamster.flowit.model.Field;
import com.bytehamster.flowit.model.Level;
import com.bytehamster.flowit.model.Modifier;
import com.bytehamster.flowit.state.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class FloodFiller {
    private boolean somethingWasFilled = false;
    private Modifier fillFrom = Modifier.EMPTY;
    private Modifier fillTo = Modifier.BLUE;
    private Runnable execAfter;
    private Level levelData;
    private State state;
    private Stack<Thread> threads = new Stack<>();

    public FloodFiller(Level levelData, State state, Runnable execAfter) {
        this.levelData = levelData;
        this.state = state;
        this.execAfter = execAfter;
    }

    public void fill(final int col, final int row) {
        new Thread() {
            public void run() {
                levelData.unvisitAll();
                somethingWasFilled = false;

                fillFrom = Modifier.EMPTY;
                fillTo = Converter.convertColor(levelData.fieldAt(col, row).getColor());

                try {
                    floodDFS(col, row, true);
                    while(!threads.isEmpty()) {
                        threads.pop().join();
                    }

                    if (!somethingWasFilled) {
                        levelData.unvisitAll();
                        fillFrom = Converter.convertColor(levelData.fieldAt(col, row).getColor());
                        fillTo = Modifier.EMPTY;
                        floodDFS(col, row, true);

                        while(!threads.isEmpty()) {
                            threads.pop().join();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                execAfter.run();
            }
        }.start();
    }

    private void startFloodDFS(final int col, final int row, final boolean isFirst) {
        Thread t = new Thread() {
            public void run() {
                try {
                    floodDFS(col, row, isFirst);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threads.add(t);
        t.start();
    }

    private void floodDFS(final int col, final int row, final boolean isFirst) throws InterruptedException {
        Field f = levelData.fieldAt(col, row);
        if (!f.isVisited()) {
            f.setVisited(true);

            if (f.getModifier() == fillFrom || isFirst) {

                if(!isFirst) {
                    somethingWasFilled = true;
                    f.setModifier(fillTo);
                    state.playSound(R.raw.fill);
                    Thread.sleep(90);
                }

                if (col > 0) {
                    startFloodDFS(col - 1, row, false);
                }
                if (col < 4) {
                    startFloodDFS(col + 1, row, false);
                }
                if (row > 0) {
                    startFloodDFS(col, row - 1, false);
                }
                if (row < 5) {
                    startFloodDFS(col, row + 1, false);
                }
            }
        }
    }
}
