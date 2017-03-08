package com.zividig.ziv.function;

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
import com.baidu.mapapi.model.LatLng;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.MessageBean;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.utils.GPSConverterUtils;

/**
 * Created by adolph
 * on 2017-02-13.
 */

public class MessageMapShow extends BaseActivity {

    private TextView mmsTvAlarmType;
    private TextView mmsTvAddress;
    private TextView mmsTvAddressDescribe;
    private TextView mmsTvAlarmTime;

    private MapView baiduMapView;
    private BaiduMap mBaiduMap;
    private MapStatus.Builder mBuilder;

    BitmapDescriptor realtimeBitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);

    private MessageBean.DataBean dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_map_show);

        Bundle bundle = getIntent().getExtras();
        dataBean = bundle.getParcelable("alarm_message_data");

        System.out.println("地图展示---lat---" + Double.toString(dataBean.getLat())
                + "---lon---" + Double.toString(dataBean.getLon()));
        initView();
        initData();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("信息详情");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mmsTvAlarmType = (TextView) findViewById(R.id.mms_tv_alarm_type);
        mmsTvAddress = (TextView) findViewById(R.id.mms_tv_address);
        mmsTvAddressDescribe = (TextView) findViewById(R.id.mms_tv_address_describe);
        mmsTvAlarmTime = (TextView) findViewById(R.id.mms_tv_time);

        baiduMapView = (MapView) findViewById(R.id.mms_map);
        mBaiduMap = baiduMapView.getMap();
        mBuilder = new MapStatus.Builder();
    }

    private void initData(){
        mmsTvAlarmType.setText("震动类型 : " + dataBean.getTitle());
        mmsTvAddress.setText("地        址 : " + dataBean.getAddress());
        mmsTvAddressDescribe.setText("地址详情 : " + dataBean.getAddress_desc());
        mmsTvAlarmTime.setText("时        间 : " + dataBean.getTime());

        LatLng latLng = new LatLng(dataBean.getLat(),dataBean.getLon());
        LatLng convertLatLon =  GPSConverterUtils.gpsToBaidu(latLng);

        MarkerOptions markerOptions = new MarkerOptions().icon(realtimeBitmap).position(convertLatLon);
        mBaiduMap.addOverlay(markerOptions);
        mBuilder.target(convertLatLon).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));
    }

    protected void onResume() {
        super.onResume();
        baiduMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        baiduMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduMapView.onDestroy();
        if (mBaiduMap != null){
            mBaiduMap = null;
        }
    }
}
