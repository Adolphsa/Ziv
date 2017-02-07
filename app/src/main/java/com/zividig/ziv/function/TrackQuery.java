package com.zividig.ziv.function;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
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
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.GPSConverterUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;


/**
 * 轨迹查询
 */
public class TrackQuery extends BaseActivity {

    BitmapDescriptor mapStart = BitmapDescriptorFactory
            .fromResource(R.mipmap.map_start);
    BitmapDescriptor mapEnd = BitmapDescriptorFactory
            .fromResource(R.mipmap.map_end);
    private MapTrackBean mapTrackBean;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private List<LatLng> overLatLng;
    private double latitude;  // 围栏圆心纬度

    private double longitude;  // 围栏圆心经度
    private OverlayOptions polylineOption;
    private Long mStartTime;
    private Long mEndTime;
    private String mDevid;
    private Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_query);

        mGson = new Gson();
        Bundle bundle = getIntent().getExtras();
        mStartTime = bundle.getLong("start_time");
        mEndTime = bundle.getLong("end_time");
        System.out.println("开始查询时间---" + mStartTime  + "结束查询时间---" + mEndTime);

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

        //设定地图的初始中心
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(GPSConverterUtils.gpsToBaidu(new LatLng(22.549467,113.920565)))
                .zoom(16.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    private void initData(){
        SharedPreferences spf = getSharedPreferences("config",MODE_PRIVATE);
        mDevid = spf.getString("devid","");
        System.out.println("设备ID：" + mDevid);
    }

    /**
     * 访问网络并得到轨迹数据
     */
    public void getMapData(){

        //配置JSON
        JSONObject json = new JSONObject();
        try {
            json.put("devid", mDevid);
            json.put("begintime",mStartTime);
            json.put("endtime",mEndTime);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(
                timestamp,
                noncestr,
                Urls.APP_KEY,
                mDevid,
                Long.toString(mStartTime),
                Long.toString(mEndTime),
                SignatureUtils.token);

        //请求参数相关
        RequestParams params = HttpParamsUtils.setParams(Urls.MAP_DATA_URL,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
        System.out.println("轨迹数据URL---" + params);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("轨迹结果：" + result);

                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    int itemNum = json.getInt("itemnum");
                    if (status == Urls.STATUS_CODE_200 && itemNum > 0){
                        mapTrackBean = mGson.fromJson(result,MapTrackBean.class);
                        if (mapTrackBean != null && mapTrackBean.getItemnum() > 0){

                            //获取当前的经纬度
                            latitude = mapTrackBean.getLocationdata().get(0).getLat();
                            longitude = mapTrackBean.getLocationdata().get(0).getLon();
                            System.out.println("经度" + latitude + "w纬度" + longitude);
                            //显示轨迹
                            showTrackInMap(mapTrackBean.getLocationdata());
                        }
                    }else {
                        if (!TrackQuery.this.isFinishing()){
                            DialogUtils.showPrompt(TrackQuery.this, "提示", "当前查询无数据", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("访问出错" + ex);
                if (!TrackQuery.this.isFinishing()){
                    DialogUtils.showPrompt(TrackQuery.this, "提示", "无数据", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }

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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (MapTrackBean.LocationdataBean locationBean: locationdata) {
//            System.out.println("lat:" + locationBean.getLat() + "--lon:" + locationBean.getLon());
            LatLng sourceLatLng = new LatLng(locationBean.getLat(),locationBean.getLon());
            //坐标转换
            LatLng desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
            builder.include(desLatLng);
            overLatLng.add(desLatLng);
        }

        System.out.println("标注的长度：" + overLatLng.size());
        LatLng firstLatLng = GPSConverterUtils.gpsToBaidu(new LatLng(latitude,longitude));
        LatLng lastLatLng = overLatLng.get(overLatLng.size() - 1);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(firstLatLng);
        mBaiduMap.animateMapStatus(u);

        //构建用户绘制折线的Option对象
        polylineOption = new PolylineOptions()
                .points(overLatLng)
                .color(Color.BLUE)
                .width(10)
                .visible(true);
        //在地图上添加折线Option，用于显示
        mBaiduMap.addOverlay(polylineOption);

        //标注第一个点的位置
        MarkerOptions markerStart = new MarkerOptions()
                .position(firstLatLng)
                .icon(mapStart).zIndex(9).draggable(true);
        mBaiduMap.addOverlay(markerStart);

        //标注最后一个点的位置
        MarkerOptions markerEnd = new MarkerOptions()
                .position(lastLatLng)
                .icon(mapEnd).zIndex(9).draggable(true);
        mBaiduMap.addOverlay(markerEnd);

        //缩放地图到合适的比例
        LatLngBounds lngBounds = builder.build();
        System.out.println("缩放地图");
        u = MapStatusUpdateFactory.newLatLngBounds(lngBounds,mMapView.getWidth(),mMapView.getHeight());
        mBaiduMap.animateMapStatus(u);



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
