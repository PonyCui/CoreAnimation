package com.yy.opensource.coreanimation.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.yy.opensource.coreanimation.CALayer;
import com.yy.opensource.coreanimation.CAShapeLayer;
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
        surfaceView.setZOrderOnTop(true);

        CAShapeLayer layer = new CAShapeLayer();
        layer.frame = new CGRect(0,0,150,250);
        Path path = new Path();
        path.addCircle(50, 50, 200, Path.Direction.CCW);
        layer.path = path;
        layer.fillColor = new CGColor(255,255,255);
//        surfaceView.layer.addSublayer(layer);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        final CALayer testLayer = new CALayer();
        testLayer.setContents(bitmap);
        testLayer.backgroundColor = new CGColor(0.5f, 0.5f, 0.5f);
        testLayer.opacity = 1.0f;
        testLayer.frame = new CGRect(0,0,150,250);
        testLayer.masksToBounds = true;
        testLayer.contentsGravity = "resizeAspectFill";
        testLayer.mask = layer;
        surfaceView.layer.addSublayer(testLayer);
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

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }
}
