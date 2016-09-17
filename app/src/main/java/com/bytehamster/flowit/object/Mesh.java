package com.bytehamster.flowit.object;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Mesh implements Drawable{
    private FloatBuffer mVerticesBuffer = null;
    private ShortBuffer mIndicesBuffer = null;
    private FloatBuffer mTextureBuffer;
    private int mNumOfIndices = -1;
    private float x = 0;
    private float y = 0;
    private float scale = 1;
    private boolean visible = true;

    public void draw(GL10 gl) {
        if(!visible) {
            return;
        }

        gl.glPushMatrix();
        gl.glTranslatef(x, y, 0);
        gl.glScalef(scale, scale, scale);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices, GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);

        gl.glPopMatrix();
    }

    void setVertices(float[] vertices) {
        // a float is 4 bytes, therefore we multiply the number if
        // vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVerticesBuffer = vbb.asFloatBuffer();
        mVerticesBuffer.put(vertices);
        mVerticesBuffer.position(0);
    }

    void setIndices(short[] indices) {
        // short is 2 bytes, therefore we multiply the number if
        // vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndicesBuffer = ibb.asShortBuffer();
        mIndicesBuffer.put(indices);
        mIndicesBuffer.position(0);
        mNumOfIndices = indices.length;
    }

    void setTextureCoordinates(float[] textureCoords) {
        // float is 4 bytes, therefore we multiply the number of vertices with 4.
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(textureCoords.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuf.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScale() {
        if (this.scale == 0.0001f) {
            // Division by zero
            return 0;
        }
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        if (this.scale == 0) {
            // Division by zero
            this.scale = 0.0001f;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
