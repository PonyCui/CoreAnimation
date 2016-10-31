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
import com.yy.opensource.coreanimation.CGRect;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CASurfaceView surfaceView = new CASurfaceView(this);
        surfaceView.FPS = 60;
        surfaceView.setZOrderOnTop(true);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        CALayer testLayer = new CALayer();
        testLayer.setContents(bitmap);
        testLayer.frame = new CGRect(0,0,150,150);
        Matrix matrix = testLayer.transform.requestMatrix();
        matrix.postRotate(20);
//        matrix.postScale(0.5f, 0.5f);
        testLayer.transform.setMatrix(matrix);
        surfaceView.layer.addSublayer(testLayer);

        CALayer testLayer2 = new CALayer();
        testLayer2.setContents(bitmap);
        testLayer2.frame = new CGRect(0,400,150,150);
        Matrix matrix2 = testLayer2.transform.requestMatrix();
//        matrix.postRotate(45);
//        matrix.postScale(0.5f, 0.5f);
        testLayer2.transform.setMatrix(matrix2);
        surfaceView.layer.addSublayer(testLayer2);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }
}
