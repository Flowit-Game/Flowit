package com.bytehamster.flowitgame.object;

public class TextureCoordinates {
    private float fromX;
    private float fromY;
    private float toX;
    private float toY;

    public TextureCoordinates(float fromX, float fromY, float toX, float toY) {
        this.fromX = fromX;
        this.toX = toX;
        this.fromY = fromY;
        this.toY = toY;
    }

    public static TextureCoordinates getFromBlocks(int fromX, int fromY, int toX, int toY) {
        float blockPercentage = 128f / 2048f;
        float padding = 1f / 2048f;
        return new TextureCoordinates((float)fromX * blockPercentage + padding, (float)fromY * blockPercentage + padding,
                (float)toX * blockPercentage - padding, (float)toY * blockPercentage - padding);
    }

    public float getToY() {
        return toY;
    }

    public void setToY(float toY) {
        this.toY = toY;
    }

    public float getToX() {
        return toX;
    }

    public void setToX(float toX) {
        this.toX = toX;
    }

    public float getFromY() {
        return fromY;
    }

    public void setFromY(float fromY) {
        this.fromY = fromY;
    }

    public float getFromX() {
        return fromX;
    }

    public void setFromX(float fromX) {
        this.fromX = fromX;
    }
}
