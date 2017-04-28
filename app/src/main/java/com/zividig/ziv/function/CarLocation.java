package com.zividig.ziv.function;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.LocationResponse;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.GPSConverterUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.SharedPreferencesUtils;
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
 * 获取GPS的定位的信息的车辆定位
 * Created by Administrator on 2016-06-14.
 */
public class CarLocation extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private MapStatus.Builder mBuilder;
    protected static OverlayOptions overlay;  // 覆盖物
    private boolean isFirst = true;

    private View view1;
    private TextView mapTime;
    BitmapDescriptor carIcon;
    double lat;
    double lon;
    long unixTime;

    private SharedPreferences spf;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlocation);

        spf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        view1 = View.inflate(this,R.layout.layout_map_lable,null);
        mapTime = (TextView) view1.findViewById(R.id.car_location_text);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("车辆定位");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMapView = (MapView) findViewById(R.id.carlocation2_map);
        mBaiduMap = mMapView.getMap();
        mBuilder = new MapStatus.Builder();

        //设定地图的初始中心
        String stringLat = SharedPreferencesUtils.getString(CarLocation.this, "ziv_lat", "0.0");
        String stringLon = SharedPreferencesUtils.getString(CarLocation.this, "ziv_lon", "0.0");

        if (!stringLat.equals("0.0") && !stringLon.equals("0.0")){
            System.out.println("lat---" + Double.valueOf(stringLat));
            System.out.println("lon---" + Double.valueOf(stringLon));
            double initLat = Double.valueOf(stringLat);
            double initLon = Double.valueOf(stringLon);
            mBuilder.target(GPSConverterUtils.gpsToBaidu(new LatLng(initLat,initLon)))
                    .zoom(16.0f);

        }else {
            System.out.println("默认22.549467");
            mBuilder.target(GPSConverterUtils.gpsToBaidu(new LatLng(22.549467,113.920565)))
                    .zoom(16.0f);
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

        RxGetLocationInfo();

    }

    public void initMap(Double lat,Double lon,String maptime){

            mBaiduMap.clear();
            LatLng sourceLatLng = new LatLng(lat,lon);
            //坐标转换
            LatLng desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
            System.out.println("转换后的经纬度---" + "lat---" + desLatLng.latitude + "lon---" + desLatLng.longitude);

            mapTime.setText(maptime);
            carIcon = BitmapDescriptorFactory
                    .fromView(view1);
            //标注
            overlay = new MarkerOptions().position(desLatLng).icon(carIcon).zIndex(9).draggable(true);

            if (isFirst){
                isFirst = false;
                mBuilder = new MapStatus.Builder();
                mBuilder.target(desLatLng).zoom(18.0f);
                mBaiduMap.addOverlay(overlay);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

            }else {
                System.out.println("增加点");
                mBaiduMap.addOverlay(overlay);

                Point pt= mBaiduMap.getMapStatus().targetScreen;
                Point point= mBaiduMap.getProjection().toScreenLocation(desLatLng);
                if(point.x < pt.x*0.4 || point.x > pt.x*1.6 || point.y < pt.y*0.4 || point.y > pt.y*1.6)
                {
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(desLatLng));
                }
            }

    }


    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMapView.onDestroy();
        if (mBaiduMap != null){
            mBaiduMap = null;
        }
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
        //保存退出时的经纬度
        SharedPreferencesUtils.putString(CarLocation.this,"ziv_lat",String.valueOf(lat));
        SharedPreferencesUtils.putString(CarLocation.this,"ziv_lon",String.valueOf(lon));
    }


    /**
     * 配置options
     * @return options
     */
    private Map<String, String> setOp(){

        String token = spf.getString("token", null);
        String devid = spf.getString("devid", null);

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

        String devid = spf.getString("devid", null);
        String token = spf.getString("token", null);

        //配置请求体
        //配置请求体
        DeviceStateBody deviceStateBody = new DeviceStateBody();
        deviceStateBody.devid = devid;
        deviceStateBody.token = token;
        String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        return jsonBody;
    }

    private void RxGetLocationInfo(){

        mSubscription = Observable.interval(0,1, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<LocationResponse>>() {
                    @Override
                    public Observable<LocationResponse> call(Long aLong) {
                        Map<String, String> options = setOp();
                        RequestBody jsonBody = setBody();
                        return ZivApiManage.getInstance().getZivApiService().getLocationInfo(options,jsonBody);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LocationResponse>() {
                    @Override
                    public void call(LocationResponse locationResponse) {
                        System.out.println("Gps信息---" + locationResponse.toString());
                        handLocationInfo(locationResponse);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("地图异常---" + throwable.getMessage());
                        DialogUtils.showPrompt(CarLocation.this, "提示", "暂无数据", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                    }
                });
    }

    private void handLocationInfo(LocationResponse locationResponse){
        int status = locationResponse.getStatus();
        if (200 == status){
            LocationResponse.GpsBean gpsBean = locationResponse.getGps();
            if (gpsBean != null){
                lat = gpsBean.getLat();
                lon = gpsBean.getLon();
                unixTime  = Long.parseLong(gpsBean.getTi());
                String maptime =  UtcTimeUtils.unixTimeToDate(unixTime);
                initMap(lat,lon,maptime);
            }
        }
    }
}
