package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SurfaceActivity extends Activity implements SurfaceHolder.Callback{

    private static final String TAG = "SurfaceActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private TestDecoder test;
    private ImageView img;
    private int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("实时视频");

        img = (ImageView) findViewById(R.id.surface_img);

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
        height = manager.getDefaultDisplay().getHeight();

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        test = new TestDecoder(surfaceHolder);
//      test.startRequest();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        test.stopRequest();
    }

    public void playVideo(View view) {
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
        img.setVisibility(View.INVISIBLE);
        Toast toast = Toast.makeText(SurfaceActivity.this, "请等待...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,height/4);
        toast.show();

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
