package com.zividig.ziv.main;

import android.app.Application;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Application
 * Created by Administrator on 2016-06-29.
 */
public class ZivApp extends Application{

    public boolean isWifiOrMobile;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this); //初始化xutils

        wifiOrMobile(); //判断是wifi还是手机网络
    }

    public void wifiOrMobile(){
        RequestParams params = new RequestParams("http://192.168.1.1/api/getdevinfo");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("wifi直连" + result);
                if (!result.isEmpty()){
                    isWifiOrMobile = true;
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("wifi直连错误" + ex);
                isWifiOrMobile = false;

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public boolean getIsWifiOrMobile(){
        return isWifiOrMobile;
    }
}
