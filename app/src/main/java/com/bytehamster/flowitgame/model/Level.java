package com.bytehamster.flowitgame.model;

public class Level {
    private Field[][] originalMap;
    private Field[][] map;
    private int number;
    private int indexInPack;
    private LevelPack pack;
    private int optimalSteps;

    public Level(int indexInPack, int number, LevelPack pack, String color, String modifier, int optimalSteps) {
        this.number = number;
        this.indexInPack = indexInPack;
        this.pack = pack;
        this.optimalSteps = optimalSteps;

        color = color.replaceAll("\\s", "");
        modifier = modifier.replaceAll("\\s", "");
        int width = 5;
        int height = 6;

        if (color.length() == 6*8 && modifier.length() == 6*8) {
            width = 6;
            height = 8;
        }

        originalMap = new Field[width][height];
        map = new Field[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                int index = col + row * width;
                originalMap[col][row] = new Field(color.charAt(index), modifier.charAt(index));
            }
        }
        reset();
    }

    public void reset() {
        int width = originalMap.length;
        int height = originalMap[0].length;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                map[col][row] = originalMap[col][row].clone();
            }
        }
    }

    public Field fieldAt(int x, int y) {
        return map[x][y];
    }

    public void unvisitAll() {
        for(int col = 0; col < getWidth(); col++) {
            for(int row = 0; row < getHeight(); row++) {
                map[col][row].setVisited(false);
            }
        }
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }

    public int getNumber() {
        return number;
    }

    public int getIndexInPack() {
        return indexInPack;
    }

    public LevelPack getPack() {
        return pack;
    }

    public int getOptimalSteps() {
        return optimalSteps;
    }
}
