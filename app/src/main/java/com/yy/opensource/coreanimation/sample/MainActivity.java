package com.yy.opensource.coreanimation.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.yy.opensource.coreanimation.CALayer;
import com.yy.opensource.coreanimation.CASurfaceView;
import com.yy.opensource.coreanimation.CGColor;
import com.yy.opensource.coreanimation.CGPoint;
import com.yy.opensource.coreanimation.CGRect;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CASurfaceView surfaceView = new CASurfaceView(this);
        surfaceView.disableAutoRefresh();
        surfaceView.FPS = 60;
        surfaceView.setZOrderOnTop(true);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        final CALayer testLayer = new CALayer();
        testLayer.setContents(bitmap);
        testLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
        testLayer.opacity = 1.0f;
        testLayer.frame = new CGRect(0,0,150,250);
        testLayer.masksToBounds = false;
        testLayer.contentsGravity = "resizeAspectFill";
        surfaceView.layer.addSublayer(testLayer);

        CALayer sLayer = new CALayer();
        sLayer.frame = new CGRect(150, 250, 150, 250);
        sLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
        sLayer.masksToBounds = false;
        sLayer.setContents(bitmap);
        testLayer.addSublayer(sLayer);

        CALayer ssLayer = new CALayer();
        ssLayer.frame = new CGRect(150, 250, 150, 250);
        ssLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
        ssLayer.setContents(bitmap);
        sLayer.addSublayer(ssLayer);

//        sLayer.removeFromSuperLayer();

//        testLayer.transform.postScale(0.5f, 0.5f);
//        testLayer.transform.postScale(1.5f, 1.5f);
//        testLayer.transform.postSkew(1.0f, 0.0f);
//        testLayer.transform.postRotate(45);

        surfaceView.setNeedsDisplay();

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }
}
