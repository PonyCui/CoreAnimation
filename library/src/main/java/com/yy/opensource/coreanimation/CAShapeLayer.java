package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/11/4.
 */

public class CAShapeLayer extends CALayer {

    public Path path;

    public CGColor fillColor = null;

    public CGColor strokeColor = null;

    public float lineWidth = 0.0f;

    public float miterLimit = 0.0f;

    public String lineCap;

    public String lineJoin;

    public void setNeedsDisplay() {
        this.contents = null;
    }

    @Override
    protected void drawContents(GL10 gl) {
        if (this.contents == null && path != null) {
            Bitmap bitmap = Bitmap.createBitmap((int)frame.width, (int)frame.height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            if (fillColor != null) {
                canvas.drawPath(path, requestFillPaint());
            }
            if (lineWidth > 0) {
                canvas.drawPath(path, requestLinePaint());
            }
            this.contents = bitmap;
        }
        super.drawContents(gl);
    }

    static private Paint mPaint = new Paint();

    Paint requestFillPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        if (fillColor != null) {
            mPaint.setARGB((int)(fillColor.getA() * 255), (int)(fillColor.getR() * 255), (int)(fillColor.getG() * 255), (int)(fillColor.getB() * 255));
        }
        return mPaint;
    }

    Paint requestLinePaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        if (strokeColor != null) {
            mPaint.setARGB((int)(strokeColor.getA() * 255), (int)(strokeColor.getR() * 255), (int)(strokeColor.getG() * 255), (int)(strokeColor.getB() * 255));
        }
        if (lineWidth != 0.0) {
            mPaint.setStrokeWidth(lineWidth);
        }
        if (lineCap != null) {
            if (lineCap.equalsIgnoreCase("butt")) {
                mPaint.setStrokeCap(Paint.Cap.BUTT);
            }
            else if (lineCap.equalsIgnoreCase("round")) {
                mPaint.setStrokeCap(Paint.Cap.ROUND);
            }
            else if (lineCap.equalsIgnoreCase("square")) {
                mPaint.setStrokeCap(Paint.Cap.SQUARE);
            }
        }
        if (lineJoin != null) {
            if (lineJoin.equalsIgnoreCase("miter")) {
                mPaint.setStrokeJoin(Paint.Join.MITER);
            }
            else if (lineJoin.equalsIgnoreCase("round")) {
                mPaint.setStrokeJoin(Paint.Join.ROUND);
            }
            else if (lineJoin.equalsIgnoreCase("bevel")) {
                mPaint.setStrokeJoin(Paint.Join.BEVEL);
            }
        }
        if (miterLimit != 0.0) {
            mPaint.setStrokeMiter(miterLimit);
        }
        return mPaint;
    }

}
