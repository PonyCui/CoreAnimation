package com.yy.opensource.coreanimation;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CALayerTexture {

    protected FloatBuffer textureBuffer;
    protected float texture[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    protected FloatBuffer vertexBuffer;
    protected float vertices[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    protected boolean textureLoaded = false;
    protected int[] textureIdentifier = new int[1];

    public CALayerTexture() {
        setVertexBufferNeedsUpdate();
        setTextureBufferNeedsUpdate();
    }

    protected void loadTexture(GL10 gl) {}

    protected void draw(GL10 gl) {
        loadTexture(gl);
    }

    protected void setVertexBufferNeedsUpdate() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    protected void setTextureBufferNeedsUpdate() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    protected void resetVertices() {
        vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f,  0.0f,
                1.0f,  -1.0f, 0.0f,
                1.0f,  1.0f,  0.0f
        };
    }

    protected void setTransform(CATransform3D transform, CGPoint anchorPoint, CGRect frame, CGRect windowBounds) {
        CGRect bounds = new CGRect(0, 0, frame.width, frame.height);
        bounds.x = -frame.width * anchorPoint.x;
        bounds.y = -frame.height * anchorPoint.y;
        float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx - bounds.x;
        float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty - bounds.y;
        float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx - bounds.x;
        float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty - bounds.y;
        float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x;
        float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y;
        float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x;
        float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y;
        vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
        vertices[4] = 1.0f - (lty / windowBounds.height * 2);
        vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
        vertices[10] = 1.0f - (rty / windowBounds.height * 2);
        vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
        vertices[1] = 1.0f - (lby / windowBounds.height * 2);
        vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
        vertices[7] = 1.0f - (rby / windowBounds.height * 2);
    }

}