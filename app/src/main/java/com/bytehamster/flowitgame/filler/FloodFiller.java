package com.bytehamster.flowitgame.filler;

import com.bytehamster.flowitgame.Converter;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.model.Field;
import com.bytehamster.flowitgame.model.Level;
import com.bytehamster.flowitgame.model.Modifier;
import com.bytehamster.flowitgame.state.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class FloodFiller {
    private boolean somethingWasFilled = false;
    private Modifier fillFrom = Modifier.EMPTY;
    private Modifier fillTo = Modifier.BLUE;
    private Runnable execAfter;
    private Level levelData;
    private State state;

    public FloodFiller(Level levelData, State state, Runnable execAfter) {
        this.levelData = levelData;
        this.state = state;
        this.execAfter = execAfter;
    }

    public void fill(final int col, final int row) {
        new Thread() {
            public void run() {
                try {
                    somethingWasFilled = false;
                    fillFrom = Modifier.EMPTY;
                    fillTo = Converter.convertColor(levelData.fieldAt(col, row).getColor());
                    floodBFS(col, row);

                    if (!somethingWasFilled) {
                        fillFrom = Converter.convertColor(levelData.fieldAt(col, row).getColor());
                        fillTo = Modifier.EMPTY;
                        floodBFS(col, row);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                execAfter.run();
            }
        }.start();
    }

    private class BfsNode {
        int col, row, distance;
        BfsNode(int col, int row, int distance) {
            this.col = col;
            this.row = row;
            this.distance = distance;
        }
        Field getField() {
            return levelData.fieldAt(col, row);
        }
    }

    private void floodBFS(final int col, final int row) throws InterruptedException {
        levelData.unvisitAll();
        Queue<BfsNode> queue = new ArrayDeque<>();

        levelData.fieldAt(col, row).setVisited(true);
        queue.add(new BfsNode(col, row, 0));
        int lastDistance = 1;

        while(!queue.isEmpty()) {
            BfsNode node = queue.poll();
            for (BfsNode neighbor : getNeighbors(node)) {
                if (!neighbor.getField().isVisited()) {
                    neighbor.getField().setVisited(true);

                    if(neighbor.getField().getModifier() == fillFrom) {
                        if (lastDistance != neighbor.distance) {
                            lastDistance = neighbor.distance;
                            Thread.sleep(80);
                            state.playSound(R.raw.fill);
                        }
                        neighbor.getField().setModifier(fillTo);
                        queue.add(neighbor);
                        somethingWasFilled = true;
                    }
                }
            }
        }
    }

    private ArrayList<BfsNode> getNeighbors(BfsNode node) {
        ArrayList<BfsNode> neighbors = new ArrayList<>();
        if (node.col > 0) {
            neighbors.add(new BfsNode(node.col - 1, node.row, node.distance+1));
        }
        if (node.col < 4) {
            neighbors.add(new BfsNode(node.col + 1, node.row, node.distance+1));
        }
        if (node.row > 0) {
            neighbors.add(new BfsNode(node.col, node.row - 1, node.distance+1));
        }
        if (node.row < 5) {
            neighbors.add(new BfsNode(node.col, node.row + 1, node.distance+1));
        }
        return neighbors;
    }
}
