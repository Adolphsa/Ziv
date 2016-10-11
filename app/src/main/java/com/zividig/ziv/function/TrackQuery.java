package com.zividig.ziv.function;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.MapTrackBean;
import com.zividig.ziv.main.BaseActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 轨迹查询
 */
public class TrackQuery extends BaseActivity {

    BitmapDescriptor realtimeBitmap = BitmapDescriptorFactory
            .fromResource(R.mipmap.icon_gcoding);

    private static final String MAP_DATA_URL = "http://dev.caowei.name/mytest/uploadtest/localtionhisziv.php";
    private MapTrackBean mapTrackBean;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> overLatLng;
    private double latitude;  // 围栏圆心纬度

    private double longitude;  // 围栏圆心经度
    private OverlayOptions polylineOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_query);

        initView();
        initData();
        getMapData();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("轨迹查询");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        overLatLng = new ArrayList<LatLng>();

        mMapView = (MapView) findViewById(R.id.track_map);
        mBaiduMap = mMapView.getMap();
    }

    private void initData(){
        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        String devid = spf.getString("devid","");
        System.out.println("设备ID：" + devid);
    }

    /**
     * 访问网络并得到轨迹数据
     */
    public void getMapData(){
        RequestParams params = new RequestParams(MAP_DATA_URL);
        params.addBodyParameter("deviceId","1234567890123456788");
        params.addBodyParameter("begin","111");
        params.addBodyParameter("end","999999999999");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                mapTrackBean = gson.fromJson(result,MapTrackBean.class);
                if (mapTrackBean != null){

                    //获取当前的经纬度
                    latitude = mapTrackBean.getLocationdata().get(0).getLat();
                    longitude = mapTrackBean.getLocationdata().get(0).getLon();

                    //显示轨迹
                    showTrackInMap(mapTrackBean.getLocationdata());
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("访问出错");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 在地图上显示轨迹
     * @param locationdata GPS轨迹列表
     */
    private void showTrackInMap(List<MapTrackBean.LocationdataBean> locationdata){

        for (MapTrackBean.LocationdataBean locationBean: locationdata) {
            System.out.println("lat:" + locationBean.getLat() + "--lon:" + locationBean.getLon());
            LatLng latLng = new LatLng(locationBean.getLat(),locationBean.getLon());
            overLatLng.add(latLng);
        }

        System.out.println("标注的长度：" + overLatLng.size());

        //构建用户绘制折线的Option对象
        polylineOption = new PolylineOptions()
                .points(overLatLng)
                .color(Color.BLACK)
                .width(10)
                .visible(true);
        //在地图上添加折线Option，用于显示
        mBaiduMap.addOverlay(polylineOption);

        //标注第一个点的位置
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude,longitude))
                .icon(realtimeBitmap).zIndex(9).draggable(true);
        mBaiduMap.addOverlay(markerOptions);

      //当地图加载完成后，设置地图的地理范围
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //缩放地图到合适的比例
                LatLngBounds lngBounds = new LatLngBounds.Builder()
                        .include(overLatLng.get(0))
                        .include(overLatLng.get(overLatLng.size()-1))
                        .build();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(lngBounds);
                mBaiduMap.setMapStatus(u);
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
    }
}
