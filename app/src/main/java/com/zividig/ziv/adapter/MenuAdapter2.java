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

import java.util.List;

/**
 * 消息的适配器
 * Created by YOLANDA on 2016/7/22.
 */
public class MenuAdapter2 extends SwipeMenuAdapter<MenuAdapter2.DefaultViewHolder> {

    private SharedPreferences spf;
    private List<String> titles;
    private OnItemClickListener mOnItemClickListener;

    public MenuAdapter2(List<String> titles,SharedPreferences spf) {
        this.titles = titles;
        this.spf = spf;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item2, parent, false);
    }

    @Override
    public MenuAdapter2.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MenuAdapter2.DefaultViewHolder holder, int position) {
        holder.setData(titles.get(position));
        holder.setOnItemClickListener(mOnItemClickListener);
        boolean isShow = spf.getBoolean("red_point",true);
        if (isShow)
            holder.setRedPoint(true);
        else
            holder.setRedPoint(false);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMfTitle;  //消息内容
        TextView tvMfTime;   //时间
        ImageView ivRedPoint; //红点

        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvMfTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvMfTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivRedPoint = (ImageView) itemView.findViewById(R.id.iv_red_point);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(String title) {
            this.tvMfTitle.setText(title);
        }

        public void setRedPoint(boolean isShow){
            if (isShow)
                this.ivRedPoint.setVisibility(View.VISIBLE);
            else
                this.ivRedPoint.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
                setRedPoint(false);
            }
        }
    }

}