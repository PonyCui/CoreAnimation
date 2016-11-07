package com.yy.opensource.coreanimation;

/**
 * Created by cuiminghui on 2016/11/7.
 */

public class CAPropertyAnimation extends CAAnimation {

    /** The key-path describing the property to be animated. */
    public String keyPath;

    /** When true the value specified by the animation will be "added" to
     * the current presentation value of the property to produce the new
     * presentation value. The addition function is type-dependent, e.g.
     * for affine transforms the two matrices are concatenated. Defaults to
     * NO.
     */
    public boolean isAdditive = false;

    /** The `cumulative' property affects how repeating animations produce
     * their result. If true then the current value of the animation is the
     * value at the end of the previous repeat cycle, plus the value of the
     * current repeat cycle. If false, the value is simply the value
     * calculated for the current repeat cycle. Defaults to NO.
     */
    public boolean isCumulative = false;

    public void CAPropertyAnimation(String keyPath) {
        this.keyPath = keyPath;
    }

}
