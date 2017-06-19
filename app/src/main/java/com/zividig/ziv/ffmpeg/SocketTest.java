package com.zividig.ziv.ffmpeg;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.zividig.ziv.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketTest extends AppCompatActivity implements Runnable {

    private static final int SHOW_TOOLS = 2;
    private static final int HIDE_TOOLS = 3;

    private ExecutorService mThreadPool;
    private SocketChannel socketChannel;
    private Selector selector;
    Set selectionKeys;
    Iterator iterator;
    SelectionKey selectionKey;
    SocketChannel client;

    boolean flag = true;

    FfmpegTest ffmpegTest;

    GLFrameRenderer mGLFRenderer;

    private int mOrientation = 1;
    private GLSurfaceView mGlView;
    private RelativeLayout mTopTools;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_UP:
                    System.out.println("单子触碰离开");
                    if (mOrientation != 1){
                        if (mTopTools.getVisibility() == View.VISIBLE){
                            hide_tools();
                        }else {
                            show_tools();
                        }
                    }
                break;
            }
            return true;
        }
    };

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_TOOLS:
                    mTopTools.setVisibility(View.VISIBLE);
                    break;
                case HIDE_TOOLS:
                    if (mOrientation != 1) {  //如果是竖屏，则播放控制不隐藏
                        mTopTools.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);

        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        mGlView = (GLSurfaceView)findViewById(R.id.glview);
        mGlView.setEGLContextClientVersion(2);
        DisplayMetrics display = new DisplayMetrics();  //窗口适配信息
        getWindowManager().getDefaultDisplay().getMetrics(display);//将当前窗口的一些信息放在DisplayMetrics类中
        mGLFRenderer = new GLFrameRenderer(mGlView,display);
        mGlView.setRenderer(mGLFRenderer);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //应用运行时，保持屏幕高亮，不锁屏

        ffmpegTest = new FfmpegTest(mGLFRenderer);
        ffmpegTest.nativeInitialize();

        ffmpegTest.setFirst(true);

        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        mThreadPool.execute(SocketTest.this);


        Button fullScreen = (Button) findViewById(R.id.st_fullScreen); //全屏
        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_full();

            }
        });

//        Button btStop = (Button) findViewById(R.id.st_stop);    //停止
//        btStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopSocket();
//            }
//        });

        Button back = (Button) findViewById(R.id.st_btn_back);  //返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopTools = (RelativeLayout) findViewById(R.id.st_rl_top);
        mGlView.setOnTouchListener(mOnTouchListener);

    }

    public void test2() {

//        InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("192.168.1.103", 8080);
        InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("192.168.2.1", 40003);
        try {
            // 打开socket通道
            socketChannel = SocketChannel.open();
            // 设置为非阻塞方式
            socketChannel.configureBlocking(false);
            // 打开选择器
            selector = Selector.open();
            // 注册连接服务端socket动作
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // 连接
            socketChannel.connect(SERVER_ADDRESS);

            ByteBuffer data = ByteBuffer.allocateDirect(2048);    //数据

            while (flag) {

                //选择一组键，其相应的通道已为 I/O 操作准备就绪。
                //此方法执行处于阻塞模式的选择操作。
                if (!selector.isOpen()) {
                    System.out.println("selector is closed");
                    break;
                }

                selector.select();

                if (!selector.isOpen()) {
                    System.out.println("selector is closed");
                    break;
                }
                //返回此选择器的已选择键集。
                selectionKeys = selector.selectedKeys();
                iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    selectionKey = (SelectionKey) iterator.next();
                    client = (SocketChannel) selectionKey.channel();
                    if (selectionKey.isConnectable()) {
                        // 判断此通道上是否正在进行连接操作。
                        // 完成套接字通道的连接过程。
                        if (client.isConnectionPending()) {
                            client.finishConnect();
                            System.out.println("connect finished");
                        }
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {

                        int length = client.read(data);
//                        System.out.println("读到的长度---" + length);
                        ffmpegTest.getDecoderData(data,length);

                        data.clear();
//                        client.register(selector, SelectionKey.OP_READ);
                    }
                }
                selectionKeys.clear();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
                socketChannel.close();
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSocket(){

        flag = false;
        try {

            if (socketChannel != null){
                socketChannel.close();
            }
            if (selector != null){
                selector.close();
            }

            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        flag = true;
        ffmpegTest.setFirst(true);
        ffmpegTest.decodeInit();
        test2();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mOrientation = newConfig.orientation;

        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;

        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("横屏W---" + W + "H---" + H);
            mGlView.getLayoutParams().width = W;
            mGlView.getLayoutParams().height = H;
            show_tools();
        } else {
            System.out.println("竖屏W---" + W + "H---" + H);
            mGlView.getLayoutParams().width = W;
            mGlView.getLayoutParams().height = H;

            mTopTools.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 全屏控制
     */
    public void click_full() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            mGLFRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            mGLFRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            mGLFRenderer.setScreenState(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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



    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSocket();
        ffmpegTest = null;
        mThreadPool = null;
        mGLFRenderer = null;
    }
}
