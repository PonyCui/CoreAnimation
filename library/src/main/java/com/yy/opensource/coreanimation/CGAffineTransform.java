package com.yy.opensource.coreanimation;

import android.graphics.Matrix;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CGAffineTransform {

    public float a = 1.0f;
    public float b = 0.0f;
    public float c = 0.0f;
    public float d = 1.0f;
    public float tx = 0.0f;
    public float ty = 0.0f;

    public float[] request2DMatrix() {
        return new float[] {
                a,  c,  tx,
                b,  d,  ty,
                0,  0,  1
        };
    }

    public Matrix requestMatrix() {
        Matrix matrix = new Matrix();
        matrix.setValues(request2DMatrix());
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        this.a = values[0];
        this.b = values[3];
        this.c = values[1];
        this.d = values[4];
        this.tx = values[2];
        this.ty = values[5];
    }

    public CGAffineTransform reset() {
        a = 1.0f;
        b = 0.0f;
        c = 0.0f;
        d = 1.0f;
        tx = 0.0f;
        ty = 0.0f;
        return this;
    }

    public CGAffineTransform postScale(float x, float y) {
        Matrix matrix = requestMatrix();
        matrix.postScale(x, y);
        setMatrix(matrix);
        return this;
    }

    public CGAffineTransform postRotate(float degrees) {
        Matrix matrix = requestMatrix();
        matrix.postRotate(degrees);
        setMatrix(matrix);
        return this;
    }

    public CGAffineTransform postSkew(float kx, float ky) {
        Matrix matrix = requestMatrix();
        matrix.postSkew(kx, ky);
        setMatrix(matrix);
        return this;
    }

    public CGAffineTransform postTranslate(float dx, float dy) {
        Matrix matrix = requestMatrix();
        matrix.postTranslate(dx, dy);
        setMatrix(matrix);
        return this;
    }

    public CGAffineTransform postTransform(CGAffineTransform transform) {
        Matrix matrix = requestMatrix();
        matrix.postConcat(transform.requestMatrix());
        setMatrix(matrix);
        return this;
    }

    public boolean isIdentity() {
        return this.a == 1.0 && this.b == 0.0 && this.c == 0.0 && this.d == 1.0 && this.tx == 0.0 && this.ty == 0.0;
    }

}
