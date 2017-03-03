package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.zividig.ziv.bean.DeviceStateInfoBean;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

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
        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid",devID);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devID,
                SignatureUtils.token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_STATE,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        System.out.println("获取设备状态---" + params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("结果" + result);
                DeviceStateInfoBean stateInfoBean = JsonUtils.deserialize(result, DeviceStateInfoBean.class);
                int status = stateInfoBean.getStatus();
                if (200 == status){
                    DeviceStateInfoBean.InfoBean infoBean = stateInfoBean.getInfo();
                    String workMode = infoBean.getWorkmode();
                    String voltage = infoBean.getVoltage();
                    if (!workMode.isEmpty()) {
                        //发送广播
                        Intent broadcast = new Intent();
                        broadcast.setAction(DEVICE_STATE_ACTION);
                        broadcast.putExtra("device_state",workMode);
                        broadcast.putExtra("voltage",voltage);
                        sendBroadcast(broadcast);
                        System.out.println("发送广播");
                    }
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
