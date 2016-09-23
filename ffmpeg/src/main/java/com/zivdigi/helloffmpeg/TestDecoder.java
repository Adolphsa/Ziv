package com.zivdigi.helloffmpeg;

import android.util.Log;

import java.util.Vector;

/**
 * Created by walker on 16-3-30.
 */
public class TestDecoder{

    private static String TAG = "testDecoder";
    private static String vedioUrl;
    ZivPlayer player;

    private volatile boolean stopRequested;

    public Vector<FrameBean> getVector() {
        return mVector;
    }

    public void setVector(Vector<FrameBean> vector) {
        mVector = vector;
    }

    private  FrameBean mFrameBean;
    private Vector<FrameBean> mVector;

    public TestDecoder(){
        player = new ZivPlayer(ZivPlayer.COLOR_FORMAT_YUV420);
        player.setPlayerUser(this);

        mFrameBean = new FrameBean();
        mVector = new Vector<FrameBean>();  //创建一个存放数据帧的容器
    }

    //Upgrade the BMP frame to UI.
    public  int upgradeFrameInUI(byte[] buf, int videoWidth, int videoHeight, int bufSize)
    {
        mFrameBean.width = videoWidth;
        mFrameBean.height = videoHeight;
        mFrameBean.pix = buf;

//        System.out.println("buf的长度：" + mFrameBean.getPix().length);
        mVector.add(mFrameBean);
        setVector(mVector);
        return 0;
    }

    /**
     * 方法待定
     * @return
     */
    public Vector<FrameBean> getDataQueue(){
        Log.i(TAG, "Vector的长度：" + mVector.size());
        return mVector;
    }

    public static void setUrl(String url) {
        vedioUrl = url;
    }

    public void startRequest(){
        final String rtspUrlPi02 = "rtsp://192.168.199.30:554/stream1";
        if(isPlaying()){
            return;
        }

        if(player != null){
            player.startStream(vedioUrl);
        }
    }

    public void stopRequest() {
        if(player != null && isPlaying()) {
            player.stopStream("test");
        }
    }

    public boolean isPlaying(){
        if(player != null) {
            return player.isPlayerPlaying();
        }

        return false;
    }

    class FrameBean {
        int width;
        int height;
        byte[] pix;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public byte[] getPix() {
            return pix;
        }

        public void setPix(byte[] pix) {
            this.pix = pix;
        }
    }
}
