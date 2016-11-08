package com.yy.opensource.coreanimation;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/11/4.
 */

class CALayerMask {

    static private float vertices[] = {
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    static void drawMask(CALayer layer, GL10 gl) {
        boolean enabled = false;
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glClearStencil(0);
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);
        FloatBuffer lastBuffer = null;
        CALayer current = layer;
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, CGColor.whiteColor.colorBuffer);
        while (current != null) {
            if (current.masksToBounds) {
                enabled = true;
                if (lastBuffer != null) {
                    gl.glStencilOp(GL10.GL_ZERO, GL10.GL_ZERO, GL10.GL_ZERO);
                    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lastBuffer);
                    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
                }
                FloatBuffer vertexBuffer = CALayerHelper.requestVertexBuffer(CALayerHelper.combineTransform(current), current.anchorPoint, current.frame, current.windowBounds);
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
                gl.glStencilFunc(GL10.GL_ALWAYS, 1, 1);
                gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
                if (current.mask != null) {
                    current.mask.superLayer = current;
                    current.mask.surfaceView = current.surfaceView;
                    current.mask.windowBounds = current.windowBounds;
                    current.mask.drawContents(gl);
                }
                else {
                    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
                }
                lastBuffer = vertexBuffer;
            }
            current = current.superLayer;
        }
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorMask(true, true, true, true);
        gl.glDepthMask(true);
        gl.glStencilFunc(GL10.GL_EQUAL, 1, 1);
        gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
        if (!enabled) {
            gl.glDisable(GL10.GL_STENCIL_TEST);
        }
    }

}
