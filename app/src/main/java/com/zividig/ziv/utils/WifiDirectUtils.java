package com.zividig.ziv.utils;

import android.content.Context;
import android.content.Intent;

import com.dtr.zxing.activity.CaptureActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by adolph
 * on 2016-10-21.
 */

public class WifiDirectUtils {

    public static void isWifiDirect(final Context context){
        if (NetworkTypeUtils.getNetworkType(context).equals(NetworkTypeUtils.WIFI)){
            System.out.println("连接设备");
            RequestParams params = new RequestParams("http://192.168.1.1/api/getdevinfo");
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("wifi直连" + result);
                    if (!result.isEmpty()){
                        context.startActivity(new Intent(context, CaptureActivity.class));
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("wifi直连错误" + ex);
                    ToastShow.showToast(context,"请连接设备WIFI");

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }else {
            ToastShow.showToast(context,"请连接设备WIFI");
        }
    }
}
