package com.zividig.ziv.fragments;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceStateInfoBean;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.function.About;
import com.zividig.ziv.function.LightColor;
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

import java.util.Timer;
import java.util.TimerTask;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

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

    private String workModel;
    private int count;

    private long secondTime = 0;

    private  Timer mTimer;

    public static SettingFragment instance() {
        SettingFragment view = new SettingFragment();
        return view;
    }

    //定时器开始轮询设备状态
    public void startTimer(){

        mTimer = new Timer();

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getDeviceState();
            }
        },0,2000);

    }

    public void stopTimer(){
        if (mTimer != null){
            mTimer.cancel();
        }

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
                            count = 0;

                            //开启轮询服务
                            startTimer();

                            secondTime = System.currentTimeMillis();

                            devID = sp.getString("devid","");

                            //配置json数据
                            final JSONObject json = new JSONObject();
                            try {
                                json.put("devid",devID);
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
                                    devID,
                                    SignatureUtils.token);
                            //发起请求
                            RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_WAKEUP,timestamp,noncestr,signature);
                            params.setBodyContent(json.toString());

                            //只发唤醒命令
                            x.http().post(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    System.out.println("主机唤醒结果：" + result);
                                    try {
                                        JSONObject json = new JSONObject(result);
                                        int status = json.getInt("status");
                                        if (200 == status){

                                            if (!SettingFragment.this.isHidden()){
                                                dialog = LoadingProgressDialog.createLoadingDialog(getContext(),"正在唤醒中...",true,false,null);
                                                dialog.show();
                                            }

                                        }else {
                                            ToastShow.showToast(getContext(),"唤醒指令发送失败");
                                            dialog.dismiss();
                                            stopTimer();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {System.out.println("主机唤醒错误：" + ex);}

                                @Override
                                public void onCancelled(CancelledException cex) {}

                                @Override
                                public void onFinished() {}
                            });

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
     * 获取设备状态
     */
    private void getDeviceState(){
         devID = sp.getString("devid","");
        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("devid",devID);
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
                devID,
                SignatureUtils.token);
        //发起请求
        RequestParams params = HttpParamsUtils.setParams(Urls.DEVICE_STATE,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        System.out.println("获取设备状态---" + params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("结果" + result);
                DeviceStateInfoBean stateInfoBean = JsonUtils.deserialize(result, DeviceStateInfoBean.class);
                int status = stateInfoBean.getStatus();
                if (200 == status){
                    DeviceStateInfoBean.InfoBean infoBean = stateInfoBean.getInfo();
                    count++;
                    System.out.println("count---" + count);
                    workModel = infoBean.getWorkmode();
                    System.out.println("工作模式---" +workModel);

                    if (workModel.equals("BOOTING")){   //主机启动中
                        if (!SettingFragment.this.isHidden()){
                            LoadingProgressDialog.setTipText("主机正在启动中……");
                        }
                    }
                    if (workModel.equals("NORMAL")){        //主机已唤醒
                        showStateInfo("主机已唤醒");
                    }
                    if (workModel.equals("WAKEFAIL")){
                        showStateInfo("主机唤醒失败");
                    }
                    if (workModel.equals("OFF")){
                        showStateInfo("离线状态无法唤醒");
                    }
                    if (count >= 60){
                        showStateInfo("主机唤醒失败");
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
     * 显示唤醒状态的对话框
     * @param smg 唤醒成功或失败
     */
    private void showStateInfo(String smg){

        stopTimer();

        count = 0;

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
                username,
                action,
                SignatureUtils.token);

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
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }
}