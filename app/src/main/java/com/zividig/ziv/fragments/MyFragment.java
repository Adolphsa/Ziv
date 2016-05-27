package com.zividig.ziv.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zividig.ziv.R;

/**
 * 我
 * Created by linhonghong on 2015/8/11.
 */
public class MyFragment extends Fragment {

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
        return view;
    }
}