package com.zividig.ziv.main;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.x;

import java.util.List;


/**
 * Application
 * Created by Administrator on 2016-06-29.
 */
public class ZivApp extends Application{

    //小米推送相关
    private static final String APP_ID = "2882303761517521785";
    private static final String APP_KEY = "5451752126785";
    public static final String TAG = "com.zividig.ziv";

    private static ZivApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
//        SDKInitializer.initialize(this.getApplicationContext()); //初始化百度地图
        x.Ext.init(this);

        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        System.out.println("application");
        if (instance == null) {
            instance = this;
        }

    }

    public static ZivApp getInstance() {
        return instance;
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
