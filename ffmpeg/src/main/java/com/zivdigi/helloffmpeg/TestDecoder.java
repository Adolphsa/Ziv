package com.zivdigi.helloffmpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.nio.ByteBuffer;

/**
 * 解码
 * Created by walker on 16-3-30.
 */
public class TestDecoder {

    ByteBuffer rgb565Buf;
    ZivPlayer player;
    Canvas canvas;
    int width;
    int height;

    private static String vedioUrl;

    private SurfaceHolder holder;
    private Bitmap bmp;
    private Bitmap scaleBmp;

    private Handler mHandler;
    private Context context;
    public TestDecoder(SurfaceHolder holder, int width, Context context,Handler handler) {
        rgb565Buf = null;
        bmp = null;

        mHandler = handler;
        this.context = context;
        this.holder = holder;
        this.width = width;

        player = new ZivPlayer(ZivPlayer.COLOR_FORMAT_RGB565LE);
        player.setPlayerUser(this);
    }

    public void getErrorCode(int errorCode){
//        Looper.prepare();
//        if (errorCode ==1){
//            showToast(context,"加载库文件出错");
//        }else if (errorCode == 2){
//            showToast(context,"无法连接设备");
//        }else if (errorCode == 3 ){
//            showToast(context,"连接成功   中途断开");
//        }
//        Looper.loop();

        Message msg = mHandler.obtainMessage();
        msg.what = errorCode;
        mHandler.sendMessage(msg);
    }

    //Upgrade the BMP frame to UI.
    public int upgradeFrameInUI(ByteBuffer buf, int videoWidth, int videoHeight, int bufSize) {

                System.out.println("原始宽度：" + videoWidth + "原始高度:" + videoHeight);
                height = videoHeight * width / videoWidth;
                System.out.println("缩放宽度：" + width + "缩放高度:" + height);
                bmp = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.RGB_565);
                bmp.copyPixelsFromBuffer(buf);
                buf.position(0);
                scaleBmp = Bitmap.createScaledBitmap(bmp, width, height, true); //缩放图片


                if (holder.getSurface().isValid()) {
                    System.out.println("判断是否已经创建好");
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    Paint paint = new Paint();
                    paint.setAntiAlias(true);
                    paint.setFilterBitmap(true);
                    paint.setDither(true);
                    canvas.drawBitmap(scaleBmp, 0, 0, paint);
                    holder.unlockCanvasAndPost(canvas);
                }
            }

        return 0;
    }

    public static void setUrl(String url) {
        vedioUrl = url;
    }

    public void startRequest() {

        if (isPlaying()) {
            return;
        }
        if(player != null){
            player.startStream(vedioUrl);
        }
    }

    public void stopRequest() {
        if (player != null && isPlaying()) {
            player.stopStream(vedioUrl);
        }
    }

    public boolean isPlaying() {
        if (player != null) {
            return player.isPlayerPlaying();
        }
        return false;
    }

    public void showToast(Context context,String str){
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
