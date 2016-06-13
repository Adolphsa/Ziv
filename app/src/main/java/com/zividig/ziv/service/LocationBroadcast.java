package com.zividig.ziv.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zividig.ziv.bean.LocationBean;

/**
 * 接收位置信息的广播
 * Created by Administrator on 2016-06-13.
 */
public class LocationBroadcast extends BroadcastReceiver{

    public static LocationBean locationBean;

    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("收到位置信息的广播了");
//        locationBean = intent.getParcelableExtra(LocationService.PAR_KEY);
//        System.out.println("广播中的" + locationBean.getLat() + "---" + locationBean.getTi());
    }
}
