package com.zivdigi.helloffmpeg;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



public class MainActivity extends Activity {

    public static final int MSG_SUCCESS = 0;//获取图片成功的标识
    public static final int MSG_FAILURE = 1;//获取图片失败的标识

    private static ImageView image;
    public TestDecoder test;
    private int bmpUseIndex = 0;
    Bitmap bmp0 = null;
    Bitmap bmp1 = null;
    Thread playerThread = null;

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
        Log.v("HelloFFMPEG", "onCreate");

        image = (ImageView) findViewById(R.id.img_pic);
//        test = new TestDecoder(mHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("HelloFFMPEG", "onConfigurationChanged");

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.v("HelloFFMPEG", "ORIENTATION_LANDSCAPE");
        }
        else{
            Log.v("HelloFFMPEG", "ORIENTATION_OTHER");
        }

        setContentView(R.layout.ffmpeg_activity_main);
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

    public void onStartButtonClick(View view) {
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

    public void onStopButtonClick(View view)
    {
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

