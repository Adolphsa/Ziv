package com.zividig.ziv.function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.service.DeviceStateService;
import com.zividig.ziv.utils.MyAlarmManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 车辆信息
 * Created by Administrator on 2016-05-30.
 */
public class CarInfo extends BaseActivity {

    private static final int VOLTAGE = 60;

    private ImageView speedPoint;
    private  ImageView oilPoint;
    private  ImageView turnSpeedPoint;
    private  ImageView temperaturePoint;
    private ImageView voltagePoint;
    private TextView tvVoltage;
    private RotateAnimation speedRotate; //速度动画
    private int [] speedTest = {120,50,100,80,180,60,30,150,50};
    private int i;

    private String mVoltage;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int n = i%9;
                    System.out.println("接收到消息了");
                    speedRotate = new RotateAnimation(0,speedTest[n], Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    speedRotate.setDuration(1000);
                    speedRotate.setFillAfter(true);
//                speedRotate.setRepeatCount(Animation.INFINITE);
                    speedPoint.setAnimation(speedRotate);
                    i++;
                    break;

                case VOLTAGE:
                    tvVoltage.setText("电压:   " + mVoltage + "V");
                    break;
            }

        }
    };
    private Timer timer;

    //广播接收
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mVoltage = intent.getStringExtra("voltage");
            mHandler.sendEmptyMessage(VOLTAGE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinfo);

        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("车辆信息");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        speedPoint = (ImageView) findViewById(R.id.img_speed_point);
//        oilPoint = (ImageView) findViewById(R.id.img_oil_point);
//        turnSpeedPoint = (ImageView) findViewById(R.id.img_turning_point); img_voltage_point
        voltagePoint = (ImageView) findViewById(R.id.img_voltage_point);
        tvVoltage = (TextView) findViewById(R.id.tv_voltage);
        temperaturePoint = (ImageView) findViewById(R.id.img_temperature_point);

        initView();

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceStateService.DEVICE_STATE_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(br, filter);
    }

    public void initView() {

        initAnimation();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("执行一次");
                mHandler.sendEmptyMessage(0);
            }
        }, 0, 10000000);
    }

    public void initAnimation(){

        System.out.println("动画被调用");
        //速度动画
        speedRotate = new RotateAnimation(0,speedTest[0], Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        speedRotate.setDuration(2000);
        speedRotate.setFillAfter(true);
        speedRotate.setRepeatCount(Animation.INFINITE);
        speedPoint.setAnimation(speedRotate);


//        //油压动画
//        RotateAnimation oilRotate = new RotateAnimation(0,100, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        oilRotate.setDuration(500);
//        oilRotate.setFillAfter(true);
//        oilPoint.setAnimation(oilRotate);

//        //转速动画
//        RotateAnimation turnRotate = new RotateAnimation(0,110, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        turnRotate.setDuration(1000);
//        turnRotate.setFillAfter(true);
//        turnSpeedPoint.setAnimation(turnRotate);

        //电量动画
        RotateAnimation voltageRotate = new RotateAnimation(0,60, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        voltageRotate.setDuration(500);
        voltageRotate.setFillAfter(true);
        voltagePoint.setAnimation(voltageRotate);

        //水温动画
        RotateAnimation temperatureRotate = new RotateAnimation(0,60, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        temperatureRotate.setDuration(500);
        temperatureRotate.setFillAfter(true);
        temperaturePoint.setAnimation(temperatureRotate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }

        MyAlarmManager.stopPollingService(CarInfo.this,DeviceStateService.class);
        unregisterReceiver(br);
    }
}
