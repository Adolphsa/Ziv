package com.zividig.ziv.function;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.LocationResponse;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.GPSConverterUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

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
 * 电子围栏
 */
public class ElectronicFence extends BaseActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private Marker marker;

    BitmapDescriptor realtimeBitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);
    BitmapDescriptor fenceCentrePointBitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.fence_centre_point);
    protected static OverlayOptions overlay;  // 覆盖物

    private boolean once = true;

    private SharedPreferences spf;

    private LatLng mLatLng;
    private double lat;
    private double lon;

    private MapStatus.Builder mBuilder;
    private CoordinateConverter mConverter;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_fence);

        spf = getSharedPreferences("config", Context.MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        spf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText(R.string.fence_title);

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMapView = (MapView) findViewById(R.id.electronic_map);
        mBaiduMap = mMapView.getMap();

        //坐标转换
        mConverter = new CoordinateConverter();
        mConverter.from(CoordinateConverter.CoordType.GPS);

        //设定地图的初始中心
        mBuilder = new MapStatus.Builder();
        mBuilder.target(GPSConverterUtils.gpsToBaidu(new LatLng(22.549467,113.920565)))
                .zoom(16.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

        RxGetLocationInfo();

        String devid = spf.getString("devid","");
        getFenceMessage(devid );
    }


    /**
     *初始化地图    这个地方要一直接收经度来实时更新位置
     */
    public void initMap(Double lat,Double lon){
        if (lat == 0 && lon == 0){
            if (once){
                ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_no_map_data));
                once = false;
            }

        }else {
            LatLng sourceLatLng = new LatLng(lat,lon);
            mConverter.coord(sourceLatLng);
            LatLng desLatLng = mConverter.convert();

            MarkerOptions markerOptions = new MarkerOptions().icon(realtimeBitmap).position(desLatLng);
            Overlay overlay = mBaiduMap.addOverlay(markerOptions);
            marker = (Marker) overlay;
            mBuilder = new MapStatus.Builder();

            mBuilder.target(desLatLng).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mBuilder.build()));

            System.out.println("获取到定位信息");

        }
    }

    /**
     * 设置围栏按钮点击事件
     * @param view v
     */
   public void setFence(View view){
        System.out.println("设置围栏");
         setFenceCentrePoint();
    }

    /**
     * 设置围栏中心点
     */
    private void setFenceCentrePoint(){

        ToastShow.showToast(this,getString(R.string.fence_choose_centre));
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                System.out.println("当前点击的经纬度" + latLng);
                if (marker != null){
                    marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions().icon(fenceCentrePointBitmap).position(latLng);
                marker = (Marker) mBaiduMap.addOverlay(markerOptions);

                //中心点确认对话框
                if (!ElectronicFence.this.isFinishing()) {
                    DialogUtils.showPrompt2(ElectronicFence.this,
                            getString(R.string.add_device_tips),
                            getString(R.string.fence_ensure_center),
                            getString(R.string.add_device_ensure),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //显示设置围栏半径的对话框
                                    showFenceDialog();
                                    mLatLng = latLng;
                                    lat = mLatLng.latitude;
                                    lon = mLatLng.longitude;
                                    System.out.println("地图点击的lat---" + lat + "\n---lon" + lon);
                                }
                            },
                            getString(R.string.add_device_cancle),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }



    /**
     * 显示设置围栏的半径和名称的对话框
     */
    private void showFenceDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.fence_setting_radius);//设置标题
        View view = LayoutInflater.from(this).inflate(R.layout.layout_fence_dialog,null);
        final EditText etFenceRadius = (EditText) view.findViewById(R.id.et_fence_radius);//围栏半径
        final EditText etFenceName = (EditText) view.findViewById(R.id.et_fence_name);//围栏名称
        builder.setView(view);//给对话框设置布局
        builder.setPositiveButton(getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //获取围栏半径和名称
                String fenceRadius = etFenceRadius.getText().toString().trim();
                String fenceName = etFenceName.getText().toString().trim();

                boolean fenceMessage = checkFenceMessage(fenceRadius,fenceName);
                if (fenceMessage){
                    //设置电子围栏到服务器
                    String devid = spf.getString("devid","");
                    setFenceToIntenet(devid,lat,lon,fenceRadius,fenceName,"open");

                    //在地图上显示圆形覆盖物
                    showCircleOverlay(mLatLng,Integer.parseInt(fenceRadius));

                }

            }
        });
        builder.setNegativeButton(getString(R.string.add_device_cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("取消设置围栏半径和名称");
            }
        });
        builder.show();
    }

    /**
     * 检查围栏表单信息的合法性
     * @param fenceRadius 围栏半径
     * @param fenceName   围栏名称
     * @return  true/false
     */
    private boolean checkFenceMessage(String fenceRadius,String fenceName){
        if (TextUtils.isEmpty(fenceRadius)){
            ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_no_radius));
            return false;
        }
        if (Integer.parseInt(fenceRadius) <= 500){
            ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_error));
            return false;
        }
        if (TextUtils.isEmpty(fenceName)){
            ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_no_name));
            return false;
        }
        return true;
    }

    /**
     * 提交设置电子围栏的信息到服务器
     * @param devid             设备ID
     * @param latitude          纬度
     * @param longitude         经度
     * @param radius            围栏半径
     * @param fence_name        围栏名称
     * @param status            围栏状态
     */
    private void setFenceToIntenet(String devid,double latitude, double longitude,String radius,String fence_name,String status){

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("lat",latitude);
            json.put("lon", longitude);
            json.put("radius",radius);
            json.put("name",fence_name);
            json.put("status",status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(Urls.SETTING_FENCE_MESSAGE);
        params.addBodyParameter("devid", devid);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("电子围栏返回信息---" + result);
                if (!TextUtils.isEmpty(result)){
                    try {
                        JSONObject json = new JSONObject(result);
                        int status = json.getInt("status");
                        if (200 == status){
                            ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_setting_ok));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {}

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

    /**
     * 显示圆形覆盖物
     * @param latLng    经纬度点
     * @param radius    半径
     */
    private void showCircleOverlay(LatLng latLng,int radius){
        overlay = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(0xAA446EF6);
        mBaiduMap.addOverlay(overlay);

        ///计算圆形标注的东北和西南的坐标，以便完全显示在地图上
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        double topLat = latitude + radius/111000.0;
        double bottomLat = latitude - radius/111000.0;
        double leftLon = longitude + radius/111000.0;
        double rightLon = longitude - radius/111000.0;

        LatLng northeast = new LatLng(topLat,leftLon); //东北
        LatLng southwest = new LatLng(bottomLat,rightLon); //西南
        LatLngBounds lngBounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .include(new LatLng(latitude,longitude)).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(lngBounds);
        mBaiduMap.setMapStatus(u);
    }

    /**
     *获取设备ID的电子围栏信息
     * @param devid 设备ID
     */
    private void getFenceMessage(String devid){
        RequestParams params = new RequestParams(Urls.GETTING_FENCE_MESSAGE);
        params.addBodyParameter("devid", devid);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("获取设备围栏信息---" + result);
                if (!TextUtils.isEmpty(result)){
                    try {
                        JSONObject json = new JSONObject(result);
                        int status = json.getInt("status");
                        if (200 == status && json.get("fence") instanceof JSONObject){

                            JSONObject fence = json.getJSONObject("fence");
                            double latTemp = fence.getDouble("lat");
                            double lonTemp = fence.getDouble("lon");
                            int radius = fence.getInt("radius");
                            showCircleOverlay(new LatLng(latTemp,lonTemp),radius);

                        }else {
                            ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_no_fence));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("获取设备围栏信息解析错误---" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });

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
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    private void RxGetLocationInfo(){

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
        final Map<String, String> options = new HashMap<>();
        options.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        options.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        options.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        options.put(SignatureUtils.SIGNATURE_STRING, signature);

        //配置请求体
        DeviceStateBody deviceStateBody = new DeviceStateBody();
        deviceStateBody.devid = devid;
        deviceStateBody.token = token;
        String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
        final RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        mSubscription = Observable.interval(0,1, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<LocationResponse>>() {
                    @Override
                    public Observable<LocationResponse> call(Long aLong) {
                        return ZivApiManage.getInstance().getZivApiService().getLocationInfo(options,jsonBody);
                    }
                })
                .takeUntil(new Func1<LocationResponse, Boolean>() {
                    @Override
                    public Boolean call(LocationResponse locationResponse) {
                        int status = locationResponse.getStatus();
                        if (200 == status){
                            LocationResponse.GpsBean gpsBean = locationResponse.getGps();
                            if (gpsBean != null && gpsBean.getLat() != 0){
                                return true;
                            }
                        }
                        return false;
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
                        ToastShow.showToast(ElectronicFence.this,getString(R.string.fence_no_location_data));
                    }
                });
    }

    /**
     * 处理位置返回信息
     * @param locationResponse  位置返回信息
     */
    private void handLocationInfo(LocationResponse locationResponse){
        int status = locationResponse.getStatus();
        if (200 == status){
            LocationResponse.GpsBean gpsBean = locationResponse.getGps();
            if (gpsBean != null){
                lat = gpsBean.getLat();
                lon = gpsBean.getLon();
                initMap(lat,lon);
            }
        }
    }
}
