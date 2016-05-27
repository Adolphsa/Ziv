package com.zividig.ziv.customView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义的ViewPager
 * Created by linhonghong on 2015/8/10.
 */
public class CustomViewPager extends ViewPager {
    private boolean mNoFocus = false; //if true, keep View don't move
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomViewPager(Context context){
        this(context,null);
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mNoFocus) {
            return false;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void setNoFocus(boolean b){
        mNoFocus = b;
    }
}