package com.zivdigi.helloffmpeg;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.nio.Buffer;
import java.util.Vector;

import static com.zivdigi.helloffmpeg.R.id.mta_fullScreen;

public class MyTestActivity extends FragmentActivity implements View.OnClickListener{

    private static String TAG = "MyTestActivity";

    private static final int PLAY = 0;
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
    private boolean isPlaying = false;      //是否继续播放
    private boolean isKeepPlay = true;      //是否保持播放
    private boolean surfaceChanged = false; //GLsuface数据切换标识
    private boolean isChange = false;       //是否在切换码流
    private boolean zoomFlag = false;       //是否图像最初标识
    private boolean mIsMoved = false;       //触屏移动标识
    private Bitmap mShowBitmap = null;      //截图Bitmap

    private TestDecoder td;                 //jni数据传递类
    private MyGLRenderer myGLRenderer;      //Opengl ES  Render
    private GLSurfaceView glSurfaceView;    //视频显示控件
    private TextView mTitle;
    private RelativeLayout playToolsBottom;
    private RelativeLayout playToolstop;
    private ProgressBar mCycleProgressBar;  //圆形进度条
    private Button mPlay;                   //播放按钮


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PLAY: {
                    mCycleProgressBar.setVisibility(View.GONE);
                    mPlay.setBackgroundResource(R.mipmap.pause);
                    break;
                }
                case SHOW_TOOLS:
                    Log.i(TAG, "handleMessage: 接收到消息");
                    playToolstop.setVisibility(View.VISIBLE);
                    playToolsBottom.setVisibility(View.VISIBLE);
                    playToolsBottom.getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
                    break;
                case HIDE_TOOLS: {
                    if (mOrientation != 1){  //注释的地方为如果是竖屏，则播放控制不隐藏
                        playToolstop.setVisibility(View.INVISIBLE);
                        playToolsBottom.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_sssp_play);

        mTitle = (TextView) findViewById(R.id.mat_tv);
        playToolsBottom = (RelativeLayout) findViewById(R.id.mta_rl);
        playToolstop = (RelativeLayout) findViewById(R.id.mta_rl_top);
        mCycleProgressBar = (ProgressBar) findViewById(R.id.mta_recycle_pb); //圆形进度条

        //播放按钮
        mPlay = (Button) findViewById(R.id.mta_play);
        mPlay.setOnClickListener(this);
        Button fullScreen = (Button) findViewById(mta_fullScreen); //全屏
        fullScreen.setOnClickListener(this);
        Button back = (Button) findViewById(R.id.mta_btn_back);
        back.setOnClickListener(this);

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
        isPlaying = true;
        td = new TestDecoder();
        td.startRequest();
        new VideoPlayTask().start();

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
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        isPlaying = false;
        MyGLRenderer.Refreshvar();//重置变量
        super.onDestroy();
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
                td.stopRequest();
                isPlaying = false;
            }else {
                mPlay.setBackgroundResource(R.mipmap.pause);
                td.startRequest();
                isPlaying = true;
                new VideoPlayTask().start();
            }
        }else if (id == R.id.mta_fullScreen){ //全屏
            Log.i(TAG, "onClick: 全屏按钮被点击了");
            click_full();
        }else if (id == R.id.mta_btn_back){ //返回
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

        @Override
        public void run() {
            int bW = 0;
            int bH = 0;
            int size;
            TestDecoder.FrameBean fb;
            Vector<TestDecoder.FrameBean> videoFrameBuf;
            while (isPlaying) {
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
//                    Log.i(TAG, "run: 传递YUV数据");
                    surfaceChanged = false;
                    isPlaying = true;
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

                        Log.i(TAG, "onTouch: 点击了");
//                        mTitle.setVisibility(View.VISIBLE);

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
}
