package com.yy.opensource.coreanimation.sample;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.yy.opensource.coreanimation.CAAnimation;
import com.yy.opensource.coreanimation.CAAnimationDelegate;
import com.yy.opensource.coreanimation.CABasicAnimation;
import com.yy.opensource.coreanimation.CADisplayLink;
import com.yy.opensource.coreanimation.CADisplayLinkDelegate;
import com.yy.opensource.coreanimation.CALayer;
import com.yy.opensource.coreanimation.CAShapeLayer;
import com.yy.opensource.coreanimation.CASurfaceView;
import com.yy.opensource.coreanimation.CATransform3D;
import com.yy.opensource.coreanimation.CGColor;
import com.yy.opensource.coreanimation.CGPoint;
import com.yy.opensource.coreanimation.CGRect;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    CADisplayLink displayLink;
    int degree = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final CASurfaceView surfaceView = new CASurfaceView(this);
        surfaceView.disableAutoRefresh();
        surfaceView.setZOrderOnTop(true);

        final CAShapeLayer shapeLayer = new CAShapeLayer();
        shapeLayer.frame = new CGRect(0,0,250,350);
        Path path = new Path();
        path.addCircle(150, 150, degree, Path.Direction.CCW);
        shapeLayer.setPath(path);
        shapeLayer.fillColor = new CGColor(255,255,255);

//        surfaceView.shapeLayer.addSublayer(shapeLayer);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_alpha_half);
        final CALayer testLayer = new CALayer();
        testLayer.setContents(bitmap);
//        testLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
        testLayer.opacity = 1.0f;
        testLayer.frame = new CGRect(0,0,250,350);
//        testLayer.masksToBounds = true;
//        testLayer.transform.postScale(3.0f, 3.0f);
//        shapeLayer.transform.reset().setMatrix(testLayer.transform.requestMatrix());
//        testLayer.contentsGravity = "resizeAspectFill";
        testLayer.mask = shapeLayer;
        testLayer.masksToBounds = true;

//        surfaceView.shapeLayer.backgroundColor = new CGColor(255,255,0);
        surfaceView.layer.addSublayer(testLayer);

        displayLink = new CADisplayLink();
        displayLink.setHandler(new CADisplayLinkDelegate() {
            @Override
            public void onDrawFrame() {
                degree++;
                if (degree > 100) {
                    degree = 0;
                }
                Path path = new Path();
                path.addCircle(150, 150, degree, Path.Direction.CCW);
                shapeLayer.setPath(path);
                shapeLayer.setNeedsDisplay();
            }
        });

//
//
////        Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.masktest);
////        Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_square_mask);
//        Bitmap maskBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_star_mask);
//        CALayer maskLayer = new CALayer();
//        maskLayer.frame = new CGRect(0,0,150,250);
//        maskLayer.setContents(maskBitmap);
//        testLayer.mask = maskLayer;
//
//        CALayer sLayer = new CALayer();
//        sLayer.frame = new CGRect(150, 250, 150, 250);
//        sLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
//        sLayer.masksToBounds = false;
////        sLayer.setContents(bitmap);
//        testLayer.addSublayer(sLayer);
////
////        CALayer ssLayer = new CALayer();
////        ssLayer.frame = new CGRect(150, 250, 150, 250);
////        ssLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
////        ssLayer.setContents(bitmap);
////        sLayer.addSublayer(ssLayer);
////
////        sLayer.removeFromSuperLayer();

        surfaceView.setNeedsDisplay();


//        displayLink = new CADisplayLink();
//        displayLink.setHandler(new CADisplayLinkDelegate() {
//            @Override
//            public void onDrawFrame() {
//                degree++;
//                testLayer.transform.reset().postRotate(degree);
//                testLayer.setNeedsDisplay();
//            }
//        });

        surfaceView.setBackgroundColor(Color.BLACK);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }
}
