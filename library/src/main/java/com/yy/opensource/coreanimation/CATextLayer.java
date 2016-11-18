package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.text.TextPaint;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/11/18.
 */

public class CATextLayer extends CALayer {

    public void setText(String text) {
        this.text = text;
        needsDisplay = true;
    }

    public void setPaint(TextPaint paint) {
        this.paint = paint;
        needsDisplay = true;
    }

    private String text;
    private TextPaint paint;
    private boolean needsDisplay = true;
    private Bitmap sharedBitmap;
    private Canvas sharedCanvas;

    @Override
    protected void drawContents(GL10 gl) {
        if (frame.width <= 0 || frame.height <= 0) {
            return;
        }
        if (needsDisplay && text != null && paint != null) {
            Bitmap bitmap;
            Canvas canvas;
            Boolean reusing = false;
            if (sharedBitmap != null && sharedBitmap.getWidth() == frame.width && sharedBitmap.getHeight() == frame.height) {
                bitmap = sharedBitmap;
                canvas = sharedCanvas;
                reusing = true;
            }
            else {
                bitmap = Bitmap.createBitmap((int)frame.width, (int)frame.height, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bitmap);
                sharedBitmap = bitmap;
                sharedCanvas = canvas;
            }
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int x = (int)(((frame.width - bounds.width()) / 2.0));
            int targetRectTop = 0;
            int targetRectBottom = (int)frame.height;
            int fonrMetricsBottom = (int)paint.getFontMetrics().bottom;
            int fonrMetricsTop = (int)paint.getFontMetrics().top;
            int y = (targetRectBottom + targetRectTop - fonrMetricsBottom - fonrMetricsTop) / 2;
            canvas.drawText(text, x, y, paint);
            this.setContents(bitmap);
            if (reusing) {
                CALayerTexture.textureChanged.put(bitmap.toString(), true);
            }
        }
        super.drawContents(gl);
        needsDisplay = false;
    }


}
