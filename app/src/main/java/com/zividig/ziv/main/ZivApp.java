package com.zividig.ziv.main;

import android.app.Application;
import android.content.Intent;

import com.zividig.ziv.service.NetWorkTypeService;

import org.xutils.x;

/**
 * Application
 * Created by Administrator on 2016-06-29.
 */
public class ZivApp extends Application{



    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this); //初始化xutils

        //开启获取网络类型的服务
        startService(new Intent(this, NetWorkTypeService.class));
    }


}
