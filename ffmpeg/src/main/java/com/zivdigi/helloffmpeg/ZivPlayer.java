package com.zivdigi.helloffmpeg;

import android.util.Log;

/**
 * Created by walker on 16-7-11.
 */
public class ZivPlayer  {

    public static final int COLOR_FORMAT_YUV420 = 0;
    public static final int COLOR_FORMAT_RGB565LE = 1;
    public static final int COLOR_FORMAT_BGR32 = 2;

    public static final int MSG_SUCCESS = 0;//获取图片成功的标识
    public static final int MSG_FAILURE = 1;//获取图片失败的标识

    private int cdata;
    private TestDecoder client;

    public ZivPlayer(int colorFormat){
        nativeInit(colorFormat);
    }

    protected void finalize() throws Throwable {
        nativeDestroy();
    }

    public int setPlayerUser(TestDecoder client)
    {
        this.client = client;
        return 0;
    }

    public int getDecodedFrameCb(byte[] buffer, int videoWidth, int videoHeight, int bufSize) {
        //Log.v("ZivPlayer", "getDecodedFrameCb was called");

        if(client != null)
        {
            client.upgradeFrameInUI(buffer, videoWidth, videoHeight, bufSize);
        }

        return 0;
    }


    public int getErrorCodeCb(int errorCode){
        /*
            ZIV_ERR_CODECLOAD = 1,
            ZIV_ERR_CONNECT = 2,
            ZIV_ERR_READFRAME = 3,
        */
        client.getErrorCode(errorCode);
        Log.v("ZivPlayer", "getErrorCodeCb was called");

        return 0;
    }

    private native int nativeInit(int colorFormat);
    private native int nativeDestroy();
    public native boolean startStream(String url);
    public native boolean stopStream(String url);
    public native boolean setThreadExit(int code1);
    public native boolean setRender(String url);
    public native boolean isPlayerPlaying();//

    public native boolean isFrameReady();
    public native int getVideoWidth();
    public native int getVideoHeight();
    public native int getOutputByteSize();

    public native void yuv420p_to_yuv420sp(byte[] yuv420p,byte[] yuv420sp,int width,int height);

    static {
        System.loadLibrary("ZivPlayer");
    }
}
