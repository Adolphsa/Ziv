package com.zividig.ziv.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
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
import java.util.HashMap;
import java.util.List;
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

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;
import static com.zividig.ziv.utils.SignatureUtils.token;

/**
 * 我的车
 */
public class MyCarFragment extends Fragment {

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

    private long secondTime = 0;

    private Subscription mSubscription;
    private int retryCount = 0;

    public static MyCarFragment instance() {
        MyCarFragment myCarView = new MyCarFragment();
        return myCarView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mycar, null);
        mSpf = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        mMainActivity = (MainActivity) getActivity();
        mSpf.edit().putBoolean("is_keeping_get_device_state", true).apply();

        initView();
        initAd();
        initFunctionButton();

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
    public void setTitle() {
        System.out.println("设置车的标题");
        String devid = mSpf.getString("devid", "");
        if (!devid.equals("")) {
            String carid = getCurrentCarid(devid);
            if (!carid.equals("")) {
                mTitle.setText(carid);
            } else {
                String tmp = devid.substring(devid.length() - 4, devid.length());
                String tmp2 = "设备ID : *" + tmp;
                mTitle.setText(tmp2);
            }
        } else {
            mTitle.setText("我的车");
        }
    }

    //得到与设备ID相匹配的车牌号
    private String getCurrentCarid(String devid) {

        String deviceInfo = mSpf.getString("device_info", "");
        if (!deviceInfo.equals("")) {
            Gson gson = new Gson();
            DeviceInfoBean deviceInfoBean = gson.fromJson(deviceInfo, DeviceInfoBean.class);
            devinfoList = deviceInfoBean.getDevinfo();

            String tmp = "";
            String carid = "";
            for (DeviceInfoBean.DevinfoBean devinfoBean : devinfoList) {
                tmp = devinfoBean.getDevid();
                if (devid.equals(tmp)) {
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
                            startActivity(new Intent(getContext(), RealTimeShow.class));
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 1:
                        System.out.println("实时视频" + position);
                        //判断是否是设备WIFI
                        if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("ziv_")) {
                            WifiDirectUtils.WifiDirect(getContext(), MyTestActivity.class);
                        } else {
                            if (!devId.equals("")) {
                                if ((System.currentTimeMillis() - secondTime) > (3 * 1000)) {
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
                        startActivity(new Intent(getContext(), CarInfo.class));
                        break;
                    case 3:
                        System.out.println("车辆定位" + position);
                        if (!devId.equals("")) {
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")) {
                                startActivity(new Intent(getContext(), CarLocation.class));
                            } else {
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
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            } else {
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            }
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 5:
                        System.out.println("违章查询" + position);
                        startActivity(new Intent(getContext(), ViolationActivity.class));
                        break;
                    case 6:
                        System.out.println("轨迹查询" + position);
                        if (!devId.equals("")) {
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
        getDevID();
        System.out.println("MyCarFragment的Resume");
        boolean isKeepingGetDeviceState = mSpf.getBoolean("is_keeping_get_device_state", true);
        System.out.println("是否继续获取设备状态---" + isKeepingGetDeviceState);

        if (isKeepingGetDeviceState) {
            mSpf.edit().putBoolean("is_keeping_get_device_state", false).apply();
            RxGetDeviceState();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
        System.out.println("MyFragment--- onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("MyFragment--- onStop");
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        countDownTimer.cancel();
        System.out.println("MyFragment--- onDestroy");


    }

    private String getDevID() {
        devId = mSpf.getString("devid", "");
        System.out.println("MyCarFragment---deviceId:" + devId);
        return devId;
    }

    /**
     * 获取
     */
    private void getDeviceState(final int postion) {

        final String devid = mSpf.getString("devid", "");

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid", devid);
            json.put(SIGNATURE_TOKEN, token);

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
                token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_STATE, timestamp, noncestr, signature);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("结果" + result);
                DeviceStateInfoBean stateInfoBean = JsonUtils.deserialize(result, DeviceStateInfoBean.class);
                int status = stateInfoBean.getStatus();
                if (200 == status) {
                    DeviceStateInfoBean.InfoBean infoBean = stateInfoBean.getInfo();
                    String workMode = infoBean.getWorkmode();
                    if (workMode.equals("NORMAL")) {
                        if (1 == postion) {
                            startVideo(devid);
                        }
                    } else {
                        ToastShow.showToast(getContext(), "主机不在线");
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
            json.put("devid", deviceId);
            json.put(SIGNATURE_TOKEN, token);

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
                token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.REQUEST_VIDEO, timestamp, noncestr, signature);
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


    /**
     * 配置options
     * @return options
     */
    private Map<String, String> setOp(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                devid,
                token);

        //配置请求头
        Map<String, String> options = new HashMap<>();
        options.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        options.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        options.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        options.put(SignatureUtils.SIGNATURE_STRING, signature);

        return options;
    }

    /**
     * 配置jsonBody
     * @return  RequestBody
     */
    private RequestBody setBody(){

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        //配置请求体
        //配置请求体
        DeviceStateBody deviceStateBody = new DeviceStateBody();
        deviceStateBody.devid = devid;
        deviceStateBody.token = token;
        String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        return jsonBody;
    }

    /**
     * 轮询获取设备状态
     */
    public void RxGetDeviceState() {

        mSubscription = Observable.interval(0,30, TimeUnit.SECONDS)
                .flatMap(new Func1<Long, Observable<DeviceStateResponse>>() {
                    @Override
                    public Observable<DeviceStateResponse> call(Long aLong) {
                        Map<String, String> options = setOp();
                        RequestBody jsonBody = setBody();
                        return ZivApiManage.getInstance().getZivApiService().getDeviceStateInfo(options, jsonBody);
                    }
                })
                .takeUntil(new Func1<DeviceStateResponse, Boolean>() {
                    @Override
                    public Boolean call(DeviceStateResponse deviceStateResponse) {
                        int currentPage = mMainActivity.getCurrentPage();
                        if (0 != currentPage) {
                            mSubscription.unsubscribe();
                            return true;
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceStateResponse>() {
                    @Override
                    public void call(DeviceStateResponse deviceStateResponse) {
                        System.out.println("RXJAVA---设备状态---" + deviceStateResponse.getInfo().getWorkmode());
                        handDevideStateResponse(deviceStateResponse);
                        setTitle();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("RXJAVA---设备状态出错---" + throwable.getMessage());
                        if (++retryCount >= 3){
                            System.out.println("查询失败");
                            deviceState.setText("查询失败");
                            retryCount = 0;
                            if (mSubscription != null){
                                mSubscription.unsubscribe();
                            }
                        }else {
                            System.out.println("RXJAVA---retryCount---" + retryCount);
                            RxGetDeviceState();
                        }

                    }
                });
    }

    /**
     * 处理设备状态的返回
     * @param deviceStateResponse   设备状态的返回
     */
    private void handDevideStateResponse(DeviceStateResponse deviceStateResponse){
        int status = deviceStateResponse.getStatus();
        if (200 == status){
            DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
            if (infoBean != null){
                String workMode = infoBean.getWorkmode();
                if (workMode.equals("NORMAL")) {
                    deviceState.setText("在线");
                } else if (workMode.equals("STDBY")) {
                    deviceState.setText("休眠");
                } else if (workMode.equals("OFF")) {
                    deviceState.setText("离线");
                } else if (workMode.equals("UNKNOWN")) {
                    deviceState.setText("未知");
                } else if (workMode.equals("BOOTING")) {
                    deviceState.setText("启动中");
                }
            }
        }
    }

}
