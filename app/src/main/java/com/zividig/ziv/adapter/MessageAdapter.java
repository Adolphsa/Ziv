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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.MessageBean;

import java.util.List;

/**
 * 消息的适配器
 * Created by YOLANDA on 2016/7/22.
 */
public class MessageAdapter extends SwipeMenuAdapter<MessageAdapter.DefaultViewHolder> {

    private List<MessageBean.DataBean> mDataBeanList;
    private OnItemClickListener mOnItemClickListener;


    public List<MessageBean.DataBean> getDataBeanList() {
        return mDataBeanList;
    }

    public void setDataBeanList(List<MessageBean.DataBean> dataBeanList) {
        mDataBeanList = dataBeanList;
    }

    public MessageAdapter() {}

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mDataBeanList == null ? 0 : 20;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
    }

    @Override
    public MessageAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.DefaultViewHolder holder, int position) {
        mDataBeanList = getDataBeanList();
        MessageBean.DataBean dataBeen = mDataBeanList.get(position);
        holder.setData(dataBeen.getTitle(),dataBeen.getAddress_desc(),dataBeen.getTime());
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAlarmType;  //报警类型
        TextView tvAlarmAddress;   //报警地址
        TextView tvAlarmTime;   //报警时间

        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvAlarmType = (TextView) itemView.findViewById(R.id.tv_item_alarm_type);
            tvAlarmAddress = (TextView) itemView.findViewById(R.id.tv_item_alarm_address);
            tvAlarmTime = (TextView) itemView.findViewById(R.id.tv_item_alarm_time);

        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String tvAlarmTitle,String tvAlarmAddress,String tvAlarmTime) {

            this.tvAlarmType.setText(tvAlarmTitle);
            this.tvAlarmAddress.setText(tvAlarmAddress);
            this.tvAlarmTime.setText(tvAlarmTime);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
