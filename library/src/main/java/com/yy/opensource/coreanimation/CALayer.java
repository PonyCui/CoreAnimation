package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CALayer extends CALayerTexture {

    /**/
    public CGRect windowBounds = new CGRect(0,0,0,0);

    /**/
    public CGRect frame = new CGRect(0,0,0,0);

    /**/
    public CATransform3D transform = new CATransform3D();

    /**/
    public CGPoint anchorPoint = new CGPoint(0.5f, 0.5f);

    /**/
    public boolean hidden = false;

    /**/
    public boolean opaque = false;

    /**/
    public float opacity = 1.0f;

    /**/
    public CGColor backgroundColor = new CGColor();

    /**/
    private Bitmap contents = null;

    private CALayer superLayer = null;
    private CALayer[] subLayers = new CALayer[0];

    public void setContents(Bitmap bitmap) {
        this.contents = bitmap;
        textureLoaded = false;
    }

    public void addSublayer(CALayer layer) {
        CALayer[] oldValues = subLayers;
        subLayers = new CALayer[oldValues.length + 1];
        for (int i = 0; i < oldValues.length; i++) {
            subLayers[i] = oldValues[i];
        }
        subLayers[oldValues.length] = layer;
        layer.superLayer = this;
    }

    @Override
    void loadTexture(GL10 gl) {
        if (textureLoaded) {
            return;
        }
        if (null != contents) {
            gl.glGenTextures(1, textureIdentifier, 0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdentifier[0]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, contents, 0);
            textureLoaded = true;
        }
    }

    @Override
    void draw(GL10 gl) {
        super.draw(gl);
        gl.glFrontFace(GL10.GL_CW);
        updateVertices();
        drawBackgroundColor(gl);
        if (textureLoaded) {
            if (hidden || opacity <= 0.0) {
                return;
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdentifier[0]);
            enableTextureFeatures(gl);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            gl.glColor4f(1.0f, 1.0f, 1.0f, combineOpacities());
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            enableTextureFeatures(gl);
        }
        for (int i = 0; i < subLayers.length; i++) {
            CALayer layer = subLayers[i];
            layer.windowBounds = windowBounds;
            layer.draw(gl);
        }
    }

    void updateVertices() {
        resetVertices();
        setFrame(frame, (superLayer != null ? superLayer.frame : null), windowBounds);
        if (!combineTransform().isIdentity()) {
            setTransform(combineTransform(), anchorPoint, frame, windowBounds);
        }
        setVertexBufferNeedsUpdate();
    }

    void drawBackgroundColor(GL10 gl) {
        if (hidden || combineOpacities() <= 0.0 || backgroundColor == null || backgroundColor.isClearColor()) {
            return;
        }
        enableBackgroundFeatures(gl);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        if (combineOpacities() < 1.0) {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, backgroundColor.colorWithAlpha(combineOpacities()).colorBuffer);
        }
        else {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, backgroundColor.colorBuffer);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        disableBackgroundFeatures(gl);
    }

    private void enableBackgroundFeatures(GL10 gl) {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        if (!opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    private void disableBackgroundFeatures(GL10 gl) {
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        if (!opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    private void enableTextureFeatures(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        if (!opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    private void disableTextureFeatures(GL10 gl) {
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        if (!opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    float combineOpacities() {
        float opacity = this.opacity;
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            opacity *= currentLayer.opacity;
            currentLayer = currentLayer.superLayer;
        }
        return opacity;
    }

    CATransform3D combineTransform() {
        Matrix matrix = transform.requestMatrix();
        matrix.postTranslate(frame.x, frame.y);
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            Matrix currentMatrix = currentLayer.transform.requestMatrix();
            matrix.postConcat(currentMatrix);
            matrix.postTranslate(currentLayer.frame.x, currentLayer.frame.y);
            currentLayer = currentLayer.superLayer;
        }
        CATransform3D finalTransform = new CATransform3D();
        finalTransform.setMatrix(matrix);
        return finalTransform;
    }

}
