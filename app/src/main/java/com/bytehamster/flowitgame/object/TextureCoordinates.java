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
        float blockPercentageX = 128f / 2048f;
        float blockPercentageY = 128f / 2176f;
        float paddingX = 1f / 2048f;
        float paddingY = 1f / 2176f;
        return new TextureCoordinates((float)fromX * blockPercentageX + paddingX, (float)fromY * blockPercentageY + paddingY,
                (float)toX * blockPercentageX - paddingX, (float)toY * blockPercentageY - paddingY);
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
