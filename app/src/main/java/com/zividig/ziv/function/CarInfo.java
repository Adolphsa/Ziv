package com.zividig.ziv.function;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    private Subscription mSubscription;
    private SharedPreferences mSpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinfo);

        mSpf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        mSpf.edit().putBoolean("is_keeping_get_device_state",true).apply();

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
        RxGetDeviceState();
    }

    public void initView() {
        initAnimation();
    }

    public void initAnimation(){

        System.out.println("动画被调用");
        //速度动画
        speedRotate = new RotateAnimation(0,speedTest[0], Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        speedRotate.setDuration(2000);
        speedRotate.setFillAfter(true);
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


    /**
     * 配置options
     * @return options
     */
    private Map<String, String> setOp(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                token);

        //配置请求头
        Map<String, String> options = new HashMap<>();
        options.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        options.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        options.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        options.put(SignatureUtils.SIGNATURE_STRING, signature);

        return options;
    }

    /**
     * 配置jsonBody
     * @return  RequestBody
     */
    private RequestBody setBody(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //配置请求体
        //配置请求体
        DeviceStateBody deviceStateBody = new DeviceStateBody();
        deviceStateBody.devid = devid;
        deviceStateBody.token = token;
        String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        return jsonBody;
    }

    /**
     * 轮询获取设备状态
     */
    public void RxGetDeviceState() {

        mSubscription = Observable.interval(0,30, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<DeviceStateResponse>>() {
                    @Override
                    public Observable<DeviceStateResponse> call(Long aLong) {
                        Map<String, String> options = setOp();
                        RequestBody jsonBody = setBody();
                        return ZivApiManage.getInstance().getZivApiService().getDeviceStateInfo(options, jsonBody);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceStateResponse>() {
                    @Override
                    public void call(DeviceStateResponse deviceStateResponse) {
                        DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
                        if (infoBean != null){
                            String voltage = infoBean.getVoltage();
                            tvVoltage.setText("电压:   " + voltage + "V");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("RXJAVA---设备状态出错---" + throwable.getMessage());
                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
    }
}
