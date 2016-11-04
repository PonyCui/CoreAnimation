package com.yy.opensource.coreanimation;

import android.graphics.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by cuiminghui on 2016/11/4.
 */

final class CALayerHelper {

    static float combineOpacity(CALayer layer) {
        float opacity = layer.opacity;
        CALayer currentLayer = layer.superLayer;
        while (null != currentLayer) {
            opacity *= currentLayer.opacity;
            currentLayer = currentLayer.superLayer;
        }
        return opacity;
    }

    static CATransform3D combineTransform(CALayer layer) {
        Matrix matrix = layer.transform.requestMatrix();
        matrix.postTranslate(layer.frame.x, layer.frame.y);
        CALayer currentLayer = layer.superLayer;
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

    static FloatBuffer requestVertexBuffer(CATransform3D transform, CGPoint anchorPoint, CGRect frame, CGRect windowBounds) {
        float[] vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                -1.0f, 1.0f,  0.0f,
                1.0f,  -1.0f, 0.0f,
                1.0f,  1.0f,  0.0f
        };
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
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    static FloatBuffer requestContentVertexBuffer(CATransform3D transform, CGPoint anchorPoint, CGRect frame, CGRect windowBounds, CALayer layer) {
        if (layer.contentsGravity.equals("resizeAspect")) {
            float[] vertices = new float[] {
                    -1.0f, -1.0f, 0.0f,
                    -1.0f, 1.0f,  0.0f,
                    1.0f,  -1.0f, 0.0f,
                    1.0f,  1.0f,  0.0f
            };
            CGRect bounds;
            float moveX = 0, moveY = 0;
            if (frame.width / frame.height >= layer.contentSize.width / layer.contentSize.height) {
                // vertical
                bounds = new CGRect(
                        0,
                        0,
                        layer.contentSize.width / layer.contentSize.height * frame.height,
                        frame.height);
                moveX = (float)((frame.width - layer.contentSize.width / layer.contentSize.height * frame.height) / 2.0);
            }
            else {
                // horizon
                bounds = new CGRect(
                        0,
                        0,
                        frame.width,
                        layer.contentSize.height / layer.contentSize.width * frame.width);
                moveY = (float)((frame.height - layer.contentSize.height / layer.contentSize.width * frame.width) / 2.0);
            }
            bounds.x = -frame.width * anchorPoint.x;
            bounds.y = -frame.height * anchorPoint.y;
            float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
            vertices[4] = 1.0f - (lty / windowBounds.height * 2);
            vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
            vertices[10] = 1.0f - (rty / windowBounds.height * 2);
            vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
            vertices[1] = 1.0f - (lby / windowBounds.height * 2);
            vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
            vertices[7] = 1.0f - (rby / windowBounds.height * 2);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
            return vertexBuffer;
        }
        else if (layer.contentsGravity.equals("resizeAspectFill")) {
            float[] vertices = new float[] {
                    -1.0f, -1.0f, 0.0f,
                    -1.0f, 1.0f,  0.0f,
                    1.0f,  -1.0f, 0.0f,
                    1.0f,  1.0f,  0.0f
            };
            CGRect bounds;
            float moveX = 0, moveY = 0;
            if (frame.width / frame.height >= layer.contentSize.width / layer.contentSize.height) {
                // horizon
                bounds = new CGRect(
                        0,
                        0,
                        frame.width,
                        frame.width / (layer.contentSize.width / layer.contentSize.height));
                moveY = (float)((frame.height - bounds.height) / 2.0);
            }
            else {
                // vertical
                bounds = new CGRect(
                        0,
                        0,
                        frame.height * (layer.contentSize.width / layer.contentSize.height),
                        frame.height);
                moveX = (float)((frame.width - bounds.width) / 2.0);
            }
            bounds.x = -frame.width * anchorPoint.x;
            bounds.y = -frame.height * anchorPoint.y;
            float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
            vertices[4] = 1.0f - (lty / windowBounds.height * 2);
            vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
            vertices[10] = 1.0f - (rty / windowBounds.height * 2);
            vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
            vertices[1] = 1.0f - (lby / windowBounds.height * 2);
            vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
            vertices[7] = 1.0f - (rby / windowBounds.height * 2);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
            return vertexBuffer;
        }
        else {
            return requestVertexBuffer(transform, anchorPoint, frame, windowBounds);
        }
    }

}
