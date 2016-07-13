package com.zividig.ziv.main;

import android.app.Application;

import org.xutils.x;

import cn.sharesdk.framework.ShareSDK;

/**
 * Application
 * Created by Administrator on 2016-06-29.
 */
public class ZivApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this); //初始化xutils
        ShareSDK.initSDK(this); //初始化ShareSDK
    }
}