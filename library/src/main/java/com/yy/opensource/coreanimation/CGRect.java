package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CGRect {

    public float x;
    public float y;
    public float width;
    public float height;

    public CGRect(float x,float y,float width,float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public CGRect(int x,int y,int width,int height) {
        this.x = (float)x;
        this.y = (float)y;
        this.width = (float)width;
        this.height = (float)height;
    }

}
