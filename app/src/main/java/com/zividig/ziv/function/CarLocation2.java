package com.zividig.ziv.function;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.baidu.mapapi.utils.CoordinateConverter;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.LocationBean;
import com.zividig.ziv.service.LocationService;
import com.zividig.ziv.utils.ToastShow;

/**
 * 获取GPS的定位的信息的车辆定位
 * Created by Administrator on 2016-06-14.
 */
public class CarLocation2 extends Activity{

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    protected static OverlayOptions overlay;  // 覆盖物
    private boolean isFirst = true;
    private boolean once = true;
    BitmapDescriptor carIcon = BitmapDescriptorFactory
            .fromResource(R.mipmap.car_icon);

    //动态注册的广播
    private BroadcastReceiver locationBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocationBean locationBean = intent.getParcelableExtra(LocationService.PAR_KEY);
            initMap(locationBean.getLat(),locationBean.getLon());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carlocation2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.LOCATION_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(locationBroadcast, filter);

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
    }

    public void initMap(Double lat,Double lon){

        if (lat == 0 && lon == 0){
            if (once){
                ToastShow.showToast(CarLocation2.this,"暂无地图数据");
                once = false;
            }

        }else {
            mBaiduMap.clear();
            LatLng sourceLatLng = new LatLng(lat,lon);
            //坐标转换
            CoordinateConverter converter = new CoordinateConverter();
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(sourceLatLng);
            LatLng desLatLng = converter.convert();

            //标注
            overlay = new MarkerOptions().position(desLatLng).icon(carIcon).zIndex(9).draggable(true);

            if (isFirst){
                isFirst = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(desLatLng).zoom(18.0f);
                mBaiduMap.addOverlay(overlay);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            }else {
                mBaiduMap.addOverlay(overlay);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationBroadcast);
    }
}
