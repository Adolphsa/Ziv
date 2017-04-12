package com.zividig.ziv.main;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Process;

import com.baidu.mapapi.SDKInitializer;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zividig.ziv.bean.DaoMaster;
import com.zividig.ziv.bean.DaoSession;
import com.zividig.ziv.service.LogService;

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

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this.getApplicationContext()); //初始化百度地图
        x.Ext.init(this);

        //初始化push推送服务
        if(shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        //日志服务
        startService(new Intent(this, LogService.class));

        System.out.println("application");
        if (instance == null) {
            instance = this;
        }

        setDatabase();
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


    private void setDatabase() {

        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this,"ziv-db", null);
        db =mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }
}
