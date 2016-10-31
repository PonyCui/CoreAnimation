package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
    public boolean hidden = false;

    /**/
    public boolean opaque = false;

    /**/
    public float opacity = 1.0f;

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
        if (textureLoaded) {
            if (hidden) {
                return;
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdentifier[0]);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            if (!opaque) {
                gl.glEnable(GL10.GL_BLEND);
                gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
                gl.glEnable(GL10.GL_DEPTH_TEST);
                gl.glEnable(GL10.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
//                GLU.gluOrtho2D(gl, 0.0f, 0.0f, 0.0f, 0.0f);
                gl.glEnable(GL10.GL_TEXTURE_2D);
            }
            gl.glFrontFace(GL10.GL_CW);
            FloatBuffer currentVertexBuffer;
            if (!transform.isIdentity()) {
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
                byteBuffer.order(ByteOrder.nativeOrder());
                currentVertexBuffer = byteBuffer.asFloatBuffer();
                currentVertexBuffer.put(this.scaledVertices(transform.a, transform.d));
                currentVertexBuffer.position(0);
            }
            else {
                currentVertexBuffer = this.vertexBuffer;
            }
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, currentVertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            changeViewport(gl);
            gl.glColor4f(1.0f, 1.0f, 1.0f, combineOpacities());
            if (!transform.isIdentity()) {
                gl.glPushMatrix();
                gl.glLoadIdentity();
                float[] values = transform.request3DMatrix();
                values[12] = 0.0f;
                values[13] = 0.0f;
                gl.glMultMatrixf(values, 0);
            }
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
            if (!transform.isIdentity()) {
                gl.glPopMatrix();
            }
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        for (int i = 0; i < subLayers.length; i++) {
            CALayer layer = subLayers[i];
            layer.windowBounds = windowBounds;
            layer.draw(gl);
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

    void changeViewport(GL10 gl) {
        int superX = 0;
        int superY = 0;
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            superX += (int)currentLayer.frame.x;
            superY += (int)currentLayer.frame.y;
            currentLayer = currentLayer.superLayer;
        }
        int translateX = 0;
        int translateY = 0;
        float transformedWidth = this.frame.width;
        float transformedHeight = this.frame.height;
        if (!transform.isIdentity()) {
            translateX = (int)transform.tx;
            translateY = (int)transform.ty;
            float llx = transform.a * frame.x + transform.c * frame.y + transform.tx;
            float lrx = transform.a * (frame.x + frame.width) + transform.c * frame.y + transform.tx;
            float lbx = transform.a * frame.x + transform.c * (frame.y + frame.height) + transform.tx;
            float rbx = transform.a * (frame.x + frame.width) + transform.c * (frame.y + frame.height) + transform.tx;
            float lly = transform.b * frame.x + transform.d * frame.y + transform.ty;
            float lry = transform.b * (frame.x + frame.width) + transform.d * frame.y + transform.ty;
            float lby = transform.b * frame.x + transform.d * (frame.y + frame.height) + transform.ty;
            float rby = transform.b * (frame.x + frame.width) + transform.d * (frame.y + frame.height) + transform.ty;
            float minX = Math.min(Math.min(lbx, rbx), Math.min(llx, lrx));
            float maxX = Math.max(Math.max(lbx, rbx), Math.max(llx, lrx));
            float minY = Math.min(Math.min(lby, rby), Math.min(lly, lry));
            float maxY = Math.max(Math.max(lby, rby), Math.max(lly, lry));
            transformedWidth = maxX - minX;
            transformedHeight = maxY - minY;
        }
        gl.glViewport(
                (int)this.frame.x + superX + translateX,
                (int)this.windowBounds.height - (int)transformedHeight - (int)this.frame.y - superY - translateY,
                (int)transformedWidth,
                (int)transformedHeight
        );
    }

}
