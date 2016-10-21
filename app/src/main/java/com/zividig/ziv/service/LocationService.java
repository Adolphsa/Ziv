package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.gson.Gson;
import com.zividig.ziv.bean.LocationBean;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LocationService extends Service {

    public static final String LOCATION_ACTION = "com.zividig.ziv.service.location";
    public static final String PAR_KEY = "location_parcelable";

    private static String URL = "http://dev.caowei.name/mytest/uploadtest/localtionhis_realtime.php";
    private LocationBean locationBean;
    private RequestParams params;
    private SharedPreferences spf;

    public LocationService() {}

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("位置信息服务开启");
        spf = getSharedPreferences("config",MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取位置信息
     */
    public void getLocation() {

        String devid = spf.getString("devid","");
//        System.out.println("服务中的devid---" + devid);

        //请求参数相关
        params = new RequestParams(URL);
        params.addBodyParameter("deviceId", devid);

//        System.out.println("gps信息---" + params.toString());
        locationBean = new LocationBean();
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                System.out.println("位置信息返回成功" + result);
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
                System.out.println("获取位置信息错误" + ex);
//                Toast.makeText(LocationService.this, "获取位置信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
