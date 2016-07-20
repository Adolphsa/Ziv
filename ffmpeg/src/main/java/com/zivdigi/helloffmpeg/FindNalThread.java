package com.zivdigi.helloffmpeg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;

/**
 * 图片线程
 * Created by Administrator on 2016-07-19.
 */
public class FindNalThread extends Thread{

    private static final String TAG = "FindNalThread";

    private boolean isClose = false;
    private boolean isPause = false;

    ZivPlayer player;
    ByteBuffer rgb565Buf;
    private Bitmap bmp;
    private SurfaceHolder holder;
    Canvas canvas;
    boolean isRunning = true;

    int width;
    int height;
    Rect srcRect;
    Rect destRect;

    public FindNalThread(SurfaceHolder holder, int width,ZivPlayer player){
        this.holder = holder;
        this.width = width;
        this.player = player;
    }


//    /**
//     * 暂停线程
//     */
//    public synchronized void onThreadPause() {
//        isPause = true;
//    }
//
//    /**
//     * 线程等待,不提供给外部调用
//     */
//    private void onThreadWait() {
//        try {
//            synchronized (this) {
//                this.wait();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 线程继续运行
//     */
//    public synchronized void onThreadResume() {
//        isPause = false;
//        this.notify();
//    }
//
//    /**
//     * 关闭线程
//     */
//    public synchronized void closeThread() {
//        try {
//            notify();
//            setClose(true);
//            interrupt();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isClose() {
//        return isClose;
//    }
//
//    public void setClose(boolean isClose) {
//        this.isClose = isClose;
//    }

    @Override
    public void run() {
        while(isRunning) {
            if (player !=  null && player.isFrameReady()) {
                Log.v(TAG, "width:" + player.getVideoWidth() + " height:" + player.getVideoHeight() + " outsize:" + player.getOutputByteSize());
                int decodedBufLen = player.getOutputByteSize();
                if(rgb565Buf == null){
                    rgb565Buf = ByteBuffer.allocateDirect(decodedBufLen);
                }
                player.decodeFrameToDirectBuffer(rgb565Buf);
                //prepare bitmap for message.
                bmp = Bitmap.createBitmap(player.getVideoWidth(), player.getVideoHeight(), Bitmap.Config.RGB_565);
                bmp.copyPixelsFromBuffer(rgb565Buf);
                rgb565Buf.position(0);

                height = player.getVideoHeight()*width/player.getVideoWidth();
                srcRect = new Rect(0,0,player.getVideoWidth(),player.getVideoHeight());
                destRect = new Rect(0,0,width,height);

                canvas = holder.lockCanvas();
                if (canvas != null){
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                    paint.setDither(true);
                    canvas.drawBitmap(bmp,0,250,paint);
                    Log.i(TAG, "贴图");
                    holder.unlockCanvasAndPost(canvas);
                }
            } else {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setIsRunning(boolean state){
        isRunning = state;
    }
}
