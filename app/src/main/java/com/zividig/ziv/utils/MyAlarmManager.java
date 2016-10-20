package com.zividig.ziv.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adolph
 * on 2016-10-20.
 */

public class MyAlarmManager {

    //开启轮询服务
    public static void startPollingService(Context context, int seconds, Class cls, String devId) {

        System.out.println("开启轮询服务");
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        intent.putExtra("devid",devId);//添加需要传递的一些参数
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//我是用的是service
        //使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), seconds * 1000, pendingIntent);

    }

    //停止轮询服务
    public static void stopPollingService(Context context, Class cls,String action) {
        System.out.println("停止轮询服务");
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls.getClass());
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //取消正在执行的服务
        manager.cancel(pendingIntent);
    }
}
