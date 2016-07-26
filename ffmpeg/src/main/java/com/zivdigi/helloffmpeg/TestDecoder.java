package com.zivdigi.helloffmpeg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;

/**
 * Created by walker on 16-3-30.
 */
public class TestDecoder{

    ByteBuffer rgb565Buf;
    ZivPlayer player;
    Canvas canvas;

    private SurfaceHolder holder;
    private Bitmap bmp;

    public TestDecoder( SurfaceHolder holder){
        rgb565Buf = null;
        bmp = null;

//        mHandler = handler;
        this.holder = holder;

        player = new ZivPlayer(ZivPlayer.COLOR_FORMAT_RGB565LE);
        player.setPlayerUser(this);
    }

    //Upgrade the BMP frame to UI.
    public  int upgradeFrameInUI(ByteBuffer buf, int videoWidth, int videoHeight, int bufSize)
    {

        bmp = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.RGB_565);
        bmp.copyPixelsFromBuffer(buf);
        buf.position(0);
        System.out.println("哈哈哈哈,宽度：" + videoWidth + "高度:" + videoHeight );

        if (holder.getSurface().isValid()){
            System.out.println("判断是否已经创建好");
            canvas = holder.lockCanvas();
            if (canvas != null){
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);
                paint.setDither(true);
                canvas.drawBitmap(bmp,0,0,paint);
                holder.unlockCanvasAndPost(canvas);
            }
        }

        return 0;
    }

    public void startRequest(){
        final String rtspUrlLocal = "rtsp://192.168.199.30/stream1";
        final String rtspUrlPi02 = "rtsp://192.168.199.103:8554/stream0";
        final String rtspUrlCentOs = "rtsp://192.168.199.21:8554/stream0";
        final String rtspUrlDevServer = "rtsp://120.24.174.213:8554/live_1234567890123456789.sdp";
        final String rtspUrlDevServer2 = "rtsp://120.25.80.80:8554/live_1234567890123456789.sdp";

        if(isPlaying()){
            return;
        }

        if(player != null){
            player.startStream(rtspUrlLocal);
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

}
