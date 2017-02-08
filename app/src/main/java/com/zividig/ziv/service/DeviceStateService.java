package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.zividig.ziv.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class DeviceStateService extends Service {

    public static final String DEVICE_STATE_ACTION = "com.zividig.ziv.service.DeviceStateService";

    private SharedPreferences spf;

    private String devID;

    public DeviceStateService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("启动获取设备状态服务");
        spf = getSharedPreferences("config",MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getDeviceState();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void getDeviceState(){
        devID = spf.getString("devid","");
        RequestParams params2 = new RequestParams(Urls.DEVICE_STATE);
        params2.addBodyParameter("devid", devID);
        System.out.println("获取设备状态---" + params2.toString());
        x.http().get(params2, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("结果" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    String workMode = json.getString("workmode");
                    if (!workMode.isEmpty()) {
                        //发送广播
                        Intent broadcast = new Intent();
                        broadcast.setAction(DEVICE_STATE_ACTION);
                        broadcast.putExtra("device_state",workMode);
                        sendBroadcast(broadcast);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }
}
