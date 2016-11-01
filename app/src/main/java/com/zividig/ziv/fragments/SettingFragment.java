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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.function.About;
import com.zividig.ziv.function.LightColor;
import com.zividig.ziv.utils.ToastShow;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 设置
 *
 */
public class SettingFragment extends Fragment {

    private ListView lvSetting;
    ViewHolder holder;
    private SettingAdapter adapter;
    private SharedPreferences sp;
    private String devID;

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

        lvSetting = (ListView) view.findViewById(R.id.lv_setting);
        adapter = new SettingAdapter();
        lvSetting.setAdapter(adapter);
        //listView的点击事件
        lvSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        holder = (ViewHolder) view.getTag();
                        boolean autoUpdate = sp.getBoolean("auto_update",false);
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
                        System.out.println("主机唤醒" + position);
                        devID = sp.getString("devid","");
                        RequestParams params = new RequestParams("http://120.24.174.213:9501/api/wakeupdevice");
                        params.addQueryStringParameter("devid",devID);
                        System.out.println("主机唤醒：" + params);
                        x.http().get(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                System.out.println("主机唤醒结果：" + result);
                                try {
                                    JSONObject json = new JSONObject(result);
                                    int errorCode = json.getInt("error");
                                    System.out.println("错误码是：" + errorCode);
                                    if (errorCode == 200){
                                        ToastShow.showToast(getActivity(),"主机正在唤醒中...");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                System.out.println("主机唤醒错误：" + ex);
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                        break;
                    case 2:
                        System.out.println("灯光设置" + position);
                        startActivity(new Intent(getContext(), LightColor.class));
                        break;
                    case 3:
                        System.out.println("关于" + position);
                        startActivity(new Intent(getContext(), About.class));
                        break;
                }
            }
        });
        return view;
    }
    class SettingAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 4;
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
                   holder.leftIcon.setImageResource(R.mipmap.update);
                   holder.itemText.setText("自动更新");
                   Boolean autoUpdate = sp.getBoolean("auto_update",false);
                   if (autoUpdate){
                       holder.RightIcon.setImageResource(R.mipmap.switch_on);
                   }else {
                       holder.RightIcon.setImageResource(R.mipmap.switch_off);
                   }
                   break;
               case 1:
                   holder.leftIcon.setImageResource(R.mipmap.recover);
                   holder.itemText.setText("主机唤醒");
                   holder.RightIcon.setImageResource(R.mipmap.rights);
                   break;
               case 2:
                   holder.leftIcon.setImageResource(R.mipmap.restaet);
                   holder.itemText.setText("灯光设置");
                   holder.RightIcon.setImageResource(R.mipmap.rights);
                   break;
               case 3:
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
}