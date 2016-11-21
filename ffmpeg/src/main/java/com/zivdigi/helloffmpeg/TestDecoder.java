package com.zivdigi.helloffmpeg;

import android.util.Log;

import java.util.Vector;

/**
 * Created by walker on 16-3-30.
 */
public class TestDecoder{

    private static String TAG = "testDecoder";
    private static String videoUrl;
    ZivPlayer player;
    int errorCode;

    private volatile boolean stopRequested;

    public Vector<FrameBean> getVector() {
        return mVector;
    }

    public void setVector(Vector<FrameBean> vector) {
        mVector = vector;
    }

    private  FrameBean mFrameBean;
    private Vector<FrameBean> mVector;

    ErrorCodeInterface mErrorCodeInterface;

    public void setErrorCodeInterface(ErrorCodeInterface errorCodeInterface){
        mErrorCodeInterface = errorCodeInterface;
    }

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
        videoUrl = url;
    }

    public void startRequest(){
//        final String rtspUrlPi02 = "rtsp://192.168.199.30:554/stream1";
        if(isPlaying()){
            return;
        }

        if(player != null){
            boolean isStart = player.startStream(videoUrl);
            System.out.println("isStart = " + isStart);
        }


    }

    public void stopRequest() {
        if(player != null && isPlaying()) {
            System.out.println("调用stopRequest");
            player.stopStream(videoUrl);
        }
    }

    public boolean isPlaying(){
        if(player != null) {
            return player.isPlayerPlaying();
        }

        return false;
    }

    public void getErrorCode(int code){
        errorCode = code;
        mErrorCodeInterface.getErrorCode(code);
        System.out.println("errorCode = " + errorCode);
    }

    public  void yuv420p_to_yuv420sp(byte[] yuv420p,byte[] yuv420sp,int width,int height){
        player.yuv420p_to_yuv420sp(yuv420p,yuv420sp,width,height);
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
