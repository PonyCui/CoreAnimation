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
import com.yy.opensource.coreanimation.CGPoint;
import com.yy.opensource.coreanimation.CGRect;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    CALayer lll;
    CASurfaceView sss;
    int i = 0;


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
        testLayer.frame = new CGRect(100,100,150,250);

        Matrix matrix = testLayer.transform.requestMatrix();
//        matrix.postRotate(i);
//        matrix.postTranslate(-100,-100);
//        matrix.postSkew(1.0f, 0.0f);
//        matrix.postScale(0.5f, 0.5f);
        testLayer.transform.setMatrix(matrix);
//        testLayer.anchorPoint = new CGPoint(0,0);

        lll = testLayer;
        sss = surfaceView;
        surfaceView.layer.addSublayer(testLayer);

//        CALayer sLayer = new CALayer();
//        sLayer.frame = new CGRect(0, 0, 300 / 3, 500 / 3);
//        sLayer.setContents(bitmap);
//        testLayer.addSublayer(sLayer);

        surfaceView.setNeedsDisplay();


//        tick();


        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }

    void tick() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                i++;
                Matrix matrix = lll.transform.requestMatrix();
                matrix.reset();
//                matrix.postTranslate(-75, -125);
                matrix.postRotate(i);
                lll.transform.setMatrix(matrix);
                sss.setNeedsDisplay();
                tick();
            }
        }, 16);
    }
}
