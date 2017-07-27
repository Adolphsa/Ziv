package com.zivdigi.helloffmpeg.twog;


import java.nio.ByteBuffer;


/**
 * Created by adolph
 * on 2017-06-07.
 */

public class FfmpegTest {

    GLFrameRenderer m_render;
    boolean isFirst;
    public native  void  nativeInitialize();
    public native void decodeInit();
    public native void nativeDestroy();

    public native void getDecoderData(ByteBuffer h264data, int length);

    public FfmpegTest(GLFrameRenderer render){
        m_render = render;
    }

    public boolean isFirst() {return isFirst;}
    public void setFirst(boolean first) {isFirst = first;}

    public void onNativeCallback(int width,int height,byte[] ydata, byte[] udata, byte[] vdata)
    {
//        System.out.println("width:"+width+"height:"+height);

        if (isFirst){
            m_render.update(width,height);
            setFirst(false);
            System.out.println("只运行一次");
        }
        m_render.update(ydata,udata,vdata);

    }

    static {System.loadLibrary("test");}
}
