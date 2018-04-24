package com.bytehamster.flowitgame.util;

import java.util.ArrayList;

public class PackRanges {
    private PackRanges() {

    }

    public static final int[][] RANGE_1 = new int[][]{
            new int[]{0, 24},
            new int[]{75, 99},
    };
    public static final int[][] RANGE_2 = new int[][]{
            new int[]{25, 49}
    };
    public static final int[][] RANGE_3 = new int[][]{
            new int[]{50, 74}
    };


    private static final ArrayList<int[]> ANOMALIES = calculateAnomalies();

    private static ArrayList<int[]> calculateAnomalies() {
        ArrayList<int[]> result = new ArrayList<>();
        result.addAll(calculateAnomalies(RANGE_1));
        result.addAll(calculateAnomalies(RANGE_2));
        result.addAll(calculateAnomalies(RANGE_3));
        return result;
    }

    private static ArrayList<int[]> calculateAnomalies(int[][] pack) {
        ArrayList<int[]> result = new ArrayList<>();
        for (int i = 0; i < pack.length - 1; i++) {
            result.add(new int[]{pack[i][1], pack[i+1][0]});
        }
        return result;
    }

    private static int firstOf(int [][] pack) {
        return pack[0][0];
    }

    public static boolean isFirstInPack(int level) {
        return level == firstOf(RANGE_1) || level == firstOf(RANGE_2) || level == firstOf(RANGE_3);
    }

    private static int lastOf(int [][] pack) {
        return pack[pack.length - 1][1];
    }

    public static boolean isLastInPack(int level) {
        return level == lastOf(RANGE_1) || level == lastOf(RANGE_2) || level == lastOf(RANGE_3);
    }

    public static int previousLevel(int level) {
        for (int[] anomaly : ANOMALIES) {
            if (anomaly[1] == level) {
                return anomaly[0];
            }
        }
        return level - 1;
    }

    public static int nextLevel(int level) {
        for (int[] anomaly : ANOMALIES) {
            if (anomaly[0] == level) {
                return anomaly[1];
            }
        }
        return level + 1;
    }
}
