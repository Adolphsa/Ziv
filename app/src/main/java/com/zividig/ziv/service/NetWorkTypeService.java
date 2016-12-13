package com.zividig.ziv.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.Urls;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

public class NetWorkTypeService extends Service {

    private Timer mTimer;
    private TimerTask mTimerTask;

    public boolean isWifiOrMobile;
    private SharedPreferences spf;

    public NetWorkTypeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        spf = getSharedPreferences("config",MODE_PRIVATE);

        //定时器相关
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
               getNetWorkType();
            }
        };
        mTimer.schedule(mTimerTask,0,2000); //每2秒启动一次任务
    }

    public void getNetWorkType(){
        if (NetworkTypeUtils.getNetworkType(this).equals(NetworkTypeUtils.WIFI)){
            System.out.println("连接设备");
            RequestParams params = new RequestParams(Urls.GET_DEVICE_INFO_WIFI);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("wifi直连" + result);
                    if (!result.isEmpty()){
                        isWifiOrMobile = true;
                        spf.edit().putBoolean("network_type",isWifiOrMobile).apply();
                        System.out.println("网络类型---" + isWifiOrMobile);
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("wifi直连错误" + ex);
                    isWifiOrMobile = false;
                    spf.edit().putBoolean("network_type",isWifiOrMobile).apply();
                    System.out.println("网络类型---" + isWifiOrMobile);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer!=null){
            mTimer.cancel();
        }
    }
}
