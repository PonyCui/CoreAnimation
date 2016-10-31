package com.yy.opensource.coreanimation.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
        surfaceView.FPS = 15;
        surfaceView.setZOrderOnTop(true);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body);
        CALayer testLayer = new CALayer();
        testLayer.setContents(bitmap);
        testLayer.frame = new CGRect(100,100,231,258);
        surfaceView.layer.addSublayer(testLayer);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.addView(surfaceView);
        frameLayout.setBackgroundColor(Color.BLACK);
        setContentView(frameLayout);
    }
}
