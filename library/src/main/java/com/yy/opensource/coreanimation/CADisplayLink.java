package com.yy.opensource.coreanimation;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.Choreographer;

import java.util.Timer;
import java.util.TimerTask;

public class CADisplayLink {

    private CADisplayLinkDelegate mHandler;

    public void setHandler(final CADisplayLinkDelegate handler) {
        this.mHandler = handler;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void doFrame(long l) {
                    if (mHandler != null) {
                        mHandler.onDrawFrame();
                        Choreographer.getInstance().postFrameCallback(this);
                    }
                }
            };
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
        else {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mHandler != null) {
                        mHandler.onDrawFrame();
                    }
                    else {
                        timer.cancel();
                    }
                }
            }, 16, 16);
        }
    }

    public void invalidate() {
        mHandler = null;
    }

}