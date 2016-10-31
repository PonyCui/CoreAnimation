package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

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
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            changeViewport(gl);
            gl.glColor4f(1.0f, 1.0f, 1.0f, opacity);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
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

    void changeViewport(GL10 gl) {
        int superX = 0;
        int superY = 0;
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            superX += (int)currentLayer.frame.x;
            superY += (int)currentLayer.frame.y;
            currentLayer = currentLayer.superLayer;
        }
        gl.glViewport((int)this.frame.x + superX, (int)this.windowBounds.height - (int)this.frame.height - (int)this.frame.y - superY, (int)this.frame.width, (int)this.frame.height);
    }

}
