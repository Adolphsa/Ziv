package com.zividig.ziv.function;

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

import java.util.Timer;
import java.util.TimerTask;

/**
 * 车辆信息
 * Created by Administrator on 2016-05-30.
 */
public class CarInfo extends BaseActivity {

    private ImageView speedPoint;
    private  ImageView oilPoint;
    private  ImageView turnSpeedPoint;
    private  ImageView temperaturePoint;
    private ImageView voltagePoint;
    private RotateAnimation speedRotate; //速度动画
    private int [] speedTest = {120,50,100,80,180,60,30,150,50};
    private int i;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                int n = i%9;
                System.out.println("接收到消息了");
                speedRotate = new RotateAnimation(0,speedTest[n], Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                speedRotate.setDuration(1000);
                speedRotate.setFillAfter(true);
//                speedRotate.setRepeatCount(Animation.INFINITE);
                speedPoint.setAnimation(speedRotate);
                i++;
            }
        }
    };
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinfo);

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
        temperaturePoint = (ImageView) findViewById(R.id.img_temperature_point);

        initView();
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
    }
}
