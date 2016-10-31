package com.yy.opensource.coreanimation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.provider.Settings;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CARenderer implements GLSurfaceView.Renderer {

    private Context context;
    private CASurfaceView surfaceView;
    private long nextTrick = -1;

    public CARenderer(Context context, CASurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glShadeModel(GL10.GL_SMOOTH);
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl10.glClearDepthf(1.0f);
        gl10.glEnable(GL10.GL_DEPTH_TEST);
        gl10.glDepthFunc(GL10.GL_LEQUAL);
        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl10.glDisable(GL10.GL_DITHER);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.surfaceView.layer.windowBounds.width = width;
        this.surfaceView.layer.windowBounds.height = height;
        this.surfaceView.layer.frame.width = width;
        this.surfaceView.layer.frame.height = height;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (System.currentTimeMillis() < nextTrick) {
            return;
        }
        nextTrick = System.currentTimeMillis() + (int)((1.0f / (float)surfaceView.FPS) * 1000);
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        this.surfaceView.layer.draw(gl10);
    }

}
