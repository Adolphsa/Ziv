package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.gson.Gson;
import com.zividig.ziv.bean.LocationBean;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

public class LocationService extends Service {

    public static final String LOCATION_ACTION = "com.zividig.ziv.service.location";
    public static final String PAR_KEY = "location_parcelable";

    private static String URL = "http://120.25.80.80:9501/api/getrealtimelocation";
    private LocationBean locationBean;
    private RequestParams params;
    private SharedPreferences spf;
    private Gson mGson;

    private double lon;
    private double lat;

    public LocationService() {}

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("位置信息服务开启");
        mGson = new Gson();
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

        //配置JSON
        JSONObject json = new JSONObject();
        try {
            json.put("devid", devid);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(
                timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                SignatureUtils.token);

        //请求参数相关
        RequestParams params = HttpParamsUtils.setParams(Urls.REAL_LOCATION,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        System.out.println("获取实时位置的URL---" + params);
        locationBean = new LocationBean();

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("位置信息返回成功" + result);

                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (status == Urls.STATUS_CODE_200){

                        JSONObject gps = json.getJSONObject("gps");

                        locationBean = mGson.fromJson(gps.toString(), LocationBean.class);

                        lon = locationBean.getLon();
                        lat = locationBean.getLat();
                        System.out.println("纬度：" + lon + "经度：" + lat);

                        //发送广播
                        Intent broadcast = new Intent();
                        broadcast.setAction(LOCATION_ACTION);
                        broadcast.putExtra(PAR_KEY,locationBean);
                        sendBroadcast(broadcast);
                    }else {
                        System.out.println("获取实时位置返回码不为200");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
