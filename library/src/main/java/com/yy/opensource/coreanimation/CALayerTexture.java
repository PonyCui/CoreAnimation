package com.yy.opensource.coreanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/10/31.
 */

class CALayerTexture {

    static private HashMap<String, CABitmapTextureEntity> textureCaches = new HashMap<>();
    static protected HashMap<String, Boolean> textureChanged = new HashMap<>();

    static private FloatBuffer textureBuffer;

    static private  float texture[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    static private float vertices[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    static protected void init() {
        if (textureBuffer != null) {
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    static protected void retainBitmap(Bitmap bitmap) {
        CABitmapTextureEntity item = textureCaches.get(bitmap.toString());
        if (item != null) {
            item.retain();
        }
        else {
            item = new CABitmapTextureEntity();
            item.bitmap = bitmap;
            item.retain();
            textureCaches.put(bitmap.toString(), item);
        }
    }

    static protected void releaseBitmap(Bitmap bitmap) {
        CABitmapTextureEntity item = textureCaches.get(bitmap.toString());
        if (item != null) {
            item.release();
        }
    }

    static void drawTextures(CALayer layer, GL10 gl) {
        init();
        if (layer.contents != null) {
            CABitmapTextureEntity item = textureCaches.get(layer.contents.toString());
            if (item == null) {
                item = new CABitmapTextureEntity();
                item.bitmap = layer.contents;
                textureCaches.put(layer.contents.toString(), item);
            }
            item.loadTexture(gl);
            if (textureChanged.get(item.bitmap.toString()) != null && textureChanged.get(item.bitmap.toString()) == true) {
                item.updateTexture(gl);
                textureChanged.remove(item.bitmap.toString());
            }
            if (!item.loaded) {
                return;
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, item.textureID[0]);
            enableTextureFeatures(layer, gl);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, CALayerHelper.requestContentVertexBuffer(CALayerHelper.combineTransform(layer), layer.anchorPoint, layer.frame, layer.windowBounds, layer));
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            float alpha = CALayerHelper.combineOpacity(layer);
            gl.glColor4f(1.0f * alpha, 1.0f * alpha, 1.0f * alpha, alpha);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            disableTextureFeatures(layer, gl);
        }
        gc(gl);
    }

    static private void enableTextureFeatures(CALayer layer, GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        if (!layer.opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    static private void disableTextureFeatures(CALayer layer, GL10 gl) {
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        if (!layer.opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    static private void gc(GL10 gl) {
        int rand =(int)(Math.random() * 11);
        if (rand == 0) {
            HashMap<String, CABitmapTextureEntity> newValues = new HashMap<>();
            for (Map.Entry<String, CABitmapTextureEntity> entry : textureCaches.entrySet()) {
                if (entry.getValue().retainCount <= 0) {
                    entry.getValue().deleteTexture(gl);
                    continue;
                }
                else {
                    newValues.put(entry.getKey(), entry.getValue());
                }
            }
            textureCaches = newValues;
        }
    }

}

class CABitmapTextureEntity {

    protected int[] textureID = new int[1];
    protected Bitmap bitmap = null;
    protected boolean loaded = false;
    protected int retainCount = 0;


    protected void loadTexture(GL10 gl) {
        if (loaded) {
            return;
        }
        if (null != bitmap && !bitmap.isRecycled()) {
            gl.glGenTextures(1, textureID, 0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            loaded = true;
        }
    }

    protected void updateTexture(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    }

    protected void deleteTexture(GL10 gl) {
        if (!loaded) {
            return;
        }
        gl.glDeleteTextures(1, textureID, 0);
        loaded = false;
    }

    protected void retain() {
        retainCount++;
    }

    protected void release() {
        retainCount--;
    }

}