package com.zivdigi.helloffmpeg;

import android.content.Context;
import android.util.AttributeSet;

import cn.sharerec.recorder.impl.SrecGLSurfaceView;

/**
 * Created by adolph
 * on 2017-05-06.
 */

public class MyGLSurfaceView extends SrecGLSurfaceView {

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 返回ShareREC的Appkey
    protected String getShareRecAppkey() {
        return "1d9e8427a4d30";
    }

    // 返回ShareREC的AppSecret
    protected String getShareRecAppSecret() {
        return "84d8ec628266535c0f6d9f16088affac";
    }

    @Override
    protected void onSizeChanged(int i, int i1, int i2, int i3) {
        super.onSizeChanged(i, i1, i2, i3);
    }
}
