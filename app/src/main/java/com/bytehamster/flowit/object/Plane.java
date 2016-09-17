package com.bytehamster.flowit.object;

public class Plane extends Mesh {

    public Plane(float x, float y, float width, float height, TextureCoordinates coordinates) {
        setX(x);
        setY(y);

        setIndices(new short[] {
                0, 2, 1, 2, 3, 1
        });
        setVertices(new float[] {
                /* X,  Y,      Z */
                0.0f,  0,      0.0f, // 0 - l.u.
                0.0f,  height, 0.0f, // 1 - l.o.
                width, 0,      0.0f, // 2 - r.u.
                width, height, 0.0f, // 3 - r.o.
        });
        updateTextureCoordinates(coordinates);
    }

    public void updateTextureCoordinates(TextureCoordinates coordinates) {
        setTextureCoordinates(new float[] {
                coordinates.getFromX(), coordinates.getToY(),    // 0 - l.u.
                coordinates.getFromX(), coordinates.getFromY(),  // 1 - l.o.
                coordinates.getToX(),   coordinates.getToY(),    // 2 - r.u.
                coordinates.getToX(),   coordinates.getFromY(),  // 3 - r.o.
        });
    }
}
