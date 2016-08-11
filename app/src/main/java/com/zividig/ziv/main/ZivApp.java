package com.zividig.ziv.main;

import android.app.Application;

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
        x.Ext.init(this); //初始化xutils

        if (instance == null) {
            instance = this;
        }

        //开启获取网络类型的服务
//        startService(new Intent(this, NetWorkTypeService.class));

        //清空二维码信息
//        SharedPreferences spf = getSharedPreferences("config", MODE_PRIVATE);
//        spf.edit().putString("two_code","").apply();
    }

    public static ZivApp getInstance() {
        return instance;
    }
}
