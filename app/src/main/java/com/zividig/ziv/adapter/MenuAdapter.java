/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zividig.ziv.adapter;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;

import java.util.List;

/**
 * 我的设备的适配器
 * Created by YOLANDA on 2016/7/22.
 */
public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    private List<DeviceInfoBean.DevinfoBean> devinfoList;
    private DeviceInfoBean.DevinfoBean devinfoBean;

    private String devid;
    private SharedPreferences spf;

    private OnItemClickListener mOnItemClickListener;

    public List<DeviceInfoBean.DevinfoBean> getDevinfoList() {
        return devinfoList;
    }

    public void setDevinfoList(List<DeviceInfoBean.DevinfoBean> devinfoList) {
        this.devinfoList = devinfoList;
    }

    public MenuAdapter(SharedPreferences spf) {
        this.spf = spf;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return devinfoList == null ? 0 : devinfoList.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
    }

    @Override
    public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        devinfoList = getDevinfoList();
        devid = spf.getString("devid","");
        System.out.println("获取保存的devid" + devid);
        devinfoBean = devinfoList.get(position);
        holder.setData(devinfoBean.getDevid(),devinfoBean.getCarid(),devinfoBean.getAlias());
        holder.setOnItemClickListener(mOnItemClickListener);
        if (devinfoBean.getDevid().equals(devid)){
            System.out.println("titles  id:---" + devinfoBean.getDevid() + "\ndevid---" + devid);
            holder.setDuigouShow(true);
        }
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mdTvDevid;
        TextView mdTvCarid;
        TextView mdTvAlias;
        ImageView duiGou;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mdTvDevid = (TextView) itemView.findViewById(R.id.md_tv_devid);
            mdTvCarid = (TextView) itemView.findViewById(R.id.md_tv_carid);
            mdTvAlias = (TextView) itemView.findViewById(R.id.md_tv_alias);
            duiGou = (ImageView) itemView.findViewById(R.id.iv_duigou);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String devid,String carid,String alias) {
            this.mdTvDevid.setText(devid);
            this.mdTvCarid.setText(carid);
            this.mdTvAlias.setText(alias);
        }

        public  void setDuigouShow(boolean isShow){
            if (isShow){
                duiGou.setVisibility(View.VISIBLE);
            }else {
                duiGou.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
