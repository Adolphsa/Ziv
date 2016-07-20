package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity {

    public static final int MSG_SUCCESS = 0;//获取图片成功的标识
    public static final int MSG_FAILURE = 1;//获取图片失败的标识

    private  ImageView image;
    public  TestDecoder test;
    private int bmpUseIndex = 0;
    Bitmap bmp0 = null;
    Bitmap bmp1 = null;
    private Thread t1;
    private ZivPlayer zivPlayer = new ZivPlayer(ZivPlayer.COLOR_FORMAT_RGB565LE);

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    //System.out.println(msg);

                    if(bmpUseIndex == 0){
                        image.setImageBitmap((Bitmap) msg.obj);
                        bmp0 = (Bitmap)(msg.obj);
                        if(bmp1 != null){
                            bmp1.recycle();
                            bmp1 = null;
                        }

                        bmpUseIndex = 1;
                    }
                    else{
                        image.setImageBitmap((Bitmap) msg.obj);
                        bmp1 = (Bitmap)(msg.obj);
                        if(bmp0 != null){
                            bmp0.recycle();
                            bmp0 = null;
                        }

                        bmpUseIndex = 0;
                    }

                    break;
                case MSG_FAILURE:
                    Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ffmpeg_activity_main);

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

        System.out.println("onCreate");
        image = (ImageView) findViewById(R.id.img_pic);

        test = new TestDecoder(mHandler);
        t1 = new Thread(test);
        t1.start();

/*
        final String rtspUrlLocal = "rtsp://192.168.199.30";
        final String rtspUrlPi02 = "rtsp://192.168.199.103:8554/stream0";
        final String rtspUrlCentOs = "rtsp://192.168.199.21:8554/stream0";
        final String rtspUrlDevServer = "rtsp://120.24.174.213:8554/live_1234567890123456789.sdp";
        ZivPlayerView playerView = new ZivPlayerView();
        playerView.initZivPlayerView();
        playerView.setVideoUri(rtspUrlPi02);

        playerView.startPlay();

        Log.v("HelloFFMPEG", "after startPlay()");
*/
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Log.v("HelloFFMPEG", "onConfigurationChanged");
//
//        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            Log.v("HelloFFMPEG", "ORIENTATION_LANDSCAPE");
//        }
//        else{
//            Log.v("HelloFFMPEG", "ORIENTATION_OTHER");
//        }
//
//        setContentView(R.layout.activity_main);
//    }

    public void xuanzhuan(View view){

        //如果是横排,则改为竖排
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        {
            System.out.println("竖屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //如果是竖排,则改为横排
        else if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        {
            System.out.println("横屏");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("HelloFFMPEG", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("HelloFFMPEG", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("HelloFFMPEG", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zivPlayer.stopStream("rtsp://192.168.199.30/stream0");
        test.setStop();
        zivPlayer.nativeDestroy();
        System.out.println("停止了");
    }
}
