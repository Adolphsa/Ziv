package com.zividig.ndk_test.weizhang.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zividig.ndk_test.R;

import java.util.List;

/**
 * Created by adolph
 * on 2016-12-15.
 */

public class ViolationResultAdapter extends BaseAdapter{

    private  Context mContext;
    private List<ViolationResultBean.ResultBean.ListsBean> mResultList;

    public ViolationResultAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = View.inflate(mContext, R.layout.violation_result_item,null);
            holder = new ViewHolder();
            holder.itemTime = (TextView) view.findViewById(R.id.vio_item_time);
            holder.itemPlace = (TextView) view.findViewById(R.id.vio_item_place);
            holder.itemReason = (TextView) view.findViewById(R.id.vio_item_reason);
            holder.itemMoney = (TextView) view.findViewById(R.id.vio_item_money);
            holder.itemScores = (TextView) view.findViewById(R.id.vio_item_scores);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        final ViolationResultBean.ResultBean.ListsBean listsBean = mResultList.get(i);
        holder.itemTime.setText(listsBean.getDate());      //时间
        holder.itemPlace.setText(listsBean.getArea());     //地点
        holder.itemReason.setText(listsBean.getAct());     //违章原因
        holder.itemMoney.setText(listsBean.getMoney());    //罚款金额
        holder.itemScores.setText(listsBean.getFen());     //扣分

        return view;
    }

    static class ViewHolder{
        TextView itemTime;
        TextView itemPlace;
        TextView itemReason;
        TextView itemMoney;
        TextView itemScores;
    }

    public void addItem(List<ViolationResultBean.ResultBean.ListsBean> resultList){
        mResultList = resultList;
    }
}
