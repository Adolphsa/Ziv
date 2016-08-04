package com.zividig.ziv.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 判断手机的网络类型
 * Created by Administrator on 2016-08-04.
 */
public class NetworkTypeUtils {

    public static String NO_NETWORK = "no_wifi";
    public static String WIFI = "wifi";
    public static String MOBILE_NETWORK = "mobile_network";

    /**
     * 获取手机的网络类型
     * @param context 上下文
     * @return  网络类型
     */
    public static String getNetworkType(Context context){
        String strNetworkType = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null){  //无网络
                strNetworkType = NO_NETWORK;
                System.out.println(strNetworkType);
            }else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){ //wifi
                strNetworkType = WIFI;
                System.out.println(strNetworkType);
            }else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE){ //手机网络
                strNetworkType = MOBILE_NETWORK;
                System.out.println(strNetworkType);
            }

        }
        return strNetworkType;
    }

}
