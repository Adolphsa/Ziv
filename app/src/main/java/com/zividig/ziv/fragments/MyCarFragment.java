package com.zividig.ziv.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.zivdigi.helloffmpeg.twog.SocketTest;
import com.zividig.ndk_test.weizhang.activity.ViolationActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.function.AddDevice;
import com.zividig.ziv.function.CarInfo;
import com.zividig.ziv.function.CarLocation;
import com.zividig.ziv.function.CxllActivity;
import com.zividig.ziv.function.ElectronicFence;
import com.zividig.ziv.function.RealTimeShow;
import com.zividig.ziv.function.TrackQueryDateChoose;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.rxjava.model.VideoResponse;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

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
            "轨迹查询",
            "服务充值"};

    private int[] itemImages = {R.drawable.selector_real_time,
            R.drawable.select_real_video,
            R.drawable.selector_car_info,
            R.drawable.selector_car_location,
            R.drawable.selector_electric_fence,
            R.drawable.selector_car_manage,
            R.drawable.selector_history_back,
            R.drawable.selector_liuliang_chaxun};

    private String devId;
    private String deviceType;

    private List<DeviceInfoBean.DevinfoBean> devinfoList;
    private TextView tvDevidState;
    private TextView deviceState;
    private SharedPreferences mSpf;
    private TextView mTitle;

    private long secondTime = 0;

    private Subscription mSubscription;
    private int retryCount = 0;
    private Dialog dialog2;

    public static MyCarFragment instance() {
        MyCarFragment myCarView = new MyCarFragment();
        return myCarView;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mycar, null);
        mSpf = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);

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
        String devid = mSpf.getString("devid", null);
        if (!TextUtils.isEmpty(devid)) {
            String carid = getCurrentCarid(devid);
            if (!TextUtils.isEmpty(carid)) {
                mTitle.setText(carid);
            } else {
                String tmp = devid.substring(devid.length() - 4, devid.length());
                String tmp2 = "设备ID : *" + tmp;
                mTitle.setText(tmp2);
            }
        } else {
            mTitle.setText(R.string.mcf_my_car);
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
                if (TextUtils.isEmpty(devId)) {
                    ToastShow.showToast(getContext(), getString(R.string.mcf_add_device));
                    return;
                }
                switch (position) {
                    case 0:
                        System.out.println("图片抓拍" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;  //检查网络类型
                        getDeviceStatus(position);
                        break;
                    case 1:
                        System.out.println("实时视频" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        String devid = mSpf.getString("devid", null);
                        String tmp = "";
                        if (!TextUtils.isEmpty(devid)) {
                            tmp = devid.substring(devid.length() - 4, devid.length());
                        }
                        //判断是否是设备WIFI
                        if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("ziv_box_" + tmp)) {
                            startActivity(new Intent(getContext(), SocketTest.class));
                        } else {

                            if ((System.currentTimeMillis() - secondTime) > (2 * 1000)) {
                                System.out.println("大于2秒");
                                secondTime = System.currentTimeMillis();
                                if (NetworkTypeUtils.is2GDevice(deviceType)) {
                                    showChangeWifiDialog();
                                } else {
                                    RxStartVideo();
                                }

                            } else {
                                ToastShow.showToast(getContext(), getString(R.string.repeat_info));
                            }
                        }
                        break;
                    case 2:
                        System.out.println("车辆信息" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), CarInfo.class));
                        break;
                    case 3:
                        System.out.println("车辆定位" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), CarLocation.class));
                        break;
                    case 4:
                        System.out.println("电子围栏" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), ElectronicFence.class));
                        break;
                    case 5:
                        System.out.println("违章查询" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), ViolationActivity.class));
                        break;
                    case 6:
                        System.out.println("轨迹查询" + position);
                        if (serviceTrueOrFalse()) return; //检查服务是否到期
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), TrackQueryDateChoose.class));
                        break;
                    case 7:
                        if (chageNetWork()) break;
                        startActivity(new Intent(getContext(), CxllActivity.class));
                        break;
                }
            }
        });
    }

    //检查服务是否到期
    private boolean serviceTrueOrFalse(){

        if (!Urls.deviceServeice ){
            DialogUtils.showPrompt(getContext(), "提示",
                    "服务已到期，请续费",
                    "确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(getContext(),CxllActivity.class));
                        }
                    });
            return true;

        }
        return false;
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
        convenientBanner.startTurning(5000);
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
        stopLoop();
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
     * 配置options
     *
     * @return options
     */
    private Map<String, String> setOp() {

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        if (token != null && devid != null) {

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
        return null;
    }

    /**
     * 配置jsonBody
     *
     * @return RequestBody
     */
    private RequestBody setBody() {

        String token = mSpf.getString("token", null);
        String devid = mSpf.getString("devid", null);

        if (token != null && devid != null) {
            //配置请求体
            DeviceStateBody deviceStateBody = new DeviceStateBody();
            deviceStateBody.devid = devid;
            deviceStateBody.token = token;
            String stringDeviceListBody = JsonUtils.serialize(deviceStateBody);
            RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);
            return jsonBody;
        }
        return null;
    }

    /**
     * 在图片抓拍和实时视频之前获取设备状态
     */
    private void getDeviceStatus(final int position) {

        Map<String, String> options = setOp();
        RequestBody jsonBody = setBody();

        ZivApiManage.getInstance().getZivApiService()
                .getDeviceStateInfo(options, jsonBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceStateResponse>() {
                    @Override
                    public void call(DeviceStateResponse deviceStateResponse) {
                        int status = deviceStateResponse.getStatus();
                        System.out.println("抓图状态---" + status);
                        if (status == 200) {
                            if (deviceStateResponse.getInfo() != null) {
                                String deviceStatus = deviceStateResponse.getInfo().getWorkmode();
                                String deviceType = deviceStateResponse.getInfo().getType();
                                boolean isService = deviceStateResponse.getInfo().isService();
                                System.out.println("设备状态---" + deviceStatus);
                                System.out.println("设备类型---" + deviceType);
                                System.out.println("是否在服务器---" + isService);
                                if (deviceStatus.equals("NORMAL")) {
                                    if (position == 0) {
                                            Intent intent = new Intent(getContext(), RealTimeShow.class);
                                            intent.putExtra("device_type",deviceType);
                                            startActivity(intent);
                                    } else if (position == 1) {
                                        RxStartVideo();
                                    }
                                } else if (deviceStatus.equals("STDBY")) {
                                    deviceState.setText(R.string.mcf_stdby);
                                    ToastShow.showToast(getContext(), getString(R.string.mcf_device_off));
                                } else if (deviceStatus.equals("OFF")) {
                                    deviceState.setText("离线");
                                    ToastShow.showToast(getContext(), getString(R.string.mcf_device_off));
                                } else {
                                    ToastShow.showToast(getContext(), getString(R.string.mcf_device_off));
                                }
                            }
                        } else if (status == 403) {
                            ToastShow.showToast(getContext(), getString(R.string.mcf_token_error));
                        } else if (status == 404) {
                            ToastShow.showToast(getContext(), getString(R.string.mcf_device_no_exist));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        String error = throwable.getMessage();
                        System.out.println("错误---" + error);
                    }
                });
    }

    /**
     * 开启实时视频
     */
    private void RxStartVideo() {

        Map<String, String> options = setOp();
        RequestBody jsonBody = setBody();

        ZivApiManage.getInstance().getZivApiService()
                .startVideo(options, jsonBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VideoResponse>() {
                    @Override
                    public void call(VideoResponse videoResponse) {
                        int status = videoResponse.getStatus();
                        if (200 == status) {
                            String url = videoResponse.getUrl();
                            System.out.println("视频URL:" + url);
                            TestDecoder.setUrl(url);
                            getActivity().startActivity(new Intent(getContext(), MyTestActivity.class));
                        } else if (402 == status) {
                            ToastShow.showToast(getContext(), getString(R.string.mcf_device_off));
                        } else if (403 == status) {
                            ToastShow.showToast(getContext(), getString(R.string.mcf_connect_error));
                        } else if (404 == status) {
                            ToastShow.showToast(getContext(), getString(R.string.mcf_device_no_exist));
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        String error = throwable.getMessage();
                        System.out.println("错误---" + error);
                    }
                });
    }


    /**
     * 轮询获取设备状态
     */
    public void RxGetDeviceState() {
        System.out.println("轮询获取涉设备状态");

        if (mSubscription == null) {
            mSubscription = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .flatMap(new Func1<Long, Observable<DeviceStateResponse>>() {
                        @Override
                        public Observable<DeviceStateResponse> call(Long aLong) {
                            Map<String, String> options = setOp();
                            RequestBody jsonBody = setBody();
                            if (options != null && jsonBody != null) {

                                return ZivApiManage.getInstance().getZivApiService().getDeviceStateInfo(options, jsonBody);
                            } else {
                                return Observable.error(new Exception("devid_is_null"));
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<DeviceStateResponse>() {
                        @Override
                        public void call(DeviceStateResponse deviceStateResponse) {
//                            System.out.println("RXJAVA---设备状态---" + deviceStateResponse.getInfo().toString());
                            handDevideStateResponse(deviceStateResponse);
                            setTitle();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            System.out.println("RXJAVA---设备状态出错---" + throwable.getMessage());
                            String error = throwable.getMessage();
                            if (++retryCount >= 10) {
                                System.out.println("查询失败");
                                String devid = mSpf.getString("devid", null);
                                if (devid != null) {
                                    deviceState.setText(R.string.mcf_query_fail);
                                } else {
                                    deviceState.setText(R.string.mcf_id_null);
                                }
                                retryCount = 0;
                                setTitle();
                                if (mSubscription != null) {
                                    mSubscription.unsubscribe();
                                }
                            } else {
                                System.out.println("RXJAVA---retryCount---" + retryCount);
                                stopLoop();
                                RxGetDeviceState();
                            }

                        }
                    });
        }


    }

    /**
     * 处理设备状态的返回
     *
     * @param deviceStateResponse 设备状态的返回
     */
    private void handDevideStateResponse(DeviceStateResponse deviceStateResponse) {
        System.out.println("设备状态---" + deviceStateResponse.getInfo().toString());
        int status = deviceStateResponse.getStatus();
        System.out.println("设备状态---" + status);
        if (200 == status) {
            DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
            if (infoBean != null) {
                String workMode = infoBean.getWorkmode();
                deviceType = infoBean.getType();
                System.out.println("设备类型---" + deviceType);
                boolean isService = infoBean.isService();
                System.out.println("isService---" + isService);
                //设置服务是否到期
                Urls.deviceServeice = isService;
                System.out.println("服务是否到期---" + Urls.deviceServeice);
                if (isService){
                    if (workMode.equals("NORMAL")) {
                        deviceState.setText(R.string.mcf_normal);
                    } else if (workMode.equals("OFFING")) {
                        deviceState.setText(R.string.mcf_offing);
                    } else if (workMode.equals("STDBY")) {
                        deviceState.setText(R.string.mcf_stdby);
                    } else if (workMode.equals("OFF")) {
                        deviceState.setText(R.string.mcf_off);
                    } else if (workMode.equals("UNKNOWN")) {
                        deviceState.setText(R.string.mcf_unknow);
                    } else if (workMode.equals("BOOTING")) {
                        deviceState.setText(R.string.mcf_booting);
                    }
                }else {
                    deviceState.setText(R.string.mcf_service_is_over);
                }
            }

        } else if (600 == status) {
            System.out.println("为600");
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
            mSpf.edit().putBoolean("is_keeping_get_device_state", true).apply();
            dialog2 = LoadingProgressDialog.createLoadingDialog(getContext(), getString(R.string.mcf_account_offline), false, true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                    getContext().startActivity(new Intent(getContext(), Login.class));
                }
            });
            dialog2.show();
        }
    }

    /**
     * 停止轮询设备状态
     */
    public void stopLoop() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    private boolean chageNetWork() {

        if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("ziv_box_") &&
                NetworkTypeUtils.is2GDevice(deviceType)) {
            DialogUtils.showPrompt2(getContext(), getString(R.string.add_device_tips),
                    getString(R.string.mcf_close_device_wifi),
                    getString(R.string.add_device_ensure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                        }
                    },
                    getString(R.string.common_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            return true;
        }
        return false;
    }


    private void showChangeWifiDialog(){
        DialogUtils.showPrompt2(getContext(), getString(R.string.add_device_tips),
                getString(R.string.mcf_open_device_wifi),
                getString(R.string.add_device_ensure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                },
                getString(R.string.common_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}
