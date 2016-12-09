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
import com.zivdigi.helloffmpeg.MyTestActivity;
import com.zivdigi.helloffmpeg.TestDecoder;
import com.zividig.ziv.R;
import com.zividig.ziv.function.AddDevice;
import com.zividig.ziv.function.CarInfo;
import com.zividig.ziv.function.CarLocation;
import com.zividig.ziv.function.ElectronicFence;
import com.zividig.ziv.function.RealTimeShow;
import com.zividig.ziv.function.TrackQueryDateChoose;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.service.LocationService;
import com.zividig.ziv.utils.MyAlarmManager;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.WifiDirectUtils;
import com.zividig.ziv.weizhang.activity.WeiZhangMainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 我的车
 */
public class MyCarFragment extends Fragment {

    private View view; //布局文件

    private ConvenientBanner convenientBanner; //顶部广告栏控件
    private ArrayList<Integer> localImages = new ArrayList<Integer>();

    private String[] itemTexts = {"实时预览",
                                  "车辆信息",
                                  "车辆定位",
                                  "电子围栏",
                                  "违章查询",
                                  "轨迹查询"};
    private int[] itemImages = {R.drawable.selector_real_time,
                                 R.drawable.selector_car_info,
                                 R.drawable.selector_car_location,
                                 R.drawable.selector_electric_fence,
                                 R.drawable.selector_car_manage,
                                 R.drawable.selector_history_back};
    private String devId;

    private TextView tvDevidState;
    private TextView deviceState;
    private SharedPreferences mSpf;

    public static MyCarFragment instance() {
        MyCarFragment myCarView = new MyCarFragment();
        return myCarView;
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    deviceState.setText("在线");
                    break;
                case 101:
                    deviceState.setText("休眠");
                    break;
                case 102:
                    deviceState.setText("离线");
                    break;
                case 103:
                    deviceState.setText("未知");
                    break;
                case 104:
                    deviceState.setText("正在查询");
            }
        }
    };

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            String devid = mSpf.getString("devid","");
            RequestParams params = new RequestParams(Urls.DEVICE_STATE);
            params.addBodyParameter("devid",devid);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        System.out.println("查询状态结果---" + result);
                        JSONObject json = new JSONObject(result);
                        String workMode = json.getString("workmode");
                        if (workMode.equals("NORMAL")){
                            mHandler.sendEmptyMessage(100);
                        }else if (workMode.equals("STDBY")){
                            mHandler.sendEmptyMessage(101);
                        }else if (workMode.equals("OFF")){
                            mHandler.sendEmptyMessage(102);
                        }else if (workMode.equals("UNKNOWN")){
                            mHandler.sendEmptyMessage(103);
                        }
                        mHandler.postDelayed(mRunnable,2000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("解析失败");
                        mHandler.sendEmptyMessage(104);
                        mHandler.removeCallbacks(mRunnable);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    mHandler.sendEmptyMessage(104);
                    mHandler.removeCallbacks(mRunnable);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mycar, null);
        mSpf = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);

        initView();
        initAd();
        initFunctionButton();
        getDevID();
        if (!devId.equals("")){
            mHandler.postDelayed(mRunnable,2000);
        }
        return view;
    }


    private void initView(){

        tvDevidState = (TextView) view.findViewById(R.id.tv_device_states);
        deviceState = (TextView) view.findViewById(R.id.tv_device_state);

        tvDevidState.setVisibility(View.VISIBLE);
        deviceState.setVisibility(View.VISIBLE); //设备状态

        //设置标题
        TextView title = (TextView) view.findViewById(R.id.tv_title);
        title.setText("我的车");

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
                        System.out.println("实时预览" + position);
                        if (!devId.equals("")) {
                            //判断是否是设备WIFI
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")){
                                WifiDirectUtils.WifiDirect(getContext(),MyTestActivity.class);
                            }else {
                                startActivity(new Intent(getContext(), RealTimeShow.class));
                            }

                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 1:
                        System.out.println("车辆信息" + position);
                        startActivity(new Intent(getContext(), CarInfo.class));
                        break;
                    case 2:
                        System.out.println("车辆定位" + position);
                        if (!devId.equals("")) {
                            //开启轮询服务获取GPS信息
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")){
                                startActivity(new Intent(getContext(), CarLocation.class));
                            }else {
                                MyAlarmManager.startPollingService(getContext(),1,LocationService.class,devId);
                                startActivity(new Intent(getContext(), CarLocation.class));
                            }

                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 3:
                        System.out.println("电子围栏" + position);
                        if (!devId.equals("")) {
                            //开启轮询服务获取GPS信息
                            if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")){
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            }else {
                                MyAlarmManager.startPollingService(getContext(),1,LocationService.class,devId);
                                startActivity(new Intent(getContext(), ElectronicFence.class));
                            }
                        } else {
                            ToastShow.showToast(getContext(), "请先添加设备");
                        }
                        break;
                    case 4:
                        System.out.println("违章查询" + position);
                        startActivity(new Intent(getContext(), WeiZhangMainActivity.class));
                        break;
                    case 5:
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
        mHandler.postDelayed(mRunnable,2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
        System.out.println("暂停");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("停止");
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        countDownTimer.cancel();
    }

    private void getDevID(){
        devId = mSpf.getString("devid","");
        System.out.println("fragment---deviceId:" + devId);
    }

    /**
     * 在设备wifi情况下看视频
     */
    private void showVideoInDeviceWifi(){
        TestDecoder.setUrl("rtsp://192.168.1.1/stream1");
        startActivity(new Intent(getContext(), MyTestActivity.class));
    }

    public void startGetDeviceState(){
        mHandler.postDelayed(mRunnable,2000);
    }

    public void stoptGetDeviceState(){
        if (mRunnable != null){
            mHandler.removeCallbacks(mRunnable);
        }

    }
}
