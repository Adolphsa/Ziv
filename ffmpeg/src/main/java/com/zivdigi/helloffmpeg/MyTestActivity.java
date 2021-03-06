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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import static com.zivdigi.helloffmpeg.R.id.mta_fullScreen;

public class MyTestActivity extends FragmentActivity implements View.OnClickListener {

    private static String TAG = "MyTestActivity";

    public static final String FF_REQUEST_VIDEO = "http://api.zivdigi.com/v1/device/rtsp";
    public static final String DEVICE_STATE = "http://api.zivdigi.com/v1/device/info";

    private static final int PLAY = 0;
    private static final int ERROR_CONNECT = 12;
    private static final int ERROR_READ_FRAME = 13;
    private static final int ERROR_TIME_OUT = 14;
    private static final int FILE_EXIST = 8;
    private static final int MAXSCALE = 1;
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
    private LoadingView mCycleProgressBar;  //圆形进度条
    private Button mPlay;                   //播放按钮
    private Button VideoRecord;
    private VideoPlayTask mVideoPlayTask;
    private Context mContext;
    private int videoRecordTemp = 0;

    GestureDetector mGestureDetector = null;

    private Timer mVideoTimer;

    private boolean isScreenshot = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PLAY:
                    mCycleProgressBar.setVisibility(View.INVISIBLE);
                    mPlay.setText("暂停");
                    playToolstop.setVisibility(View.VISIBLE);
                    playToolsBottom.setVisibility(View.VISIBLE);
                    break;

                case SHOW_TOOLS:
                    Log.i(TAG, "handleMessage: 接收到消息");
                    playToolstop.setVisibility(View.VISIBLE);
                    playToolsBottom.setVisibility(View.VISIBLE);
                    playToolsBottom.getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                    break;

                case HIDE_TOOLS:
                    if (mOrientation != 1) {  //注释的地方为如果是竖屏，则播放控制不隐藏
                        playToolstop.setVisibility(View.INVISIBLE);
                        playToolsBottom.setVisibility(View.INVISIBLE);
                    }
                    break;

                case ERROR_CONNECT:    //播放错误
                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    mCycleProgressBar.initPercentCircle(0,0);
                    isProgressShow = true;
                    break;
                case ERROR_READ_FRAME:    //av_read_frame error
                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    mCycleProgressBar.initPercentCircle(0,0);
                    isProgressShow = true;
                    break;
                case ERROR_TIME_OUT:    //avformat_open_input timeout
                    mCycleProgressBar.setVisibility(View.VISIBLE);
                    mCycleProgressBar.initPercentCircle(0,0);
                    isProgressShow = true;
                    showDialog("avformat_open_input timeout");
                    break;
                case FILE_EXIST:    //文件存在
                    Toast.makeText(MyTestActivity.this, "截图已保存", Toast.LENGTH_SHORT).show();
                    break;
                case MAXSCALE:
                    Toast.makeText(MyTestActivity.this, "已经放大到最大级别了", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    private SharedPreferences mSpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_sssp_play);

        mSpf = getSharedPreferences("config", MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        mSpf.edit().putBoolean("is_keeping_get_device_state", true).apply();

        mContext = MyTestActivity.this;

        //设置status的颜色
        FfmpegStatusBarUtils.setColor(this, getResources().getColor(R.color.statusColor));

        mTitle = (TextView) findViewById(R.id.mat_tv);
        playToolsBottom = (RelativeLayout) findViewById(R.id.mta_rl);
        playToolstop = (RelativeLayout) findViewById(R.id.mta_rl_top);
        VideoRecord = (Button) findViewById(R.id.ffmpeg_record);
        mCycleProgressBar = (LoadingView) findViewById(R.id.mta_recycle_pb); //圆形进度条
        mCycleProgressBar.setTargetPercent(99);

        //播放按钮
        mPlay = (Button) findViewById(R.id.mta_play);
        mPlay.setOnClickListener(this);
        Button fullScreen = (Button) findViewById(mta_fullScreen); //全屏
        fullScreen.setOnClickListener(this);
        Button back = (Button) findViewById(R.id.mta_btn_back);  //返回
        back.setOnClickListener(this);
        Button screenshot = (Button) findViewById(R.id.mta_screenshot);  //截图
        screenshot.setOnClickListener(this);
        VideoRecord.setOnClickListener(this);

        glSurfaceView = (GLSurfaceView) findViewById(R.id.play_view);

        DisplayMetrics display = new DisplayMetrics();  //窗口适配信息
        getWindowManager().getDefaultDisplay().getMetrics(display);//将当前窗口的一些信息放在DisplayMetrics类中，

        //初始化GLRenderer
        myGLRenderer = new MyGLRenderer(glSurfaceView, display, this);
        //设置glsurfaceView参数
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(myGLRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        //开始播放视频
        isKeepPlay = true;
        td = new TestDecoder();
        td.setCancelInterface(true);
        td.startRequest();

        mVideoPlayTask = new VideoPlayTask();
        mVideoPlayTask.start();

        if (!MyTestActivity.this.isFinishing()) {
            td.setErrorCodeInterface(new ErrorCodeInterface() {
                @Override
                public void getErrorCode(int error) {
                    System.out.println("td.error------" + error);
                    if (error == 2) {
                        System.out.println("播放异常2");
                        mHandler.sendEmptyMessage(ERROR_CONNECT);
                        try {
                            Thread.sleep(2000);
                            td.startRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (error == 3) { //av_read_frame error
                        mHandler.sendEmptyMessage(ERROR_READ_FRAME);
                        try {
                            Thread.sleep(2000);
                            td.startRequest();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (error == 4) { //avformat_open_input timeout
                        mHandler.sendEmptyMessage(ERROR_TIME_OUT);
                    }
                }
            });
        }


        //发送继续推流命令
        startTimer();

        glSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {}

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                surfaceChanged = true;
            }
        });

        //glSurfaceView触碰监听事件
        glSurfaceView.setOnTouchListener(mTouchListener);
        mGestureDetector = new GestureDetector(new simpleGestureListener());
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCycleProgressBar.setVisibility(View.VISIBLE);
        isKeepPlay = true;
        td.setCancelInterface(true);
        td.startRequest();
        isProgressShow = true;
        startTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("视频onStop");
        td.setCancelInterface(false);
        td.stopRequest();
        stopTimer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
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
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.mta_play) { //播放
            Log.i(TAG, "onClick: play按钮被点击了");
            if (isPlaying) {
                mPlay.setText("播放");
                isPlaying = false;
                mVideoPlayTask.setSuspend(true);    //线程暂停
            } else {
                mPlay.setText("暂停");
                isPlaying = true;
                mVideoPlayTask.setSuspend(false);   //线程恢复

            }
        } else if (id == R.id.mta_fullScreen) { //全屏
            Log.i(TAG, "onClick: 全屏按钮被点击了");
            click_full();
        } else if (id == R.id.mta_screenshot) { //截图
            isScreenshot = true;
            System.out.println("截图设置为真");
        } else if (id == R.id.mta_btn_back) { //返回
            finish();
        }else if (id == R.id.ffmpeg_record){    //视频录制

//            GLRecorder recorder = glSurfaceView.getRecorder();
//
//            // 设置视频的最大尺寸
//            recorder.setMaxFrameSize(Recorder.LevelMaxFrameSize.LEVEL_1280_720);
//            // 设置视频的质量（高、中、低）
//            recorder.setVideoQuality(Recorder.LevelVideoQuality.LEVEL_HIGH);
//            // 设置视频的最短时长
//            recorder.setMinDuration(5 * 1000);
//
//            File zivFile = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
//            File videoFile = new File(zivFile, "video");
//            String aa = videoFile.getAbsolutePath();
//            System.out.println("录制文件地址" + aa);
//            // 设置视频的输出路径
//            recorder.setCacheFolder(aa);
//            // 设置是否强制使用软件编码器对视频进行编码（兼容性更高）
//            recorder.setForceSoftwareEncoding(true, true);
//
//            if (recorder.isAvailable()){
//                if (videoRecordTemp == 0){
//                    //开启录制
//                    System.out.println("开启录制");
//                    recorder.startRecorder();
//                    VideoRecord.setText("停止录制");
//                    videoRecordTemp = 1;
//                }else if (videoRecordTemp == 1){
//                    videoRecordTemp = 0;
//                    recorder.stopRecorder();
//                    System.out.println("停止录制");
//                    VideoRecord.setText("开始录制");
//
//                    recorder.showShare();
//                }
//
//            }
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

                synchronized (control) {
                    if (suspend) {
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
                    if (isScreenshot) {
                        File zivFile = new File(Environment.getExternalStorageDirectory(), "Ziv"); //创建Ziv文件夹
                        File imageFile = new File(zivFile, "images");
                        String target = getDateAndTime() + ".jpeg";
                        File file = new File(imageFile, target);
                        System.out.println("路径为---" + file);
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            dates = new byte[fb.width * fb.height * 2];
                            td.yuv420p_to_yuv420sp(fb.pix, dates, fb.width, fb.height);
                            YuvImage yuvImage = new YuvImage(dates, ImageFormat.NV21, fb.width, fb.height, null);
                            Rect rect = new Rect(0, 0, fb.width, fb.height);

                            //转换图片格式为jpeg
                            if (yuvImage.compressToJpeg(rect, 70, out)) {
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
                            if (file.exists()) {
                                mHandler.sendEmptyMessage(FILE_EXIST);
                            }

                        } catch (Exception e) {
                            System.out.println(e);
                        }
                        isScreenshot = false;
                        System.out.println("线程中运行了");
                    }
//                    Log.i(TAG, "run: 传递YUV数据");

                    if (isProgressShow) {
                        System.out.println("进度条显示");
                        isProgressShow = false;
                        mHandler.sendEmptyMessage(PLAY);
                    }
                    surfaceChanged = false;
                    myGLRenderer.update(fb.pix, fb.width, fb.height);//更新视频数据(刷新GLSufaceView)
//                    System.out.println("视频宽---" + fb.width + "视频高---" + fb.height);
                } else {
                    SystemClock.sleep(10);
                }
            }
        }
    }


    private float distance = 1f;
    private float detDistance;

    private float tScale;
    private float svaeScale = 0;
    private float detScale;
    private float tempDist;
    private boolean isFirst;
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
            mGestureDetector.onTouchEvent(event);
            boolean ret = false;
//            if (!isPlaying)
//                return ret;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                //单点触碰按下动作
                case MotionEvent.ACTION_DOWN:
                    System.out.println("单指按下");
                    mStatus = DRAG;         //单指拖动状态
                    prev.set(event.getX(), event.getY());
                    if (MyGLRenderer.ChangeScale <= 1.0f) {
                        zoomFlag = false;
                        MyGLRenderer.Translate = false;
                    } else {
                        zoomFlag = true;
                        MyGLRenderer.Translate = true;
                        MyGLRenderer.Second = true;
                        System.out.println("大于=1.0f");
                    }
                    ret = true;
                    break;
                //多点触碰按下动作
                case MotionEvent.ACTION_POINTER_DOWN:
                        distance = spacing(event);
                    // 如果连续两点距离大于10，则判定为多点模式
                    if (distance > 10f) {
                        System.out.println("多点触碰，distance---" + distance);
                        isFirst = true;
                        mStatus = ZOOM;
                        zoomFlag = true;
                    }
                    break;
                //(需判断移动为多点或单点)触碰点移动动作
                case MotionEvent.ACTION_MOVE:
                    if (zoomFlag) {
                        if (mStatus == DRAG) {       //移动操作
                            mIsMoved = true;
                            MyGLRenderer.Translate = true;
                            MyGLRenderer.transx = event.getX() - prev.x;//偏差值
                            MyGLRenderer.transy = event.getY() - prev.y;//平衡y轴
                        } else if (mStatus == ZOOM) {//缩放操作
                            MyGLRenderer.Translate = false;
                            MyGLRenderer.isFirst = true;

                            float newDist = spacing(event);
                            System.out.println("newDist---" + newDist);
                            if (newDist > 10f) {

                                detDistance = newDist - distance;
                                detScale = detDistance / distance;
                                if (detScale > svaeScale){
                                    System.out.println("svaeScale不大于0");
                                    if (tScale < 8.0){
                                        tScale = detScale*2 + 1;
                                    }else {
                                        tScale = 7.0f;
                                        mHandler.sendEmptyMessage(MAXSCALE);
                                    }

                                }else {
                                    if (tScale < 8.0){
                                        tScale = detScale*2 + svaeScale;
                                        System.out.println("svaeScale大于0");
                                    }else {
                                        tScale = 7.0f;
                                        mHandler.sendEmptyMessage(MAXSCALE);
                                    }
                                }
                                MyGLRenderer.ChangeScale = tScale;
//                                if (isFirst) {
//                                    svaeScale = svaeScale0;
//                                    isFirst = false;
//                                    MyGLRenderer.ChangeScale = svaeScale;          //将缩放比例交给MyGLRenderer
//                                } else {
//
//                                    if (newDist - tempDist < 0) {
//                                        System.out.println("缩小");
//                                        svaeScale0 = tScale;
//                                    } else {
//
//                                        svaeScale0 = svaeScale + tScale;
//                                        System.out.println("放大---" + svaeScale0);
//                                    }
//
//                                    tempDist = newDist;
//                                    MyGLRenderer.ChangeScale = svaeScale0;          //将缩放比例交给MyGLRenderer
//                                }
                            }
                        }
                    }
                    ret = true;
                    break;
                //多点触碰离开动作
                case MotionEvent.ACTION_POINTER_UP:
                    MyGLRenderer.Translate = false;
                    zoomFlag = false;
                    mStatus = NONE;             //多点触碰取消
                    svaeScale = tScale;
                    System.out.println("多点触碰离开svaeScale---" + svaeScale);
                    break;
                //单点触碰离开动作
                case MotionEvent.ACTION_UP:
                    MyGLRenderer.haveUp = true;
                    if (!mIsMoved && !zoomFlag) {
                        if (playToolstop.getVisibility() == View.VISIBLE) {
                            hide_tools();
                            Log.i(TAG, "onTouch: 隐藏");
                        } else {
                            Log.i(TAG, "onTouch: 显示");
                            show_tools();
//                            if (MyGLRenderer.ChangeScale == 0.5f) {
//
//                                Log.i(TAG, "onTouch: 显示");
//                            }
                        }

                    }
                    mIsMoved = false;
                    break;
            }
            return ret;
        }
    };


    private int statusGesture = 0;

    private class simpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            System.out.println("双击屏幕");
            if (tScale < 5.0 && statusGesture == 0) {
                System.out.println("放大");
                tScale += 1.0f;
            }
            if (tScale >= 5.0f) {
                statusGesture = 1;
            }
            if (statusGesture == 1) {
                tScale -= 1.0f;
            }
            if (tScale <= 1.0f) {
                statusGesture = 0;
            }
            MyGLRenderer.ChangeScale = tScale;
            return true;
        }
    }

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

    public void showDialog(String msg) {
        if (!MyTestActivity.this.isFinishing()) {
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

    //定时器开始轮询设备状态
    public void startTimer() {
        System.out.println("开始轮询");
        mVideoTimer = new Timer();

        mVideoTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startVideo();
            }
        }, 20 * 1000, 20 * 1000);

        mVideoTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getDeviceStateInFfmpeg();
            }
        },2000,2000);

    }

    public void stopTimer() {
        System.out.println("停止轮询");
        if (mVideoTimer != null) {
            mVideoTimer.cancel();
        }
    }

    /**
     * 开启实时视频
     *
     * @param
     */
    public void startVideo() {
        String token = mSpf.getString("token", "");
        String deviceId = mSpf.getString("devid", "");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid", deviceId);
            json.put("token", token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算signature
        String timestamp = ArrayUtil.getTimestamp();
        String noncestr = ArrayUtil.getRandomString(10);
        String signature = ArrayUtil.getSinnature(timestamp,
                noncestr,
                ArrayUtil.FF_APP_KEY,
                deviceId,
                token);
        //发起请求
        RequestParams params = ArrayUtil.setParams(FF_REQUEST_VIDEO, timestamp, noncestr, signature);
        params.setBodyContent(json.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("继续请求推流结果" + result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("访问错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getDeviceStateInFfmpeg() {
        String token = mSpf.getString("token", "");
        final String deviceId = mSpf.getString("devid", "");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid", deviceId);
            json.put("token", token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算signature
        String timestamp = ArrayUtil.getTimestamp();
        String noncestr = ArrayUtil.getRandomString(10);
        String signature = ArrayUtil.getSinnature(timestamp,
                noncestr,
                ArrayUtil.FF_APP_KEY,
                deviceId,
                token);
        //发起请求
        RequestParams params = ArrayUtil.setParams(DEVICE_STATE, timestamp, noncestr, signature);
        params.setBodyContent(json.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (200 == status) {
                            JSONObject info = jsonObject.getJSONObject("info");
                            String deviceStatus = info.getString("workmode");
                            System.out.println("播放视频时候的状态---" + deviceStatus);
                            if (!deviceStatus.equals("NORMAL") && !MyTestActivity.this.isFinishing()) {
                                td.setCancelInterface(false);
                                td.stopRequest();
                                stopTimer();
                                FfmpegDialogUtils.showPrompt(MyTestActivity.this, "提示", "设备唤醒时间已到，正在进入休眠,视频停止！", "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

}
