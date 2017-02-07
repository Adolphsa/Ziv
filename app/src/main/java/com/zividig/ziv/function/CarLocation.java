package com.zividig.ziv.function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.zividig.ziv.bean.LocationBean;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.service.LocationService;
import com.zividig.ziv.utils.GPSConverterUtils;
import com.zividig.ziv.utils.MyAlarmManager;
import com.zividig.ziv.utils.SharedPreferencesUtils;
import com.zividig.ziv.utils.ToastShow;

/**
 * 获取GPS的定位的信息的车辆定位
 * Created by Administrator on 2016-06-14.
 */
public class CarLocation extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    protected static OverlayOptions overlay;  // 覆盖物
    private boolean isFirst = true;
    private boolean once = true;
    BitmapDescriptor carIcon = BitmapDescriptorFactory
            .fromResource(R.mipmap.car_icon);
    double lat;
    double lon;

    //动态注册的广播
    private BroadcastReceiver locationBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationBean locationBean = intent.getParcelableExtra(LocationService.PAR_KEY);
            lat = locationBean.getLat();
            lon = locationBean.getLon();
            initMap(lat,lon);
        }
    };
    private MapStatus.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlocation2);

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
            mBuilder.target(GPSConverterUtils.gpsToBaidu(new LatLng(22.549467,113.920565)))
                    .zoom(16.0f);
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.LOCATION_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(locationBroadcast, filter);

    }

    public void initMap(Double lat,Double lon){

        if (lat == 0 && lon == 0){
            if (once){
                ToastShow.showToast(CarLocation.this,"暂无地图数据");
                once = false;
            }

        }else {
            mBaiduMap.clear();
            LatLng sourceLatLng = new LatLng(lat,lon);
            //坐标转换
            LatLng desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);

            //标注
            overlay = new MarkerOptions().position(desLatLng).icon(carIcon).zIndex(9).draggable(true);

            if (isFirst){
                isFirst = false;
                mBuilder = new MapStatus.Builder();
                mBuilder.target(desLatLng).zoom(18.0f);
                mBaiduMap.addOverlay(overlay);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

            }else {
//                System.out.println("增加点");
                mBaiduMap.addOverlay(overlay);

                Point pt= mBaiduMap.getMapStatus().targetScreen;
                Point point= mBaiduMap.getProjection().toScreenLocation(desLatLng);
//                System.out.println("point.x = " + point.x);
//                System.out.println("point.y = " + point.y);
//                System.out.println("pt.x = " + pt.x);
//                System.out.println("pt.y = " + pt.y);
                if(point.x < pt.x*0.4 || point.x > pt.x*1.6 || point.y < pt.y*0.4 || point.y > pt.y*1.6)
                {

                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(desLatLng));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyAlarmManager.stopPollingService(CarLocation.this, LocationService.class);
        unregisterReceiver(locationBroadcast);

        //保存退出时的经纬度
        SharedPreferencesUtils.putString(CarLocation.this,"ziv_lat",String.valueOf(lat));
        SharedPreferencesUtils.putString(CarLocation.this,"ziv_lon",String.valueOf(lon));
    }
}
