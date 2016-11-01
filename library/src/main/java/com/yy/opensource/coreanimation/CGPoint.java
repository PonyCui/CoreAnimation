package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/11/1.
 */

public class CGPoint {

    public float x;
    public float y;

    public CGPoint(float x,float y) {
        this.x = x;
        this.y = y;
    }

    public CGPoint(int x,int y) {
        this.x = (float)x;
        this.y = (float)y;
    }

}
