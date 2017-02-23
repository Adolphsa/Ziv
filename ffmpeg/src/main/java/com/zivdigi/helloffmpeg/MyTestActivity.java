package com.zivdigi.helloffmpeg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Vector;

import static com.zivdigi.helloffmpeg.R.id.mta_fullScreen;

public class MyTestActivity extends FragmentActivity implements View.OnClickListener{

    private static String TAG = "MyTestActivity";

    private static final int PLAY = 0;
    private static final int ERROR_CONNECT = 12;
    private static final int ERROR_READ_FRAME = 13;
    private static final int ERROR_TIME_OUT = 14;
    private static final int FILE_EXIST = 8;
    private static final int AUDIO = 1;
    private static final int SHOW_TOOLS = 2;
    private static final int HIDE_TOOLS = 3;
    private static final int OPENFAIL = 4;
    private static final int STOP = 5;
    private static final int SAVAPHOTO = 6;

    private int mOrientation = 1;
    private byte[] dates;                  //byte数据返回值
    private Buffer buffer = null;           //图像buffer信息
    private boolean audioFlag = false;
    private boolean isPlaying = true;      //是否继续播放
    private boolean isKeepPlay = false;      //是否保持播放
    private boolean surfaceChanged = false; //GLsuface数据切换标识
    private boolean isChange = false;       //是否在切换码流
    private boolean zoomFlag = false;       //是否图像最初标识
    private boolean mIsMoved = false;       //触屏移动标识
    private Bitmap mShowBitmap = null;      //截图Bitmap
    private boolean isProgressShow = true;  //进度条是否显示

    private TestDecoder td;                 //jni数据传递类
    private MyGLRenderer myGLRenderer;      //Opengl ES  Render
    private GLSurfaceView glSurfaceView;    //视频显示控件
    private TextView mTitle;
    private RelativeLayout playToolsBottom;
    private RelativeLayout playToolstop;
    private ProgressBar mCycleProgressBar;  //圆形进度条
    private Button mPlay;                   //播放按钮
    private VideoPlayTask mVideoPlayTask;
    private Context mContext;

    private boolean isScreenshot = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PLAY:
                    mCycleProgressBar.setVisibility(View.INVISIBLE);
                    mPlay.setBackgroundResource(R.mipmap.pause);
                    break;

                case SHOW_TOOLS:
                    Log.i(TAG, "handleMessage: 接收到消息");
                    playToolstop.setVisibility(View.VISIBLE);
                    playToolsBottom.setVisibility(View.VISIBLE);
                    playToolsBottom.getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                    break;

                case HIDE_TOOLS:
                    if (mOrientation != 1){  //注释的地方为如果是竖屏，则播放控制不隐藏
                        playToolstop.setVisibility(View.INVISIBLE);
                        playToolsBottom.setVisibility(View.INVISIBLE);
                    }
                    break;

                case ERROR_CONNECT:    //播放错误
//                    Toast.makeText(MyTestActivity.this,"could not open input stream",Toast.LENGTH_LONG).show();
                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    isProgressShow = true;
//                    showDialog("could not open input stream");
                    break;
                case ERROR_READ_FRAME:    //av_read_frame error

                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    isProgressShow = true;
//                    showDialog("av_read_frame error");
                    break;
                case ERROR_TIME_OUT:    //avformat_open_input timeout

                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    isProgressShow = true;
                    showDialog("avformat_open_input timeout");
                    break;
                case FILE_EXIST:    //文件存在
                    Toast.makeText(MyTestActivity.this,"截图已保存",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_sssp_play);

        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        mContext = MyTestActivity.this;

        //设置status的颜色
        FfmpegStatusBarUtils.setColor(this,getResources().getColor(R.color.statusColor));

        mTitle = (TextView) findViewById(R.id.mat_tv);
        playToolsBottom = (RelativeLayout) findViewById(R.id.mta_rl);
        playToolstop = (RelativeLayout) findViewById(R.id.mta_rl_top);
        mCycleProgressBar = (ProgressBar) findViewById(R.id.mta_recycle_pb); //圆形进度条

        //播放按钮
        mPlay = (Button) findViewById(R.id.mta_play);
        mPlay.setOnClickListener(this);
        Button fullScreen = (Button) findViewById(mta_fullScreen); //全屏
        fullScreen.setOnClickListener(this);
        Button back = (Button) findViewById(R.id.mta_btn_back);  //返回
        back.setOnClickListener(this);
        Button screenshot = (Button) findViewById(R.id.mta_screenshot);  //截图
        screenshot.setOnClickListener(this);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.play_view);

        DisplayMetrics display = new DisplayMetrics();  //窗口适配信息
        getWindowManager().getDefaultDisplay().getMetrics(display);//将当前窗口的一些信息放在DisplayMetrics类中，

        //初始化GLRenderer
        myGLRenderer = new MyGLRenderer(glSurfaceView, display, this);
        //设置glsurfaceView参数
        glSurfaceView.setEGLContextClientVersion(2);
//        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.setRenderer(myGLRenderer);
//        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
//        glSurfaceView.setZOrderOnTop(true);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

//        glSurfaceView.setZOrderMediaOverlay(true);

        //开始播放视频
        isKeepPlay = true;
        td = new TestDecoder();
        td.setCancelInterface(true);
        td.startRequest();

        mVideoPlayTask = new VideoPlayTask();
        mVideoPlayTask.start();

        if (!MyTestActivity.this.isFinishing()){
            td.setErrorCodeInterface(new ErrorCodeInterface() {
                @Override
                public void getErrorCode(int error) {
                    System.out.println("td.error------" + error);
                    if (error == 2){
                        System.out.println("播放异常2");
                         mHandler.sendEmptyMessage(ERROR_CONNECT);
                        try {
                            Thread.sleep(2000);
                            td.startRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else if(error == 3){ //av_read_frame error
                        mHandler.sendEmptyMessage(ERROR_READ_FRAME);
                        try {
                            Thread.sleep(2000);
                            td.startRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else if (error == 4){ //avformat_open_input timeout
                        mHandler.sendEmptyMessage(ERROR_TIME_OUT);
                    }
                }
            });
        }


        glSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceChanged = true;
            }
        });

        //glSurfaceView触碰监听事件
        glSurfaceView.setOnTouchListener(mTouchListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //默认强制竖屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            myGLRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        System.out.println("onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCycleProgressBar.setVisibility(View.VISIBLE);
        isKeepPlay = true;
        td.setCancelInterface(true);
        td.startRequest();
        isProgressShow = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("视频onStop");
        td.setCancelInterface(false);
        td.stopRequest();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("视频onDestroy");
        isKeepPlay = false;
        td.setCancelInterface(false);
        td.stopRequest();
        System.out.println("视频onDestroy2");
        MyGLRenderer.Refreshvar();//重置变量
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            show_tools();
        } else {
            playToolstop.setVisibility(View.VISIBLE);
            playToolsBottom.setVisibility(View.VISIBLE);
            playToolsBottom.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
        }
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.mta_play){ //播放
            Log.i(TAG, "onClick: play按钮被点击了");
            if (isPlaying){
                mPlay.setBackgroundResource(R.mipmap.play);
                isPlaying = false;
                mVideoPlayTask.setSuspend(true);    //线程暂停
            }else {
                mPlay.setBackgroundResource(R.mipmap.pause);
                isPlaying = true;
                mVideoPlayTask.setSuspend(false);   //线程恢复

            }
        }else if (id == R.id.mta_fullScreen){ //全屏
            Log.i(TAG, "onClick: 全屏按钮被点击了");
            click_full();
        }else if (id == R.id.mta_screenshot){ //截图
            isScreenshot = true;
            System.out.println("截图设置为真");
        } else if (id == R.id.mta_btn_back){ //返回
            finish();
        }
    }


    /**
     * 视频播放子线程(重要)
     */
    class VideoPlayTask extends Thread {

        SurfaceHolder holder;
        VideoPlayTask() {
            holder = glSurfaceView.getHolder();
        }

        boolean suspend = false;
        final String control = "";

        public void setSuspend(boolean suspend) {
            if (!suspend) {
                synchronized (control) {
                    control.notifyAll();
                    System.out.println("唤醒");
                }
            }
            this.suspend = suspend;
        }

        @Override
        public void run() {
            int bW = 0;
            int bH = 0;
            int size;
            TestDecoder.FrameBean fb;
            Vector<TestDecoder.FrameBean> videoFrameBuf;

            while (isKeepPlay) {

                synchronized (control){
                    if (suspend){
                        try {
                            control.wait();
                            System.out.println("暂停");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                videoFrameBuf = td.getVector();//获取视频数据
                size = videoFrameBuf.size();
//                Log.i(TAG, "run: Vector的长度是：" + size);
                if (size > 2) {
                    fb = videoFrameBuf.remove(0);
//                    Log.i("realvideo","WITH=="+fb.width+",Height==="+fb.height);
//                    Log.i(TAG, "run: pix的长度" + fb.pix.length);
                    //判断长宽是否发生变化
                    if (bW != fb.width || bH != fb.height || surfaceChanged) {
                        bW = fb.width;
                        bH = fb.height;
                        if (bW == 0 || bH == 0) {
                            return;
                        }
                        isChange = false;
                        mHandler.sendEmptyMessage(PLAY);
                        //获取截图数据
//                        mShowBitmap = Bitmap.createBitmap(bW, bH,
//                                Bitmap.Config.RGB_565);
//                        dates = new byte[bW * bH * 2];
////                        pmc.cvt420pToRGB565(bW, bH, fb.pix, dates);
////                        dates = fb.getPix();
//                        buffer = ByteBuffer.wrap(dates);
//                        mShowBitmap.copyPixelsFromBuffer(buffer);
                        Log.i(TAG, "run: 第一次启动或者长宽发生....变化");
                        myGLRenderer.update(fb.width, fb.height);//设置视频宽高
                    }
                    //是否截图
                    if (isScreenshot){
                        File zivFile = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
                        File imageFile = new File(zivFile ,"images");
                        String target = getDateAndTime() + ".jpeg";
                        File file = new File(imageFile,target);
                        System.out.println("路径为---" + file);
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            dates = new byte[fb.width * fb.height * 2];
                            td.yuv420p_to_yuv420sp(fb.pix,dates,fb.width,fb.height);
                            YuvImage yuvImage = new YuvImage(dates, ImageFormat.NV21,fb.width,fb.height,null);
                            Rect rect = new Rect(0,0,fb.width,fb.height);

                            //转换图片格式为jpeg
                            if (yuvImage.compressToJpeg(rect,70,out)) {
                                System.out.println("截图在file2");
                                out.flush();
                                out.close();

                                // 其次把文件插入到系统图库
                                try {
                                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                                            file.getAbsolutePath(), target, null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                updateImage();
                            }

//                            updateImage();
                            //判断图片是否已保存
                            if (file.exists()){
                                mHandler.sendEmptyMessage(FILE_EXIST);
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        isScreenshot = false;
                        System.out.println("线程中运行了");
                    }
//                    Log.i(TAG, "run: 传递YUV数据");

                    if (isProgressShow){
                        System.out.println("进度条显示");
                        isProgressShow = false;
                        mHandler.sendEmptyMessage(PLAY);
                    }
                    surfaceChanged = false;
                    myGLRenderer.update(fb.pix, fb.width, fb.height);//更新视频数据(刷新GLSufaceView)
                } else {
                    SystemClock.sleep(60);
                }
            }
        }
    }


    private float distance = 1f;
    PointF prev = new PointF();
    private static final int NONE = 0;// 原始
    private static final int DRAG = 1;// 拖动
    private static final int ZOOM = 2;// 放大
    private int mStatus = NONE;

    /**
     * SufaceView触碰监听
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            boolean ret = false;
            if (!isPlaying)
                return ret;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                //单点触碰按下动作
                case MotionEvent.ACTION_DOWN:
                    mStatus = DRAG;         //单指拖动状态
                    prev.set(event.getX(), event.getY());
                    if (MyGLRenderer.ChangeScale <= 0.5f) {
                        zoomFlag = false;
                        MyGLRenderer.Translate = false;
                    } else {
                        zoomFlag = true;
                        MyGLRenderer.Translate = true;
                        MyGLRenderer.Second = true;
                    }
                    ret = true;
                    break;
//                //多点触碰按下动作
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    distance = spacing(event);
//                    // 如果连续两点距离大于10，则判定为多点模式
//                    if (distance > 10f) {
//                        mStatus = ZOOM;
//                        zoomFlag = true;
//                    }
//                    break;
//                //(需判断移动为多点或单点)触碰点移动动作
//                case MotionEvent.ACTION_MOVE:
//                    if (zoomFlag) {
//                        if (mStatus == DRAG) {       //移动操作
//                            mIsMoved = true;
//                            MyGLRenderer.Translate = true;
//                            MyGLRenderer.transx = event.getX() - prev.x;//偏差值
//                            MyGLRenderer.transy = event.getY() - prev.y;//平衡y轴
//                        } else if (mStatus == ZOOM) {//缩放操作
//                            MyGLRenderer.Translate = false;
//                            MyGLRenderer.isFirst = true;
//                            float newDist = spacing(event);
//                            if (newDist > 10f) {
//                                float tScale = newDist / distance;
//                                MyGLRenderer.ChangeScale = tScale;          //将缩放比例交给MyGLRenderer
//                            }
//                        }
//                    }
//                    ret = true;
//                    break;
//                //多点触碰离开动作
//                case MotionEvent.ACTION_POINTER_UP:
//                    MyGLRenderer.Translate = false;
//                    zoomFlag = false;
//                    mStatus = NONE;             //多点触碰取消
//                    break;
                //单点触碰离开动作
                case MotionEvent.ACTION_UP:
                    MyGLRenderer.haveUp = true;
                    if (!mIsMoved && !zoomFlag) {
                        if (playToolstop.getVisibility() == View.VISIBLE) {
                            hide_tools();
                            Log.i(TAG, "onTouch: 隐藏");
                        } else {
                            if (MyGLRenderer.ChangeScale == 0.5f) {
                                show_tools();
                                Log.i(TAG, "onTouch: 显示");
                            }
                        }

                    }
                    mIsMoved = false;
                    break;
            }

            return ret;
        }
    };


    private void show_tools() {
        mHandler.removeMessages(SHOW_TOOLS);
        mHandler.sendEmptyMessageDelayed(HIDE_TOOLS, 5 * 1000);
        mHandler.sendEmptyMessage(SHOW_TOOLS);
    }

    private void hide_tools() {
        mHandler.removeMessages(HIDE_TOOLS);
        mHandler.sendEmptyMessage(HIDE_TOOLS);
    }

    /**
     * 计算两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 全屏控制
     */
    public void click_full() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            myGLRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            myGLRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            myGLRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void showDialog(String msg){
        if (!MyTestActivity.this.isFinishing()){
            new AlertDialog.Builder(MyTestActivity.this)
                    .setTitle("警告")
                    .setMessage(msg)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create().show();
        }

    }

    /***
     * 获取时间和日期
     *
     * @return string
     */
    public static String getDateAndTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sDateFormat.format(new java.util.Date());
        System.out.println(date);
        return date;
    }

    /**
     * 更新图片
     */
    private void updateImage() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        String path = Environment.getExternalStorageDirectory() + "/Ziv/images";
        Uri uri = Uri.fromFile(new File(path));
        intent.setData(uri);
        this.sendBroadcast(intent);
    }

}
