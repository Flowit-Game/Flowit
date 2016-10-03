package com.bytehamster.flowitgame.object;

public class ObjectFactory {
    public static Plane createSingleBox(int texX, int texY, float boxSize) {
        TextureCoordinates coordinates = TextureCoordinates.getFromBlocks(texX, texY, texX+1, texY+1);
        return new Plane(0, 0, boxSize, boxSize, coordinates);
    }
}
