package com.bytehamster.flowitgame.util;

public class PackRanges {
    private PackRanges() {

    }

    public static int[][] RANGE_1 = new int[][]{
            new int[]{0, 24},
            new int[]{75, 99},
    };
    public static int[][] RANGE_2 = new int[][]{
            new int[]{25, 49}
    };
    public static int[][] RANGE_3 = new int[][]{
            new int[]{50, 74}
    };

    public static boolean isFirstInPack(int level) {
        return level == 0 || level == 25 || level == 50;
    }

    public static boolean isLastInPack(int level) {
        return level == 99 || level == 49 || level == 74;
    }

    public static int previousLevel(int level) {
        if (level == 75) {
            return 24;
        }
        return level - 1;
    }

    public static int nextLevel(int level) {
        if (level == 24) {
            return 75;
        }
        return level + 1;
    }
}
