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
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
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

    private int[] distanceArr = new int[]{20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000};
    private int[] levelArr = new int[]{21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3};

    BitmapDescriptor mapStart = BitmapDescriptorFactory
            .fromResource(R.mipmap.map_start);
    BitmapDescriptor mapEnd = BitmapDescriptorFactory
            .fromResource(R.mipmap.map_end);
    private MapTrackBean mapTrackBean;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private double latitude;  // 围栏圆心纬度

    private double longitude;  // 围栏圆心经度
    private OverlayOptions polylineOption;
    private Long mStartTime;
    private Long mEndTime;
    private String mDevid;
    private Gson mGson;

    boolean isFenduan = true;

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
        txtTitle.setText(R.string.tq_title);

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMapView = (MapView) findViewById(R.id.track_map);
        mBaiduMap = mMapView.getMap();

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

                            //设定地图的初始中心
                            MapStatus.Builder builder = new MapStatus.Builder();
                            builder.target(GPSConverterUtils.gpsToBaidu(new LatLng(latitude,longitude)))
                                    .zoom(16.0f);
                            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                            //显示轨迹
                            showTrackInMap(mapTrackBean.getLocationdata());
                        }
                    }else {
                        if (!TrackQuery.this.isFinishing()){
                            DialogUtils.showPrompt(TrackQuery.this, getString(R.string.add_device_tips), getString(R.string.tq_no_data), getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
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
                    DialogUtils.showPrompt(TrackQuery.this,getString(R.string.add_device_tips), getString(R.string.tq_data_no), getString(R.string.add_device_ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });
    }

    /**
     * 在地图上显示轨迹
     * @param locationdata GPS轨迹列表
     */
    private void showTrackInMap(List<MapTrackBean.LocationdataBean> locationdata){

        LatLng sourceLatLng;
        LatLng desLatLng;
        boolean isSpilt = true;
        List<LatLng> oneLatLng = new ArrayList<LatLng>();
        List<LatLng> twoLatLng = new ArrayList<LatLng>();

        for (int i = 0; i < locationdata.size()-2; i++) {

                if ((Math.abs(locationdata.get(i).getTi() - locationdata.get(i+1).getTi()) < 600) && isSpilt)
                {
                    if (twoLatLng.size() > 2){
                        System.out.println("绘制---twoLatLng");
                        splitData(twoLatLng,Color.parseColor("#00796B"));
                        twoLatLng.clear();
                    }
                    sourceLatLng = new LatLng(locationdata.get(i).getLat(),locationdata.get(i).getLon());
                    System.out.println("");
                    //坐标转换
                    desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
                    oneLatLng.add(desLatLng);

                }else {

                    if (oneLatLng.size() > 2){
                        sourceLatLng = new LatLng(locationdata.get(i).getLat(),locationdata.get(i).getLon());
                        //坐标转换
                        desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
                        oneLatLng.add(desLatLng);
                        splitData(oneLatLng,Color.parseColor("#009688"));
                        System.out.println("---绘制oneLatLng");
                        oneLatLng.clear();

                    }
                    if (Math.abs(locationdata.get(i+1).getTi() - locationdata.get(i+2).getTi()) < 600){
                        sourceLatLng = new LatLng(locationdata.get(i+1).getLat(),locationdata.get(i+1).getLon());
                        //坐标转换
                        desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
                        twoLatLng.add(desLatLng);
                        isSpilt = false;
                    }else {

                        sourceLatLng = new LatLng(locationdata.get(i+1).getLat(),locationdata.get(i+1).getLon());
                        //坐标转换
                        desLatLng = GPSConverterUtils.gpsToBaidu(sourceLatLng);
                        twoLatLng.add(desLatLng);
                        isSpilt = true;
                    }

                }
        }
        if (oneLatLng.size() > 2){
            System.out.println("最后一次绘制oneLatLng");
            splitData(oneLatLng,Color.parseColor("#0288D1"));
            oneLatLng.clear();
        }
        if (twoLatLng.size() > 2){
            System.out.println("最后一次绘制twoLatLng");
            splitData(twoLatLng,Color.parseColor("#03A9F4"));
            twoLatLng.clear();
        }
        int tempLength = locationdata.size()/2;
        setLevel(new LatLng(locationdata.get(0).getLat(),locationdata.get(0).getLon()),
                new LatLng(locationdata.get(tempLength).getLat(),locationdata.get(tempLength).getLon()));
    }

    private void splitData(List<LatLng> overLatLng,int color){

        System.out.println("标注的长度：" + overLatLng.size());
        LatLng firstLatLng = overLatLng.get(0);
        LatLng lastLatLng = overLatLng.get(overLatLng.size() - 1);

        //构建用户绘制折线的Option对象
        polylineOption = new PolylineOptions()
                .points(overLatLng)
                .color(color)
                .width(13)
                .visible(true);
        //在地图上添加折线Option，用于显示
        mBaiduMap.addOverlay(polylineOption);

//        //标注第一个点的位置
//        MarkerOptions markerStart = new MarkerOptions()
//                .position(firstLatLng)
//                .icon(mapStart).zIndex(9).draggable(true);
//        mBaiduMap.addOverlay(markerStart);
//
//        //标注最后一个点的位置
//        MarkerOptions markerEnd = new MarkerOptions()
//                .position(lastLatLng)
//                .icon(mapEnd).zIndex(9).draggable(true);
//        mBaiduMap.addOverlay(markerEnd);
    }

    //设置地图缩放级别
    private void setLevel(LatLng llStart,LatLng llEnd) {
        //起点： latitude纬度           longitude经度
        if (llStart != null ) {
            int distance = (int) DistanceUtil.getDistance(llStart, llEnd);
            System.out.println("距离约" + distance + "米");
            int level = getLevel(distance);
            //设置缩放级别
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(levelArr[level]).build()));
        }
    }

    //根据距离计算出差值数组,并排序（取正数）
    private int getLevel(int distance) {
        int level = -1;
        int min = 10000000;
        for (int i = 0; i < distanceArr.length; i++) {
            if (distanceArr[i] - distance > 0 && distanceArr[i] - distance < min) {
                min = distanceArr[i] - distance;
                level = i;
            }
        }
        return level;
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
