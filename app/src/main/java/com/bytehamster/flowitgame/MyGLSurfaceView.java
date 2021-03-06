package com.bytehamster.flowitgame;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {
    private final GLRenderer glRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        glRenderer = new GLRenderer(context);
        this.setRenderer(glRenderer);
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        glRenderer = new GLRenderer(context);
        this.setRenderer(glRenderer);
    }

    public GLRenderer getRenderer() {
        return glRenderer;
    }
}
