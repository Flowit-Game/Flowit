package com.bytehamster.flowitgame;

import com.bytehamster.flowitgame.model.Color;
import com.bytehamster.flowitgame.model.Modifier;

public class Converter {
    public static Color convertColor(Modifier m) {
        switch (m) {
            case DARK:
                return Color.DARK;
            case GREEN:
                return Color.GREEN;
            case BLUE:
                return Color.BLUE;
            case ORANGE:
                return Color.ORANGE;
            case RED:
                return Color.RED;
            case EMPTY:
                return Color.EMPTY;
            default:
                return null;
        }
    }

    public static Modifier convertColor(Color c) {
        switch (c) {
            case DARK:
                return Modifier.DARK;
            case GREEN:
                return Modifier.GREEN;
            case BLUE:
                return Modifier.BLUE;
            case ORANGE:
                return Modifier.ORANGE;
            case RED:
                return Modifier.RED;
            case EMPTY:
                return Modifier.EMPTY;
            default:
                return null;
        }
    }
}
