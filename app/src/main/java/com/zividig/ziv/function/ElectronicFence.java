package com.zividig.ziv.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnGeoFenceListener;
import com.zividig.ziv.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 电子围栏
 */
public class ElectronicFence extends Activity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    protected  MapStatusUpdate msUpdate = null;

    BitmapDescriptor realtimeBitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);
    protected static OverlayOptions overlay;  // 覆盖物
    private Button setFence;

    private static LBSTraceClient client;
    protected static long serviceId =  113838  ; // serviceId为开发者创建的鹰眼服务ID
    protected static String entityName = null;


    private double latitude = 0;  // 围栏圆心纬度

    private double longitude = 0;  // 围栏圆心经度

    protected static int radius = 100;  // 围栏半径

    protected static int radiusTemp = radius;

    protected static int fenceId = 0;  // 围栏编号

    private int delayTime = 5;   // 延迟时间（单位: 分）

    protected static OnGeoFenceListener geoFenceListener = null;  // 地理围栏监听器

    protected static OverlayOptions fenceOverlay = null;  // 围栏覆盖物

    protected static OverlayOptions fenceOverlayTemp = null;

    protected static boolean isShow = false;

    protected BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener() {

        public void onMapClick(LatLng arg0) {

            mBaiduMap.clear();
            latitude = arg0.latitude;
            longitude = arg0.longitude;

            MapStatus mMapStatus = new MapStatus.Builder().target(arg0).zoom(18).build();
            msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

            overlay = new MarkerOptions().position(arg0)
                    .icon(realtimeBitmap).zIndex(9).draggable(true);
            mBaiduMap.addOverlay(overlay);

            fenceOverlayTemp = fenceOverlay;
            fenceOverlay = new CircleOptions().fillColor(0x000000FF).center(arg0)
                    .stroke(new Stroke(5, Color.rgb(0xff, 0x00, 0x33)))
                    .radius(radius);

            addMarker();
            createOrUpdateDialog();
        }

        public boolean onMapPoiClick(MapPoi arg0) {

            return false;
        }
    };

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
        mBaiduMap = mMapView.getMap();

        client = new LBSTraceClient(getApplicationContext());
        entityName = "myTrace";

        initMap();
        initOnGeoFenceListener();
    }

    public void initMap(){
        LatLng ll = new LatLng(22.551906,113.93208);

        overlay = new MarkerOptions().position(ll)
                .icon(realtimeBitmap).zIndex(9).draggable(true);
        MapStatus.Builder builder = new MapStatus.Builder();

        builder.target(ll).zoom(18.0f);
        mBaiduMap.addOverlay(overlay);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 设置围栏  按钮点击事件
     * @param view
     */
    public void setFence(View view){
        inputDialog();
         mBaiduMap.setOnMapClickListener(mapClickListener);
    }

    /**
     * 创建围栏（若创建围栏时，还未创建entity标识，请先使用addEntity(...)添加entity）
     */
    private void createFence() {

        // 创建者（entity标识）
        String creator = entityName;
        // 围栏名称
        String fenceName = entityName + "_fence";
        // 围栏描述
        String fenceDesc = "test";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 围栏圆心（圆心位置, 格式 : "经度,纬度"）
        String center = longitude + "," + latitude;
        // 围栏半径（单位 : 米）
        double radius = ElectronicFence.radius;
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;

        client.createCircularFence(serviceId, creator, fenceName, fenceDesc,
                monitoredPersons, observers,
                validTimes, validCycle, validDate, validDays, coordType, center, radius, alarmCondition,
                geoFenceListener);

    }

    /**
     * 删除围栏
     */
    @SuppressWarnings("unused")
    private static void deleteFence(int fenceId) {
        client.deleteFence(serviceId, fenceId, geoFenceListener);
    }

    /**
     * 更新围栏
     */
    private void updateFence() {
        // 围栏名称
        String fenceName = entityName + "_fence";
        // 围栏ID
        int fenceId = ElectronicFence.fenceId;
        // 围栏描述
        String fenceDesc = "test fence";
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = entityName;
        // 观察者列表（多个entityName，以英文逗号"," 分割）
        String observers = entityName;
        // 生效时间列表
        String validTimes = "0800,2300";
        // 生效周期
        int validCycle = 4;
        // 围栏生效日期
        String validDate = "";
        // 生效日期列表
        String validDays = "";
        // 坐标类型 （1：GPS经纬度，2：国测局经纬度，3：百度经纬度）
        int coordType = 3;
        // 围栏圆心（圆心位置, 格式 : "经度,纬度"）
        String center = longitude + "," + latitude;
        // 围栏半径（单位 : 米）
        double radius = ElectronicFence.radius;
        // 报警条件（1：进入时触发提醒，2：离开时触发提醒，3：进入离开均触发提醒）
        int alarmCondition = 3;

        client.updateCircularFence(serviceId, fenceName, fenceId, fenceDesc,
                monitoredPersons,
                observers, validTimes, validCycle, validDate, validDays, coordType, center, radius, alarmCondition,
                geoFenceListener);
    }

    /**
     * 围栏列表
     */
    private void queryFenceList() {
        // 创建者（entity标识）
        String creator = entityName;
        // 围栏ID列表
        String fenceIds = "";
        client.queryFenceList(serviceId, creator, fenceIds, geoFenceListener);
    }

    /**
     * 监控状态
     */
    private void monitoredStatus() {
        // 围栏ID
        int fenceId = ElectronicFence.fenceId;
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = entityName;
        client.queryMonitoredStatus(serviceId, fenceId, monitoredPersons,
                geoFenceListener);
    }

    /**
     * 报警信息
     */
    private void historyAlarm() {
        // 围栏ID
        int fenceId = ElectronicFence.fenceId;
        // 监控对象列表（多个entityName，以英文逗号"," 分割）
        String monitoredPersons = entityName;
        // 开始时间（unix时间戳）
        int beginTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        // 结束时间（unix时间戳）
        int endTime = (int) (System.currentTimeMillis() / 1000);

        client.queryFenceHistoryAlarmInfo(serviceId, fenceId, monitoredPersons, beginTime,
                endTime,
                geoFenceListener);
    }

    /**
     * 初始化OnGeoFenceListener
     */
    private void initOnGeoFenceListener() {
        geoFenceListener = new OnGeoFenceListener() {

            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                mBaiduMap.clear();
                if (null != fenceOverlayTemp) {
                    fenceOverlay = fenceOverlayTemp;
                    fenceOverlayTemp = null;
                }
                radius = radiusTemp;
                addMarker();

            }

            // 创建圆形围栏回调接口
            @Override
            public void onCreateCircularFenceCallback(String arg0) {
                // TODO Auto-generated method stub

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    int status = dataJson.getInt("status");
                    if (0 == status) {
                        fenceId = dataJson.getInt("fence_id");
                        fenceOverlayTemp = null;
                        System.out.println("围栏创建成功");
                    } else {
                        mBaiduMap.clear();
                        fenceOverlay = fenceOverlayTemp;
                        fenceOverlayTemp = null;
                        radius = radiusTemp;
                        addMarker();
                        System.out.println("创建圆形围栏回调接口消息 : " + arg0);
                    }
                } catch (JSONException e) {

                    System.out.println("解析创建围栏回调消息失败");
                }

            }

            // 更新圆形围栏回调接口
            @Override
            public void onUpdateCircularFenceCallback(String arg0) {

                System.out.println("更新圆形围栏回调接口消息 : " + arg0);
            }

            // 延迟报警回调接口
            @Override
            public void onDelayAlarmCallback(String arg0) {

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    int status = dataJson.getInt("status");
                    if (0 == status) {
                        System.out.println(delayTime + "分钟内不再报警");
                    } else {
                        System.out.println("延迟报警回调接口消息 : " + arg0);
                    }
                } catch (JSONException e) {

                    System.out.println("解析延迟报警回调消息失败");
                }
            }

            // 删除围栏回调接口
            @Override
            public void onDeleteFenceCallback(String arg0) {

                System.out.println(" 删除围栏回调接口消息 : " + arg0);
            }

            // 查询围栏列表回调接口
            @Override
            public void onQueryFenceListCallback(String arg0) {

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    int status = dataJson.getInt("status");
                    if (0 == status) {
                        if (dataJson.has("size")) {
                            JSONArray jsonArray = dataJson.getJSONArray("fences");
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            fenceId = jsonObj.getInt("fence_id");
                            JSONObject center = jsonObj.getJSONObject("center");

                            latitude = center.getDouble("latitude");
                            longitude = center.getDouble("longitude");
                            radius = (int) (jsonObj.getDouble("radius"));

                            LatLng latLng = new LatLng(latitude, longitude);

                            MapStatus mMapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();
                            msUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

                            fenceOverlay = new CircleOptions().fillColor(0x000000FF).center(latLng)
                                    .stroke(new Stroke(5, Color.rgb(0xff, 0x00, 0x33)))
                                    .radius(radius);

                            addMarker();
                        }
                    }
                } catch (JSONException e) {

                    System.out.println("解析围栏列表回调消息失败");
                }

            }

            // 查询历史报警回调接口
            @Override
            public void onQueryHistoryAlarmCallback(String arg0) {

                System.out.println(" 查询历史报警回调接口消息 : " + arg0);
            }

            // 查询监控对象状态回调接口
            @Override
            public void onQueryMonitoredStatusCallback(String arg0) {

                JSONObject dataJson = null;
                try {
                    dataJson = new JSONObject(arg0);
                    int status = dataJson.getInt("status");
                    if (0 == status) {
                        int size = dataJson.getInt("size");
                        if (size >= 1) {
                            JSONArray jsonArray = dataJson.getJSONArray("monitored_person_statuses");
                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                            String mPerson = jsonObj.getString("monitored_person");
                            int mStatus = jsonObj.getInt("monitored_status");
                            if (1 == mStatus) {
                                System.out.println("监控对象[ " + mPerson + " ]在围栏内");
                            } else {
                                System.out.println("监控对象[ " + mPerson + " ]在围栏外");
                            }
                        }
                    } else {
                        System.out.println("查询监控对象状态回调消息 : " + arg0);
                    }
                } catch (JSONException e) {

                    System.out.println("解析查询监控对象状态回调消息失败");
                }
            }
        };
    }

    // 输入围栏信息对话框
    private void inputDialog() {

        final EditText circleRadius = new EditText(this);
        circleRadius.setFocusable(true);
        circleRadius.setText(radius + "");
        circleRadius.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("围栏半径(单位:米)").setView(circleRadius)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        mBaiduMap.setOnMapClickListener(null);
                    }

                });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String radiusStr = circleRadius.getText().toString();
                if (!TextUtils.isEmpty(radiusStr)) {
                    radiusTemp = radius;
                    radius = Integer.parseInt(radiusStr) > 0 ? Integer.parseInt(radiusStr) : radius;
                }
                Toast.makeText(ElectronicFence.this, "请点击地图标记围栏圆心", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    /**
     * 设置围栏对话框
     */
    private void createOrUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("确定设置围栏?");

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mBaiduMap.clear();
                // 添加覆盖物
                if (null != fenceOverlayTemp) {
                    fenceOverlay = fenceOverlayTemp;
                }
                radius = radiusTemp;
                addMarker();
                mBaiduMap.setOnMapClickListener(null);
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (0 == fenceId) {
                    // 创建围栏
                    createFence();
                } else {
                    // 更新围栏
                    updateFence();
                }
                mBaiduMap.setOnMapClickListener(null);
            }
        });
        builder.show();
    }

    /**
     * 提示信息
     * @param message
     */
    public void showMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    /**
     * 添加地图覆盖物
     */
    protected  void addMarker() {

        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }


        // 围栏覆盖物
        if (null != ElectronicFence.fenceOverlay) {
            mBaiduMap.addOverlay(ElectronicFence.fenceOverlay);
        }

//        // 实时点覆盖物
//        if (null != overlay) {
//            mBaiduMap.addOverlay(overlay);
//        }
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
