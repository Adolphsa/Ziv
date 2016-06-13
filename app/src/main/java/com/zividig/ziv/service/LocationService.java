package com.zividig.ziv.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zividig.ziv.bean.LocationBean;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    public static final String LOCATION_ACTION = "com.zividig.ziv.service.location";
    public static final String PAR_KEY = "location_parcelable";

    private Timer mTimer;
    private TimerTask mTimerTask;

    private static String URL = "http://dev.caowei.name/mytest/uploadtest/localtionhis_realtime.php";
    private LocationBean locationBean;
    private RequestParams params;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("位置信息服务开启");
        params = new RequestParams(URL);
        params.addBodyParameter("deviceId", "1234567890123456789");
        locationBean = new LocationBean();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getLocation();
            }
        };
        mTimer.schedule(mTimerTask,3000,3000); //每3秒启动一次任务
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取位置信息
     */
    public void getLocation() {
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("位置信息返回成功" + result);
                Gson gson = new Gson();
                locationBean = gson.fromJson(result, LocationBean.class);
                System.out.println("纬度：" + locationBean.getLon() + "经度：" + locationBean.getLat());

                //发送广播
                Intent broadcast = new Intent();
                broadcast.setAction(LOCATION_ACTION);
                broadcast.putExtra(PAR_KEY,locationBean);
                sendBroadcast(broadcast);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LocationService.this, "获取位置信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                System.out.println("位置信息获取完成");
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer!=null){
            mTimer.cancel();
        }
    }
}
