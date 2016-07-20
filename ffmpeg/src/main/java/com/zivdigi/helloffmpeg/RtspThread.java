package com.zivdigi.helloffmpeg;

import android.util.Log;

/**
 * RTSP线程
 * Created by Administrator on 2016-07-19.
 */
public class RtspThread extends Thread {
    private static final String TAG = "RtspThread";
    public static final String RTSP_NEW = "rtsp://192.168.199.30/stream0";
    ZivPlayer player;

    public RtspThread(ZivPlayer player){

        this.player = player;
    }
    @Override
    public void run() {
       player.startStream(RTSP_NEW);
        Log.i(TAG, "执行RtspThread");
    }
}

