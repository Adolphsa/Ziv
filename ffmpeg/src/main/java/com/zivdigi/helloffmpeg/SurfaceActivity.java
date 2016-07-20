package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class SurfaceActivity extends Activity implements SurfaceHolder.Callback{

    private static final String TAG = "SurfaceActivity";
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private ZivPlayer player;
    private FindNalThread findNalThread;
    private RtspThread rtspThread;

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
        surfaceView.setZOrderOnTop(true);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

        player = new ZivPlayer(ZivPlayer.COLOR_FORMAT_RGB565LE);
        rtspThread = new RtspThread(player);
        findNalThread = new FindNalThread(surfaceHolder,width,player);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        rtspThread.start();
        findNalThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        findNalThread.setIsRunning(false);
    }

//    public void play(View view) {
//        Log.i(TAG, "播放");
//        player.startStream(RtspThread.RTSP_NEW);
//    }
//
//    public void stop(View view){
//        Log.i(TAG, "停止");
//        player.stopStream(RtspThread.RTSP_NEW);
//
//    }

}
