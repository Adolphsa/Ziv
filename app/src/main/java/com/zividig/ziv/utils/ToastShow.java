package com.zividig.ziv.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast展示
 * Created by Administrator on 2016-08-05.
 */
public class ToastShow {

    private static Toast sToast = null;

    public static void showToast(Context context,String str){
        if (sToast == null) {
            sToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        }else {
            sToast.setText(str);
        }
        sToast.setGravity(Gravity.CENTER, 0, 0);
        sToast.show();
    }

    /**
     * 根据自己的时间去定义一个Toast 输入时间为毫秒
     * @param c
     * @param info
     * @param time
     */
    public static void setToatBytTime(Context c, String info, int time) {
        final Toast toast = Toast.makeText(c, info, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                toast.cancel();
            }
        }, time);
    }
}
