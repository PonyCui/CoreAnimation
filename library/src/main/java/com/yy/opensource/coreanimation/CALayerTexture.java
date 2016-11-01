package com.yy.opensource.coreanimation;

import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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

    void loadTexture(GL10 gl) {}

    void draw(GL10 gl) {
        loadTexture(gl);
    }

    void setVertexBufferNeedsUpdate() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    void setTextureBufferNeedsUpdate() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    void resetVertices() {
        vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f,  0.0f,
                1.0f,  -1.0f, 0.0f,
                1.0f,  1.0f,  0.0f
        };
    }

    void setFrame(CGRect frame, CGRect windowBounds) {
        float originX = frame.x / windowBounds.width * 2;
        float sizeX = frame.width / windowBounds.width * 2;
        vertices[0] = -1.0f + originX;
        vertices[6] = -1.0f + originX + sizeX;
        vertices[3] = -1.0f + originX;
        vertices[9] = -1.0f + originX + sizeX;
        float originY = frame.y / windowBounds.height * 2;
        float sizeY = frame.height / windowBounds.height * 2;
        vertices[1] = 1.0f - originY - sizeY;
        vertices[4] = 1.0f - originY;
        vertices[7] = 1.0f - originY - sizeY;
        vertices[10] = 1.0f - originY;
    }

    void setTransform(CATransform3D transform, CGRect frame, CGRect windowBounds) {
        CGRect bounds = new CGRect(0, 0, frame.width, frame.height);
        float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx + frame.x;
        float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty + frame.y;
        float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx + frame.x;
        float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty + frame.y;
        float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx + frame.x;
        float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty + frame.y;
        float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + frame.height) + transform.tx + frame.x;
        float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + frame.height) + transform.ty + frame.y;
        vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
        vertices[4] = 1.0f - (lty / windowBounds.height * 2);
        vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
        vertices[10] = 1.0f - (rty / windowBounds.height * 2);
        vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
        vertices[1] = 1.0f - (lby / windowBounds.height * 2);
        vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
        vertices[7] = 1.0f - (rby / windowBounds.height * 2);

//        float[] originVertices = vertices.clone();
//        vertices[0] = transform.a * originVertices[0] + (-transform.c) * originVertices[1] + transform.tx / windowBounds.width * 2;
//        vertices[1] = (-transform.b) * originVertices[0] + transform.d * originVertices[1] - transform.ty / windowBounds.height * 2;
//        vertices[3] = transform.a * originVertices[3] + (-transform.c) * originVertices[4] + transform.tx / windowBounds.width * 2;
//        vertices[4] = (-transform.b) * originVertices[3] + transform.d * originVertices[4] - transform.ty / windowBounds.height * 2;
//        vertices[6] = transform.a * originVertices[6] + (-transform.c) * originVertices[7] + transform.tx / windowBounds.width * 2;
//        vertices[7] = (-transform.b) * originVertices[6] + transform.d * originVertices[7] - transform.ty / windowBounds.height * 2;
//        vertices[9] = transform.a * originVertices[9] + (-transform.c) * originVertices[10] + transform.tx / windowBounds.width * 2;
//        vertices[10] = (-transform.b) * originVertices[9] + transform.d * originVertices[10] - transform.ty / windowBounds.height * 2;

//        // scaleX
//        vertices[6] = (vertices[6] - vertices[0]) * transform.a + vertices[0];
//        vertices[9] = (vertices[9] - vertices[3]) * transform.a + vertices[3];
//        // scaleY
//        vertices[1] = vertices[4] - (vertices[4] - vertices[1]) * transform.d;
//        vertices[7] = vertices[10] - (vertices[10] - vertices[7]) * transform.d;
//        // skewX
//        vertices[0] += (vertices[9] - vertices[3]) * transform.c;
//        vertices[6] += (vertices[9] - vertices[3]) * transform.c;
//        // skewY
//        vertices[10] -= (vertices[4] - vertices[1]) * transform.b;
//        vertices[7] -= (vertices[4] - vertices[1]) * transform.b;
//        // translateX
//        vertices[0] += transform.tx / windowBounds.width * 2;
//        vertices[6] += transform.tx / windowBounds.width * 2;
//        vertices[3] += transform.tx / windowBounds.width * 2;
//        vertices[9] += transform.tx / windowBounds.width * 2;
//        // translateY
//        vertices[1] -= transform.ty / windowBounds.height * 2;
//        vertices[4] -= transform.ty / windowBounds.height * 2;
//        vertices[7] -= transform.ty / windowBounds.height * 2;
//        vertices[10] -= transform.ty / windowBounds.height * 2;
    }

}