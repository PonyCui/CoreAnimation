package com.yy.opensource.coreanimation;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CASurfaceView extends GLSurfaceView {

    private CARenderer renderer;
    public CALayer layer = new CALayer();
    public int FPS = 60;

    public CASurfaceView(Context context) {
        super(context);
        init();
    }

    public CASurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        renderer = new CARenderer(getContext(), this);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setRenderer(renderer);
    }

}
