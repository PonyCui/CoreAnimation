package com.yy.opensource.coreanimation;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/11/4.
 */

class CALayerBackground {

    static private float vertices[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    static void drawBackgroundColor(CALayer layer, GL10 gl) {
        if (layer.backgroundColor == null || layer.backgroundColor.isClearColor()) {
            return;
        }
        enableBackgroundFeatures(layer, gl);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, CALayerHelper.requestVertexBuffer(CALayerHelper.combineTransform(layer), layer.anchorPoint, layer.frame, layer.windowBounds));
        if (CALayerHelper.combineOpacity(layer) < 1.0) {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, layer.backgroundColor.colorWithAlpha(CALayerHelper.combineOpacity(layer)).colorBuffer);
        }
        else {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, layer.backgroundColor.colorBuffer);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        disableBackgroundFeatures(layer, gl);
    }

    static void enableBackgroundFeatures(CALayer layer, GL10 gl) {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        if (!layer.opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ZERO);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    static void disableBackgroundFeatures(CALayer layer, GL10 gl) {
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        if (!layer.opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

}
