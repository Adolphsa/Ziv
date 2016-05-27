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
 * 我的车
 * Created by linhonghong on 2015/8/11.
 */
public class MyCarFragment extends Fragment {

    public static MyCarFragment instance() {
        MyCarFragment view = new MyCarFragment();
		return view;
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycar,null);

        //设置标题
        TextView title = (TextView)view.findViewById(R.id.tv_title);
        title.setText("我的车");
        return view;
    }
}
