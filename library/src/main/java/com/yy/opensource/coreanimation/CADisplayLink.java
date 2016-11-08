package com.yy.opensource.coreanimation;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

import java.util.Timer;
import java.util.TimerTask;

public class CADisplayLink {

    public int frameInterval = 1;

    private CADisplayLinkDelegate mHandler;
    private int frameSkipped = 0;

    public void setHandler(final CADisplayLinkDelegate handler) {
        this.mHandler = handler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void doFrame(long l) {
                    frameSkipped++;
                    if (frameSkipped < frameInterval) {
                        Choreographer.getInstance().postFrameCallback(this);
                        return;
                    }
                    if (mHandler != null) {
                        mHandler.onDrawFrame();
                        Choreographer.getInstance().postFrameCallback(this);
                    }
                    frameSkipped = 0;
                }
            };
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
        else {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frameSkipped++;
                    if (frameSkipped < frameInterval) {
                        return;
                    }
                    if (mHandler != null) {
                        mHandler.onDrawFrame();
                    }
                    else {
                        timer.cancel();
                    }
                    frameSkipped = 0;
                }
            }, 16, 16);
        }
    }

    public void invalidate() {
        mHandler = null;
    }

}