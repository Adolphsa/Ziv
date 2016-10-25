package com.zividig.ziv.utils;

import android.content.Context;
import android.content.Intent;

import com.zivdigi.helloffmpeg.MyTestActivity;
import com.zivdigi.helloffmpeg.TestDecoder;
import com.zividig.ziv.main.MainActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by adolph
 * on 2016-10-21.
 */

public class WifiDirectUtils {

    public static void WifiDirect(final Context context,final Class cls){
        if (NetworkTypeUtils.getNetworkType(context).equals(NetworkTypeUtils.WIFI)){
            System.out.println("连接设备");
            RequestParams params = new RequestParams("http://192.168.1.1/api/getdevinfo");
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("wifi直连" + result);
                    if (!result.isEmpty()){
                        if (cls.getName().equals(MainActivity.class.getName())){ //进入主界面
                            context.startActivity(new Intent(context, cls));
                            ToastShow.showToast(context,"设备WIFI直连");
                        }else if(cls.getName().equals(MyTestActivity.class.getName())){ //进入wifi视频预览
                            TestDecoder.setUrl("rtsp://192.168.1.1/stream1");
                            context.startActivity(new Intent(context, cls));
                            ToastShow.showToast(context,"设备WIFI预览");
                        }


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
