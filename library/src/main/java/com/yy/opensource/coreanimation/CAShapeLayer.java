package com.yy.opensource.coreanimation;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/11/4.
 */

public class CAShapeLayer extends CALayer {

    @Override
    protected void drawContents(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        float angle = (float)(2 * 3.14 / 100.0);
        gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
    }

}
