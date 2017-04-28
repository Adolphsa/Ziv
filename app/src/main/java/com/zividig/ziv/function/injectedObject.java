package com.zividig.ziv.function;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

/**
 * Created by adolph
 * on 2017-03-27.
 */

public class injectedObject {

    Handler mHandler;

    public injectedObject(Handler handler){
        mHandler = handler;
    }

    @JavascriptInterface
    public void openURL(String url){
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.obj = url;
        mHandler.sendMessage(message);
    }

}
