package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SurfaceActivity extends Activity implements SurfaceHolder.Callback{

    private static final String TAG = "SurfaceActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ViewGroup.LayoutParams params;
    private TestDecoder test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("实时视频");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = manager.getDefaultDisplay().getWidth();

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();

//        surfaceView.setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        test = new TestDecoder(surfaceHolder);
//        test.startRequest();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        test.stopRequest();
    }

    public void play(View view) {
        Log.i(TAG, "播放");
        Log.v("HelloFFMPEG", "onStartButtonClick");
        if (test == null) {
            return;
        }

        if (test.isPlaying()) {
            Log.v("HelloFFMPEG", "Already start playing");
            return;
        }

        test.startRequest();

    }

    public void stop(View view){
        Log.i(TAG, "停止");
        Log.v("HelloFFMPEG", "onStopButtonClick");

        if(test == null)
        {
            return;
        }

        if(test.isPlaying() == false)
        {
            Log.v("HelloFFMPEG", "Already stop playing");
            return;
        }

        test.stopRequest();

    }
}
