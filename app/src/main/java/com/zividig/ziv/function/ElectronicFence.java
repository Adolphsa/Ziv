package com.zividig.ziv.function;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnGeoFenceListener;
import com.zividig.ziv.R;

/**
 * 电子围栏
 */
public class ElectronicFence extends Activity {

    private MapView mMapView;
    private Button setFence;

    private LBSTraceClient client;

    protected static OnGeoFenceListener geoFenceListener = null; // 地理围栏监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_fence);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("电子围栏");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setFence = (Button) findViewById(R.id.bt_electric_fence);
        mMapView = (MapView) findViewById(R.id.electronic_map);

        client = new LBSTraceClient(getApplicationContext());
    }

    public void setFence(View view){
        System.out.println("设置围栏被点击了");
        //鹰眼服务ID
        long serviceId = 118422; // <开发者创建的鹰眼服务的ID>;
        //创建者（entity标识）
        String creator = "mom";
        //围栏名称
        String fenceName = "school";
        //围栏描述
        String fenceDesc = "学校";
        //监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = "daughter";
        //观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = "mom,dad";
        //生效时间列表
        String validTimes = "0800,1630";
        //生效周期
        int validCycle = 5;
        //围栏生效日期
        String validDate = "";
        //生效日期列表
        String validDays = "";
        //坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        //围栏圆心（圆心位置, 格式 : "经度,纬度"）
        String center = "116.838463,40.263548";
        //围栏半径（单位 : 米）
        double radius = 500;
        //报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;

        //创建圆形地理围栏
        client.createCircularFence(serviceId, creator, fenceName, fenceDesc,
                monitoredPersons, observers,
                validTimes, validCycle, validDate, validDays,
                coordType, center, radius, alarmCondition, geoFenceListener);

    }
    @Override
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
    }
}
