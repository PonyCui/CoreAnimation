package com.yy.opensource.coreanimation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by cuiminghui on 2016/11/2.
 */

public class CGColor {

    static public CGColor whiteColor = new CGColor(255, 255, 255);
    static public CGColor blackColor = new CGColor(0, 0, 0);
    static public CGColor clearColor = new CGColor(0, 0, 0, 0);

    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private float a = 0.0f;

    FloatBuffer colorBuffer;

    public CGColor() {
        resetBuffer();
    }

    public CGColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        resetBuffer();
    }

    public CGColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 1.0f;
        resetBuffer();
    }

    public CGColor(int r, int g, int b, float a) {
        this.r = (float)r / 255.0f;
        this.g = (float)g / 255.0f;
        this.b = (float)b / 255.0f;
        this.a = a;
        resetBuffer();
    }

    public CGColor(int r, int g, int b) {
        this.r = (float)r / 255.0f;
        this.g = (float)g / 255.0f;
        this.b = (float)b / 255.0f;
        this.a = 1.0f;
        resetBuffer();
    }

    public CGColor colorWithAlpha(float a) {
        return new CGColor(r, g, b, a);
    }

    public boolean isClearColor() {
        return this.a <= 0.0f;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }

    public float getR() {
        return r;
    }

    private void resetBuffer() {
        float[] colors = {
                r, g, b, a,
                r, g, b, a,
                r, g, b, a,
                r, g, b, a,
        };
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuffer.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

}
