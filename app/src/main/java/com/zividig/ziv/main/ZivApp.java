package com.zividig.ziv.main;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.xutils.x;


/**
 * Application
 * Created by Administrator on 2016-06-29.
 */
public class ZivApp extends Application{

    private static ZivApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this); //初始化百度地图
        x.Ext.init(this);

        System.out.println("application");
        if (instance == null) {
            instance = this;
        }

    }

    public static ZivApp getInstance() {
        return instance;
    }
}
