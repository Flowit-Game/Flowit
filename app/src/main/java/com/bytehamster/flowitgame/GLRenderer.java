package com.bytehamster.flowitgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import androidx.annotation.DrawableRes;
import com.bytehamster.flowitgame.object.Drawable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class GLRenderer implements Renderer {
    private static final String TAG = "GLRenderer";
    private final Context myContext;

    private final int[] textureDrawables = new int[] {R.drawable.texture_colorscheme_0, R.drawable.texture_colorscheme_1};
    public final int numberOfColorschemes = textureDrawables.length;

    private int width = 0;
    private int height = 0;
    private final int[] textures = new int[1];
    private Runnable onViewportSetupComplete = null;
    private final ArrayList<Drawable> objects = new ArrayList<>();

    private int currentColorschemeIndex = 0;
    private boolean reloadTextureNextFrame = false;

    public GLRenderer(Context c) {
        myContext = c;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (reloadTextureNextFrame) {
            loadTexture(gl, 0, textureDrawables[currentColorschemeIndex]);
            reloadTextureNextFrame = false;
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        for (Drawable o : objects) {
            o.draw(gl);
        }
        debugOutput(gl);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        debugOutput(gl);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glGenTextures(textures.length, textures, 0);
        loadTexture(gl, 0, textureDrawables[currentColorschemeIndex]);

        gl.glLoadIdentity();

        debugOutput(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "setupViewport");
        if (height == 0)                       //Prevent A Divide By Zero By
            height = 1;                        //Making Height Equal One

        this.width = width;
        this.height = height;

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, 0, width, 0, height);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        debugOutput(gl);

        if (onViewportSetupComplete != null) {
            onViewportSetupComplete.run();
            onViewportSetupComplete = null;
        }
    }

    public void setColorscheme(int colorschemeIndex) {
        // Return to default colorscheme if given an invalid colorschemeIndex.
        // For example, if the number of colorschemes decreases after an update.
        if ((0 <= colorschemeIndex) && (colorschemeIndex < numberOfColorschemes)) {
            currentColorschemeIndex = colorschemeIndex;
        } else {
            currentColorschemeIndex = 0;
        }

        reloadTextureNextFrame = true;
    }

    private void loadTexture(GL10 gl, int position, @DrawableRes int resource) {
        Log.d(TAG, "loadTexture");
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[position]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(myContext.getResources(), resource), 0);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    }

    private static void debugOutput(GL10 gl) {
        int code = gl.glGetError();
        if (code != 0) {
            String errorString;
            switch (code)  {
                case GL10.GL_INVALID_ENUM:
                    errorString = "GL_INVALID_ENUM";
                    break;
                case GL10.GL_INVALID_VALUE:
                    errorString = "GL_INVALID_VALUE";
                    break;
                case GL10.GL_INVALID_OPERATION:
                    errorString = "GL_INVALID_OPERATION";
                    break;
                case GL10.GL_STACK_OVERFLOW:
                    errorString = "GL_STACK_OVERFLOW";
                    break;
                case GL10.GL_STACK_UNDERFLOW:
                    errorString = "GL_STACK_UNDERFLOW";
                    break;
                case GL10.GL_OUT_OF_MEMORY:
                    errorString = "GL_OUT_OF_MEMORY";
                    break;
                default:
                    errorString = "unknown";
                    break;
            }

            StackTraceElement elem = new Exception().getStackTrace()[1];
            Log.e(TAG, "OpenGL error: " + errorString + " (" + code + ") in " + elem.getClassName()
                    + "/" + elem.getMethodName() + ":" + elem.getLineNumber());
        }
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

    void setOnViewportSetupComplete(Runnable onViewportSetupComplete) {
        this.onViewportSetupComplete = onViewportSetupComplete;
    }
}