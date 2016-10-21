package com.bytehamster.flowitgame.model;

public enum Modifier {
    DARK, GREEN, BLUE, ORANGE, RED, EMPTY, TRANSPARENT,
    FLOOD, BOMB,
    UP, RIGHT, LEFT, DOWN,
    ROTATE_UP, ROTATE_RIGHT, ROTATE_LEFT, ROTATE_DOWN;

    public boolean isRotating() {
        return this == ROTATE_DOWN || this == ROTATE_UP
                || this == ROTATE_LEFT || this == ROTATE_RIGHT;
    }

    public Modifier rotate() {
        switch (this) {
            case ROTATE_UP:
                return ROTATE_RIGHT;
            case ROTATE_RIGHT:
                return ROTATE_DOWN;
            case ROTATE_LEFT:
                return ROTATE_UP;
            case ROTATE_DOWN:
                return ROTATE_LEFT;
            default:
                return TRANSPARENT;
        }
    }
}
