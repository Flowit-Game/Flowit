package com.bytehamster.flowit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

import com.bytehamster.flowit.object.Drawable;
import com.bytehamster.flowit.object.Plane;
import com.bytehamster.flowit.object.TextureCoordinates;

import java.util.ArrayList;

public class GLRenderer implements Renderer {
    private Context myContext = null;
    private float width = 0;
    private float height = 0;
    private int[] textures = new int[2];
    private Runnable onReady = null;
    private int loadProgress = 0;
    private ArrayList<Drawable> objects = new ArrayList<>();

    public GLRenderer(Context c) {
        myContext = c;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (loadProgress == 0) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

            float loaderSize = getWidth() / 4;
            Plane loader = new Plane(getWidth()/2 - loaderSize/2, getHeight()/2 - loaderSize/2,
                    loaderSize, loaderSize, new TextureCoordinates(0,0,1,1));
            loader.draw(gl);

            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
            loadProgress = 1;
        } else if (loadProgress == 1) {
            loadTex(gl);
            if(onReady != null) {
                onReady.run();
            }
            loadProgress = 2;
            gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        }


        for(Drawable o : objects) {
            o.draw(gl);
        }
    }

    void loadTexLoader(GL10 gl) {
        gl.glGenTextures(textures.length, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(myContext.getResources(), R.drawable.loading), 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
    }

    void loadTex(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(myContext.getResources(), R.drawable.texture), 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0)                       //Prevent A Divide By Zero By
            height = 1;                        //Making Height Equal One

        this.width = width;
        this.height = height;

        gl.glEnable(GL10.GL_ALPHA_TEST);        // Enable Alpha
        gl.glAlphaFunc(GL10.GL_GREATER, 0.9f);  //

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        GLU.gluOrtho2D(gl, 0, width, 0, height);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        loadTexLoader(gl);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void addDrawable(Drawable d) {
        objects.add(d);
    }

    public void removeDrawable(Drawable d) {
        objects.remove(d);
    }

    public void setOnReady(Runnable onReady) {
        this.onReady = onReady;
    }
}