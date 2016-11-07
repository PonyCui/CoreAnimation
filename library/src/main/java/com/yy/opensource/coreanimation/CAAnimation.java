package com.yy.opensource.coreanimation;

public class CAAnimation {

    /** The delegate of the animation. This object is retained for the
     * lifetime of the animation object. Defaults to nil. See below for the
     * supported delegate methods.
     */
    public CAAnimationDelegate delegate = null;

    /** When true, the animation is removed from the render tree once its
     * active duration has passed. Defaults to YES.
     */
    public boolean isRemovedOnCompletion = true;

    /**
     * The basic duration of the object. Defaults to 0.
     */
    public float duration = 0.0f;

    /**
     * Private Methods and Props.
     */

    protected CALayer layer = null;
    protected long beginTime = 0;
    protected CADisplayLink displayLink = null;
    protected boolean markEnded = false;

    protected void start() {
        if (layer == null) {
            return;
        }
        beginTime = System.currentTimeMillis();
        displayLink = new CADisplayLink();
        displayLink.setHandler(new CADisplayLinkDelegate() {
            @Override
            public void onDrawFrame() {
                duration(System.currentTimeMillis() - beginTime);
            }
        });
        if (delegate != null) {
            delegate.animationDidStart(this);
        }
    }

    protected void duration(long currentTimeMillis) {

    }

    protected void end(boolean finished) {
        if (markEnded) {
            return;
        }
        markEnded = true;
        beginTime = 0;
        if (displayLink != null) {
            displayLink.invalidate();
            displayLink = null;
        }
        if (delegate != null) {
            delegate.animationDidStop(this, finished);
        }
    }

}
