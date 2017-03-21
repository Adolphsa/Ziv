package com.zividig.ziv.fragments;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.function.About;
import com.zividig.ziv.function.LightColor;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.DeviceStateBody;
import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.rxjava.model.DeviceWakeResponse;
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
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;
import static com.zividig.ziv.utils.SignatureUtils.token;

/**
 * 设置
 *
 */
public class SettingFragment extends Fragment {

    private ListView lvSettingDevice;
    private ListView lvSettingSoftware;
    ViewHolder holder;
    private SettingAdapter adapter;
    private SharedPreferences sp;
    private String devID;

    private Dialog dialog;
    private Dialog dialog2;

    private long secondTime = 0;
    private int count = 0;

    private Subscription mSubscription;

    public static SettingFragment instance() {
        SettingFragment view = new SettingFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, null);

        sp = getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        //设置标题
        TextView title = (TextView)view.findViewById(R.id.tv_title);
        title.setText("设置");

        //主机设备列表
        lvSettingDevice = (ListView) view.findViewById(R.id.lv_setting_device);
        adapter = new SettingAdapter();
        lvSettingDevice.setAdapter(adapter);
        //listView的点击事件
        lvSettingDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        System.out.println("主机唤醒" + position);
                        //连续点击不生效
                        if ((System.currentTimeMillis()- secondTime) > (2 * 1000)){
                            System.out.println("进来了");
                            if (!SettingFragment.this.isHidden()){
                                dialog = LoadingProgressDialog.createLoadingDialog(getContext(),"正在唤醒中...",true,false,null);
                                dialog.show();
                            }
                            RxSendWakeupOrder();
                            secondTime = System.currentTimeMillis();
                        }else {
                           ToastShow.showToast(getContext(),"请不要重复点击。");
                            break;
                        }
                        break;
                    case 1:
                        System.out.println("灯光设置" + position);
                        startActivity(new Intent(getContext(), LightColor.class));
                        break;

                }
            }
        });

        //软件相关设置列表
        lvSettingSoftware = (ListView) view.findViewById(R.id.lv_setting_software);
        SettingSoftWareAdapter settingSoftWareAdapter = new SettingSoftWareAdapter();
        lvSettingSoftware.setAdapter(settingSoftWareAdapter);
        lvSettingSoftware.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                holder = (ViewHolder) view.getTag();
                switch (position){
                    case 0:
                        boolean autoUpdate = sp.getBoolean("auto_update",true);
                        if (autoUpdate){
                            holder.RightIcon.setImageResource(R.mipmap.switch_off); //关闭自动更新
                            sp.edit().putBoolean("auto_update",false).apply();
                            System.out.println("关闭自动更新");

                        }else {
                            holder.RightIcon.setImageResource(R.mipmap.switch_on); //开启自动更新
                            sp.edit().putBoolean("auto_update",true).apply();
                            System.out.println("开启自动更新");
                        }
                        break;
                    case 1:
                        System.out.println("震动免打扰" + position);
                        String username = sp.getString("et_user", "");
                        System.out.println("用户名" + username);
                        String alarmState = sp.getString("alarm_status","");
                        System.out.println("");
                        if (alarmState.equals("open")){
                            holder.RightIcon.setImageResource(R.mipmap.switch_on); //不允许推送
                            //设置成不推送
                            setAlarmDoNotDisturb(username,"close");
                            sp.edit().putString("alarm_status","close").apply();
                            System.out.println("设置成不推送");

                        }else {
                            holder.RightIcon.setImageResource(R.mipmap.switch_off); //允许推送
//                            sp.edit().putBoolean("no_disturb",true).apply();
                            //设置成推送
                            setAlarmDoNotDisturb(username,"open");
                            sp.edit().putString("alarm_status","open").apply();
                            System.out.println("设置成推送");
                        }
                        break;
                    case 2:
                        System.out.println("关于" + position);
                        startActivity(new Intent(getContext(), About.class));
                        break;
                }
            }
        });
        return view;
    }

    /**
     * 配置options
     * @return options
     */
    private Map<String, String> setOp(){

        String devid = sp.getString("devid", null);
        String token = sp.getString("token", null);


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

        String devid = sp.getString("devid", null);
        String token = sp.getString("token", null);

        //配置请求体
        DeviceStateBody body = new  DeviceStateBody();
        body.devid = devid;
        body.token = token;
        String stringDeviceListBody = JsonUtils.serialize(body);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), stringDeviceListBody);

        return jsonBody;
    }


    /**
     * 发送唤醒主机命令
     */
    private void RxSendWakeupOrder(){

        final Map<String, String> options = setOp();
        final RequestBody jsonBody = setBody();

        mSubscription =  ZivApiManage.getInstance().getZhihuApiService()
                .getDeviceStateInfo(options,jsonBody)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<DeviceStateResponse, Observable<DeviceWakeResponse>>() {
                    @Override
                    public Observable<DeviceWakeResponse> call(DeviceStateResponse deviceStateResponse) {
                        System.out.println("判断设备状态是否不为休眠");
                        DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
                        String deviceState = infoBean.getWorkmode();
                        if (deviceState.equals("NORMAL")){
                            return Observable.error(new Exception("normal"));
                        }else if (!deviceState.equals("STDBY")){
                            System.out.println("异常执行了2");
                            return Observable.error(new Exception("no_stdby"));
                        }
                        return ZivApiManage.getInstance().getZhihuApiService().sendWakeupOrder(options,jsonBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeviceWakeResponse>() {
                    @Override
                    public void call(DeviceWakeResponse deviceWakeResponse) {
                        int status = deviceWakeResponse.getStatus();
                        if (200 == status){
                            System.out.println("发送唤醒命令");
                            RxGetDeviceState();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("啦啦啦---" + throwable.getMessage());
                        String t = throwable.getMessage();
                        if (t.equals("normal")){
                            showStateInfo("主机已在线，无需唤醒!");
                        }else if(t.equals("no_stdby")){
                            showStateInfo("非休眠状态不能唤醒主机！");
                        }
                    }
                });
    }

    /**
     * 轮询获取设备状态
     */
    public void RxGetDeviceState() {

        count =0;
        String token = sp.getString("token", null);
        String devid = sp.getString("devid", null);

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


        mSubscription = Observable.interval(0,2,TimeUnit.SECONDS)
                .take(20)
                .flatMap(new Func1<Long, Observable<DeviceStateResponse>>() { //先查看设备的状态
                    @Override
                    public Observable<DeviceStateResponse> call(Long aLong) {
                        return ZivApiManage.getInstance().getZhihuApiService().getDeviceStateInfo(options,jsonBody);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .takeUntil(new Func1<DeviceStateResponse, Boolean>() {
                    @Override
                    public Boolean call(DeviceStateResponse deviceStateResponse) {
                        DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
                        String deviceState = infoBean.getWorkmode();
                        count++;
                        System.out.println();
                        if (!TextUtils.isEmpty(deviceState) && deviceState.equals("NORMAL")){
                            System.out.println("轮询---主机在线了");
                            showStateInfo("主机唤醒成功!");
                            return true;
                        }
                        return false;
                    }
                })
                .subscribe(new Action1<DeviceStateResponse>() {
                    @Override
                    public void call(DeviceStateResponse deviceStateResponse) {
                        System.out.println("RXJAVA---设备状态---" + deviceStateResponse.getInfo().getWorkmode());
                        DeviceStateResponse.InfoBean infoBean = deviceStateResponse.getInfo();
                        String deviceState = infoBean.getWorkmode();
                        if (deviceState.equals("BOOTING")) {
                            LoadingProgressDialog.setTipText("主机正在启动中……");
                            System.out.println("主机正在启动中……");
                        } else if (deviceState.equals("NORMAL")) {
                            System.out.println("onNext---主机在线了");
                            Observable.error(new Exception("normal"));
                        } else if (deviceState.equals("WAKEFAIL")) {
                            LoadingProgressDialog.setTipText("主机唤醒失败");
                            System.out.println("主机唤醒失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("RXJAVA---设备状态出错---" + throwable.getMessage());
                        String t = throwable.getMessage();
                        if (t.equals("normal")) {
                            showStateInfo("主机唤醒成功!");
                            if (mSubscription != null){
                                mSubscription.unsubscribe();
                            }
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                     if (count > 19){
                         showStateInfo("主机唤醒失败!");
                         if (mSubscription != null){
                             mSubscription.unsubscribe();
                         }
                     }
                    }
                });
    }

    /**
     * 显示唤醒状态的对话框
     * @param smg 唤醒成功或失败
     */
    private void showStateInfo(String smg){

        //关闭dialog1
        if (dialog != null){
            dialog.dismiss();
        }

        //显示dialog2
        dialog2 = LoadingProgressDialog.createLoadingDialog(getContext(), smg, false, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        dialog2.show();
    }

    /**
     * 设置震动消息免打扰开关
     * @param username  用户名
     * @param action    动作（开或关）
     */
    private void setAlarmDoNotDisturb(String username,String action){

        //配置json数据
        final JSONObject json = new JSONObject();
        try {
            json.put("username",username);
            json.put("action", action);
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
                username,
                action,
                token);

        RequestParams params = HttpParamsUtils.setParams(Urls.SETTING_ALARM_DO_NOT_DISTURB,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
        System.out.println("params---" + params.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("震动消息免设置消息返回结果---" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (200 == status){
                        System.out.println("设置震动消息免打扰成功");
//                        ToastShow.showToast(getContext(),"设置成功");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("震动消息免打扰设置异常---" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    class SettingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null){
                convertView = View.inflate(getContext(),R.layout.layout_setting_list_view,null);
                holder = new ViewHolder();
                holder.leftIcon = (ImageView) convertView.findViewById(R.id.item_left_icon);
                holder.itemText = (TextView) convertView.findViewById(R.id.item_text);
                holder.RightIcon = (ImageView) convertView.findViewById(R.id.item_right_icon);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

           switch (position){
               case 0:
                   holder.leftIcon.setImageResource(R.mipmap.recover);
                   holder.itemText.setText("主机唤醒");
                   holder.RightIcon.setImageResource(R.mipmap.rights);
                   break;
               case 1:
                   holder.leftIcon.setImageResource(R.mipmap.photo_flash);
                   holder.itemText.setText("灯光设置");
                   holder.RightIcon.setImageResource(R.mipmap.rights);
                   break;

           }
            return convertView;
        }
    }

    class SettingSoftWareAdapter extends BaseAdapter{

        @Override
        public int getCount() {return 3;}

        @Override
        public Object getItem(int position) {return null;}

        @Override
        public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = View.inflate(getContext(),R.layout.layout_setting_list_view,null);
                holder = new ViewHolder();
                holder.leftIcon = (ImageView) convertView.findViewById(R.id.item_left_icon);
                holder.itemText = (TextView) convertView.findViewById(R.id.item_text);
                holder.RightIcon = (ImageView) convertView.findViewById(R.id.item_right_icon);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (position){
                case 0:
                    holder.leftIcon.setImageResource(R.mipmap.update);
                    holder.itemText.setText("自动更新");
                    Boolean autoUpdate = sp.getBoolean("auto_update",true);
                    if (autoUpdate){
                        holder.RightIcon.setImageResource(R.mipmap.switch_on);
                    }else {
                        holder.RightIcon.setImageResource(R.mipmap.switch_off);
                    }
                    break;

                case 1:
                    holder.leftIcon.setImageResource(R.mipmap.restaet);
                    holder.itemText.setText("震动免打扰");
                    String alarmState = sp.getString("alarm_status","");
                    if (alarmState.equals("open")){
                        holder.RightIcon.setImageResource(R.mipmap.switch_off);
                    }else {
                        holder.RightIcon.setImageResource(R.mipmap.switch_on);
                    }
                    break;
                case 2:
                    holder.leftIcon.setImageResource(R.mipmap.about);
                    holder.itemText.setText("关于");
                    holder.RightIcon.setImageResource(R.mipmap.rights);
                    break;
            }
            return convertView;
        }
    }

    static class ViewHolder{
        ImageView leftIcon;
        TextView itemText;
        ImageView RightIcon;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Setting---onStop");
        if (mSubscription != null){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}