package com.yy.opensource.coreanimation;

import android.graphics.CornerPathEffect;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;

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
        CALayer current = layer;
        CALayer target = null;
        while (current != null) {
            if (current.masksToBounds) {
                target = current;
            }
            current = current.superLayer;
        }
        if (target != null) {
            setCornerRadius(target);
            gl.glEnable(GL10.GL_STENCIL_TEST);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glClearStencil(0);
            gl.glColorMask(false, false, false, false);
            gl.glDepthMask(false);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, CGColor.whiteColor.colorBuffer);
            FloatBuffer vertexBuffer = CALayerHelper.requestVertexBuffer(CALayerHelper.combineTransform(target), target.anchorPoint, target.frame, target.windowBounds);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glStencilFunc(GL10.GL_ALWAYS, 1, 1);
            gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
            if (target.mask != null) {
                target.mask.surfaceView = target.surfaceView;
                target.mask.windowBounds = target.windowBounds;
                target.mask.transform = CALayerHelper.combineTransform(target);
                target.mask.drawContents(gl);
            }
            else {
                gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
            }
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glColorMask(true, true, true, true);
            gl.glDepthMask(true);
            gl.glStencilFunc(GL10.GL_EQUAL, 1, 1);
            gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
        }
        else {
            gl.glClearStencil(0);
            gl.glDisable(GL10.GL_STENCIL_TEST);
        }
    }

    static void setCornerRadius(CALayer layer) {
        if (layer.oldCornerRadius == layer.cornerRadius) {
            return;
        }
        if (layer.mask == null && layer.cornerRadius > 0.0) {
            CAShapeLayer shapeLayer = new CAShapeLayer();
            shapeLayer.frame = new CGRect(0,0,layer.frame.width, layer.frame.height);
            shapeLayer.fillColor = CGColor.whiteColor;
            shapeLayer.setPath(roundedRect(0,0,layer.frame.width, layer.frame.height, layer.cornerRadius,layer.cornerRadius,false));
            layer.mask = shapeLayer;
        }
        else if (layer.mask != null && layer.cornerRadius == 0.0 && layer.oldCornerRadius > 0.0) {
            layer.mask = null;
        }
        layer.oldCornerRadius = layer.cornerRadius;
    }

    static public Path roundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));
        path.moveTo(right, top + ry);
        RectF rectF = new RectF();
        rectF.set(right - 2*rx, top, right, top + 2*ry);
        path.arcTo(rectF, 0, -90, false); //top-right-corner
        path.rLineTo(-widthMinusCorners, 0);
        rectF.set(left, top, left + 2*rx, top + 2*ry);
        path.arcTo(rectF, 270, -90, false);//top-left corner.
        path.rLineTo(0, heightMinusCorners);
        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        }
        else {
            rectF.set(left, bottom - 2 * ry, left + 2 * rx, bottom);
            path.arcTo(rectF, 180, -90, false); //bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            rectF.set(right - 2 * rx, bottom - 2 * ry, right, bottom);
            path.arcTo(rectF, 90, -90, false); //bottom-right corner
        }
        path.rLineTo(0, -heightMinusCorners);
        path.close();//Given close, last lineto can be removed.
        return path;
    }

}
