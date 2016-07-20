package com.zivdigi.helloffmpeg;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by walker on 16-3-30.
 */
public class TestDecoder implements Runnable{
    private String inputFileName;
    private String outputFileName;
    private int readCount;
    private int readTotal;
    private int decodeBufferSize;
    public byte[] decodeBuffer;
    private int decodeBufferAvailable;

    ZivPlayer player;
    private Handler mHandler;
    private int nalBegin;
    private Boolean findNalHead;
    private Lock m_decodeBufLock;
    private Object recvDisplayLock;
    ByteBuffer rgb565Buf;
    private Bitmap bmp;

    private boolean isPlayer = true;

    public TestDecoder(Handler handler){
        inputFileName = "";
        outputFileName = "";
        readCount = 0;
        readTotal = 0;
        decodeBufferSize = 1024 * 4096;
        decodeBuffer = new byte[decodeBufferSize];
        decodeBufferAvailable = decodeBufferSize;

        m_decodeBufLock = new ReentrantLock();
        recvDisplayLock = new Object();
        rgb565Buf = null;
        bmp = null;
        mHandler = handler;
    }


    private int findNalInDecodeBuffer3() throws FileNotFoundException {
        rgb565Buf = null;
        bmp = null;

        while(isPlayer) {
            if (player !=  null && player.isFrameReady()) {
                Log.v("TestDecoder", "width:" + player.getVideoWidth() + " height:" + player.getVideoHeight() + " outsize:" + player.getOutputByteSize());
                int decodedBufLen = player.getOutputByteSize();
                if(rgb565Buf == null){
                    rgb565Buf = ByteBuffer.allocateDirect(decodedBufLen);
                }

                player.decodeFrameToDirectBuffer(rgb565Buf);

                //prepare bitmap for message.
                bmp = Bitmap.createBitmap(player.getVideoWidth(), player.getVideoHeight(), Bitmap.Config.RGB_565);
                bmp.copyPixelsFromBuffer(rgb565Buf);
                rgb565Buf.position(0);

                //Send Update Picture Message to UI.
                mHandler.obtainMessage(MainActivity.MSG_SUCCESS, bmp).sendToTarget();

            }
            else
            {
                //Log.v("TestDecoder", "isFrameReady == false.");

                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
    private int testRTSPStream2()
    {
        final String rtspUrlLocal = "rtsp://192.168.199.30/stream0";
        final String rtspUrlPi02 = "rtsp://192.168.199.103:8554/stream0";
        final String rtspUrlCentOs = "rtsp://192.168.199.21:8554/stream0";
        final String rtspUrlDevServer = "rtsp://120.24.174.213:8554/live_1234567890123456789.sdp";

        player = new ZivPlayer(ZivPlayer.COLOR_FORMAT_RGB565LE);

        //Start Display Thread.
        Thread display = new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                findNalInDecodeBuffer3();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            }
        });

        display.start();

        player.startStream(rtspUrlLocal);
        return 0;
    }

    @Override
    public void run() {

        testRTSPStream2();

    }

    public void setStop(){
        isPlayer = false;
    }
}
