package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/11/3.
 */

public class CGSize {

    public float width;
    public float height;

    public CGSize(float width,float height) {
        this.width = width;
        this.height = height;
    }

    public CGSize(int width,int height) {
        this.width = (float)width;
        this.height = (float)height;
    }

}
