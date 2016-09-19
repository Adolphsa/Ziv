package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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
    private int width;
    private FrameLayout frameLayout;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    img.setVisibility(View.VISIBLE);
                    showToast(SurfaceActivity.this,"加载库文件出错");
                    break;
                case 2:
                    img.setVisibility(View.VISIBLE);
                    showToast(SurfaceActivity.this,"无法连接设备");
                    break;
                case 3:
                    img.setVisibility(View.VISIBLE);
                    showToast(SurfaceActivity.this,"网络断开");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("实时视频");

        img = (ImageView) findViewById(R.id.surface_img);
//        frameLayout = (FrameLayout) findViewById(R.id.ffmpeg_fl);
//        frameLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("FrameLayout被点击了");
//            }
//        });

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
        width = manager.getDefaultDisplay().getWidth();

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        test = new TestDecoder(surfaceHolder,width,SurfaceActivity.this,handler);
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
        showToast(SurfaceActivity.this, "请等待...");


    }

    public void playPause(View view){
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

    public void showToast(Context context, String str){
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
