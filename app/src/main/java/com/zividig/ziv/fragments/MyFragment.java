package com.zividig.ziv.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.zividig.ziv.R;
import com.zividig.ziv.function.MyDevice;
import com.zividig.ziv.function.MyPicture;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.utils.ToastShow;

import java.io.File;

/**
 * 我
 *
 */
public class MyFragment extends Fragment {

    private static String path = Environment.getExternalStorageDirectory() + "/Ziv";

    public static MyFragment instance() {
        MyFragment view = new MyFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);

        //设置标题
        TextView title = (TextView)view.findViewById(R.id.tv_title);
        title.setText("我");

        ListView lvMy = (ListView) view.findViewById(R.id.lv_my);
        lvMy.setAdapter(new MyAdapter());
        lvMy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: //我的账号
//                        startActivity(new Intent(getContext(), TestShareVideo.class));
                        break;
                    case 1: //我的图片
                        File file = new File(path);
                        if (file.exists()){
                            startActivity(new Intent(getContext(), MyPicture.class));
                        }else {
                            Toast.makeText(getContext(),"无图片",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2: //我的设备
                        if (Login.getDeviceList().size() > 0){
                            startActivity(new Intent(getContext(), MyDevice.class));
                        }else {
                            ToastShow.showToast(getContext(),"无设备");
                        }

                        break;
                }
            }
        });
        return view;
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 3;
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
            ViewHolder holder;
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
                    holder.leftIcon.setImageResource(R.mipmap.my_account);
                    holder.itemText.setText("我的帐号");
                    break;
                case 1:
                    holder.leftIcon.setImageResource(R.mipmap.my_ablum);
                    holder.itemText.setText("我的图片");
                    break;
                case 2:

                    holder.itemText.setText("我的设备");
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