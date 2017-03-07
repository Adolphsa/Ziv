package com.zividig.ziv.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.google.gson.Gson;
import com.zivdigi.helloffmpeg.MyTestActivity;
import com.zivdigi.helloffmpeg.TestDecoder;
import com.zividig.ndk_test.weizhang.activity.ViolationActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.bean.DeviceStateInfoBean;
import com.zividig.ziv.bean.VideoInfoBean;
import com.zividig.ziv.function.AddDevice;
import com.zividig.ziv.function.CarInfo;
import com.zividig.ziv.function.CarLocation;
import com.zividig.ziv.function.ElectronicFence;
import com.zividig.ziv.function.RealTimeShow;
import com.zividig.ziv.function.TrackQueryDateChoose;
import com.zividig.ziv.main.MainActivity;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.service.DeviceStateService;
import com.zividig.ziv.service.LocationService;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.MyAlarmManager;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;
import com.zividig.ziv.utils.WifiDirectUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

/**
 * 我的车
 */
public class MyCarFragment extends Fragment {

    private static final int DEVICE_STATE_BOOTING = 90;
    private static final int DEVICE_STATE_NORMAL = 100;
    private static final int DEVICE_STATE_STDBY = 101;
    private static final int DEVICE_STATE_OFF = 102;
    private static final int DEVICE_STATE_UNKNOWN = 103;
    private static final int DEVICE_STATE_DEFAULT = 104;
    private static final int ID_IS_NULL = 105;

    private static final int TITLE_CAR_ID = 106;
    private static final int TITLE_DEVICE_ID = 107;
    private static final int TITLE_DEFAULT = 108;

    private View view; //布局文件

    private ConvenientBanner convenientBanner; //顶部广告栏控件
    private ArrayList<Integer> localImages = new ArrayList<Integer>();

    private String[] itemTexts = {"图片抓拍",
            "实时视频",
            "车辆信息",
            "车辆定位",
            "电子围栏",
            "违章查询",
            "轨迹查询"};
    private int[] itemImages = {R.drawable.selector_real_time,
            R.drawable.select_real_video,
            R.drawable.selector_car_info,
            R.drawable.selector_car_location,
            R.drawable.selector_electric_fence,
            R.drawable.selector_car_manage,
            R.drawable.selector_history_back};

    private String devId;

    private List<DeviceInfoBean.DevinfoBean> devinfoList;
    private TextView tvDevidState;
    private TextView deviceState;
    private SharedPreferences mSpf;
    private TextView mTitle;

    private MainActivity mMainActivity;

    private String titleCarid;
    private String titleDeviceId;

    private static Timer mTimer;

    private long secondTime = 0;
    private int loopTime = 5;

    public static MyCarFragment instance() {
        MyCarFragment myCarView = new MyCarFragment();
        return myCarView;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DEVICE_STATE_NORMAL:
                    deviceState.setText("在线");
                    break;
                case DEVICE_STATE_STDBY:
                    deviceState.setText("休眠");
                    break;
                case DEVICE_STATE_BOOTING:
                    deviceState.setText("启动中");
                    break;
                case DEVICE_STATE_OFF:
                    deviceState.setText("离线");
                    break;
                case DEVICE_STATE_UNKNOWN:
                    deviceState.setText("未知");
                    break;
                case DEVICE_STATE_DEFAULT:
                    deviceState.setText("正在查询");
                    break;
                case ID_IS_NULL:
                    deviceState.setText("ID为空");
                    break;
                case TITLE_CAR_ID:
                    mTitle.setText(titleCarid);
                    break;
                case TITLE_DEVICE_ID:
                    mTitle.setText(titleDeviceId);
                    break;
                case TITLE_DEFAULT:
                    mTitle.setText("我的车");
                    break;
            }
        }
    };

    //定时器开始轮询设备状态
    public void startTimer(){

        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("开始定时器");
                loopGetDeviceState();
            }
        },0,loopTime*1000);
    }

    public void stopTimer(){
        if (mTimer != null){
            mTimer.cancel();
        }

    }

    /**
     * 轮询获取设备状态
     */
    private void loopGetDeviceState(){
        String devid = mSpf.getString("devid", "");
        System.out.println("token---" + SignatureUtils.token);
        if (devid.equals("")){
            mHandler.sendEmptyMessage(ID_IS_NULL);
        }else {
            //配置json数据
            JSONObject json = new JSONObject();
            try {
                json.put("devid",devid);
                json.put(SignatureUtils.SIGNATURE_TOKEN, SignatureUtils.token);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //计算signature
            String timestamp = UtcTimeUtils.getTimestamp();
            String noncestr = HttpParamsUtils.getRandomString(10);
            String signature = SignatureUtils.getSinnature(timestamp,
                    noncestr,
                    Urls.APP_KEY,
                    devid,
                    SignatureUtils.token);
            //发起请求
            RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_STATE,timestamp,noncestr,signature);
            params.setBodyContent(json.toString());

//            System.out.println("获取设备状态---" + params.toString());
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {

                    DeviceStateInfoBean stateInfoBean = JsonUtils.deserialize(result, DeviceStateInfoBean.class);
                    int status = stateInfoBean.getStatus();
                    System.out.println("获取设备状态结果---" + result);
                    if (200 == status){
                        DeviceStateInfoBean.InfoBean infoBean = stateInfoBean.getInfo();
                        String workMode = infoBean.getWorkmode();
                        if (workMode.equals("NORMAL")) {
                            mHandler.sendEmptyMessage(DEVICE_STATE_NORMAL);
                            loopTime = 30;
                        } else if (workMode.equals("STDBY")) {
                            mHandler.sendEmptyMessage(DEVICE_STATE_STDBY);
                        } else if (workMode.equals("OFF")) {
                            loopTime = 2;
                            mHandler.sendEmptyMessage(DEVICE_STATE_OFF);
                        } else if (workMode.equals("UNKNOWN")) {
                            mHandler.sendEmptyMessage(DEVICE_STATE_UNKNOWN);
                        }else if (workMode.equals("BOOTING")){
                            mHandler.sendEmptyMessage(DEVICE_STATE_BOOTING);
                        }
                    }else {
                        System.out.println("解析失败");
                        mHandler.sendEmptyMessage(DEVICE_STATE_DEFAULT);
                    }


                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    mHandler.sendEmptyMessage(DEVICE_STATE_DEFAULT);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
        setTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mycar, null);
        mSpf = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mMainActivity = (MainActivity) getActivity();
        mSpf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        initView();
        initAd();
        initFunctionButton();

//        startTimer();
        return view;
    }

    private void initView() {

        tvDevidState = (TextView) view.findViewById(R.id.tv_device_states);
        deviceState = (TextView) view.findViewById(R.id.tv_device_state);

        tvDevidState.setVisibility(View.VISIBLE);
        deviceState.setVisibility(View.VISIBLE); //设备状态

        //设置标题
        mTitle = (TextView) view.findViewById(R.id.tv_title);
        setTitle();

        //添加设备按钮
        Button addDevice = (Button) view.findViewById(R.id.bt_add_device);
        addDevice.setVisibility(View.VISIBLE);
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddDevice.class));
            }
        });

    }

    //设置我的车的标题
    public void setTitle(){
        System.out.println("设置车的标题");
        String devid = mSpf.getString("devid","");
        if (!devid.equals("")){
            String carid = getCurrentCarid(devid);
            if (!carid.equals("")){
                titleCarid = carid;
                mHandler.sendEmptyMessage(TITLE_CAR_ID);
            }else {
                String tmp = devid.substring(devid.length()-4,devid.length());
                String tmp2 = "设备ID : *" + tmp;
                titleDeviceId = tmp2;
                mHandler.sendEmptyMessage(TITLE_DEVICE_ID);
            }
        }else {
            mHandler.sendEmptyMessage(TITLE_DEFAULT);
        }
    }

    //得到与设备ID相匹配的车牌号
    private String getCurrentCarid(String devid){

        String deviceInfo = mSpf.getString("device_info","");
        if (!deviceInfo.equals("")) {
            Gson gson = new Gson();
            DeviceInfoBean deviceInfoBean = gson.fromJson(deviceInfo, DeviceInfoBean.class);
            devinfoList = deviceInfoBean.getDevinfo();

            String tmp = "";
            String carid = "";
            for (DeviceInfoBean.DevinfoBean devinfoBean: devinfoList){
                tmp = devinfoBean.getDevid();
                if (devid.equals(tmp)){
                    carid = devinfoBean.getCarid();
                    break;
                }
            }

            return carid;
        }
        return "";
    }

    //初始化功能按钮
    private void initFunctionButton() {
        GridView gridView = (GridView) view.findViewById(R.id.gv_mycar);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return itemTexts.length;
            }

            @Override
            public Object getItem(int position) {
                return itemTexts[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = View.inflate(getContext(), R.layout.layout_function_button, null);
                ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
                TextView tvItem = (TextView) view.findViewById(R.id.tv_item);

                ivItem.setImageResource(itemImages[position]);
                tvItem.setText(itemTexts[position]);
                return view;
            }
        });

        //先判断是否能够获取到设备ID，然后再点击
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDevID();
                switch (position) {
                    case 0:
                        System.out.println("图片抓拍" + position);
                        if (!devId.equals("")) {
                            stopTimer();//取消设备状态轮询
                            getDeviceState(position);
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 1:
                        System.out.println("实时视频" + position);
                        //判断是否是设备WIFI
                        if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")) {
                            WifiDirectUtils.WifiDirect(getContext(), MyTestActivity.class);
                        } else {
                            if (!devId.equals("")) {
                                if ((System.currentTimeMillis()- secondTime) > (3 * 1000)){
                                    System.out.println("大于3秒");
                                    getDeviceState(position);
                                }
                                secondTime = System.currentTimeMillis();
                            } else {
                                ToastShow.showToast(getContext(), "请先添加设备");
                            }

                        }
                        break;
                    case 2:
                        System.out.println("车辆信息" + position);
                        stopTimer();//取消设备状态轮询
                        //开启轮询服务获取设备状态
                        MyAlarmManager.startPollingService(getContext(), 10, DeviceStateService.class, "");
                        startActivity(new Intent(getContext(), CarInfo.class));
                        break;
                    case 3:
                        System.out.println("车辆定位" + position);
                        if (!devId.equals("")) {
                            //开启轮询服务获取GPS信息
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")) {
                                stopTimer();//取消设备状态轮询
                                startActivity(new Intent(getContext(), CarLocation.class));
                            } else {
                                MyAlarmManager.startPollingService(getContext(), 1, LocationService.class, devId);
                                stopTimer();//取消设备状态轮询
                                startActivity(new Intent(getContext(), CarLocation.class));
                            }

                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 4:
                        System.out.println("电子围栏" + position);
                        if (!devId.equals("")) {
                            //开启轮询服务获取GPS信息
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")) {
                                stopTimer();//取消设备状态轮询
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            } else {
                                MyAlarmManager.startPollingService(getContext(), 1, LocationService.class, devId);
                                stopTimer();//取消设备状态轮询
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            }
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 5:
                        System.out.println("违章查询" + position);
                        stopTimer();//取消设备状态轮询
                        startActivity(new Intent(getContext(), ViolationActivity.class));
                        break;
                    case 6:
                        System.out.println("轨迹查询" + position);
                        if (!devId.equals("")) {
                            stopTimer();//取消设备状态轮询
                            startActivity(new Intent(getContext(), TrackQueryDateChoose.class));
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                }
            }
        });
    }

    //初始化广告控件
    private void initAd() {
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        localImages = new ArrayList<>();
        localImages.add(R.mipmap.ad1);
        localImages.add(R.mipmap.ad2);
        localImages.add(R.mipmap.ad3);

        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public LocalImageHolderView createHolder() {
                return new LocalImageHolderView();
            }
        }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
    }

    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Integer data) {
            imageView.setImageResource(data);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(3000);
        loopGetDeviceState();
        getDevID();
        System.out.println("MyCarFragment的Resume");
        boolean isKeepingGetDeviceState = mSpf.getBoolean("is_keeping_get_device_state",true);
        System.out.println("是否继续获取设备状态---" + isKeepingGetDeviceState);

        if (isKeepingGetDeviceState){
//            mSpf.edit().putBoolean("is_keeping_get_device_state",false).apply();
            startTimer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        System.out.println("我的车可见");
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
        stopTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        countDownTimer.cancel();
        stopTimer();
    }



    private String getDevID() {
        devId = mSpf.getString("devid", "");
        System.out.println("MyCarFragment---deviceId:" + devId);
        return devId;
    }

    /**
     * 获取
     */
    private void getDeviceState(final int postion){

        final String devid = mSpf.getString("devid", "");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid",devid);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                SignatureUtils.token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_STATE,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("结果" + result);
                DeviceStateInfoBean stateInfoBean = JsonUtils.deserialize(result, DeviceStateInfoBean.class);
                int status = stateInfoBean.getStatus();
                if (200 == status){
                    DeviceStateInfoBean.InfoBean infoBean = stateInfoBean.getInfo();
                    String workMode = infoBean.getWorkmode();
                    if (workMode.equals("NORMAL")){
                        if (0 == postion){
                            startActivity(new Intent(getContext(), RealTimeShow.class));
                        }else if (1 == postion){
                            startVideo(devid);
                        }

                    }else {
                        ToastShow.showToast(getContext(),"主机不在线");
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
     * 开启实时视频
     *
     * @param
     */
    public void startVideo(String deviceId) {
        System.out.println("点击了开启实时视频1");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid",deviceId);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                deviceId,
                SignatureUtils.token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.REQUEST_VIDEO,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);

                VideoInfoBean videoInfoBean = JsonUtils.deserialize(result, VideoInfoBean.class);
                int code = videoInfoBean.getStatus();
                if (code == 200) {
                    System.out.println("视频URL:" + videoInfoBean.getUrl());
                    TestDecoder.setUrl(videoInfoBean.getUrl());
                    stopTimer();//取消设备状态轮询
                    getActivity().startActivity(new Intent(getContext(), MyTestActivity.class));
                } else {
                    System.out.println("非200");
                    ToastShow.showToast(getContext(), "连接异常，请检测设备状态");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("访问错误" + ex);
                ToastShow.showToast(getContext(), "视频访问错误");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
