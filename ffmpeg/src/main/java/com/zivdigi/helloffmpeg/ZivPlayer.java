package com.zivdigi.helloffmpeg;

import java.nio.ByteBuffer;
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

    public ZivPlayer(int colorFormat){
        nativeInit(colorFormat);
    }

    protected void finalize() throws Throwable {
        nativeDestroy();
    }

    private native int nativeInit(int colorFormat);
    private native int nativeDestroy();

    public native boolean startStream(String url);
    public native boolean stopStream(String url);
    public native boolean setRender(String url);

    public native boolean isFrameReady();
    public native int getVideoWidth();
    public native int getVideoHeight();
    public native int getOutputByteSize();
    public native long decodeFrameToDirectBuffer(ByteBuffer buffer);

    static {
        System.loadLibrary("ZivPlayer");
    }
}
