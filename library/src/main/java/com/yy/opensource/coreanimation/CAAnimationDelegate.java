package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/11/7.
 */

public interface CAAnimationDelegate {

    void animationDidStart(CAAnimation animation);
    void animationDidStop(CAAnimation animation, boolean finished);

}
