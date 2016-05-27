package com.zividig.ziv.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.adapter.ImagePagerAdapter;
import com.zividig.ziv.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * 我的车
 * Created by linhonghong on 2015/8/11.
 */
public class MyCarFragment extends Fragment {

    private List<Integer>       imageIdList;

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

        AutoScrollViewPager autoScrollViewPager = (AutoScrollViewPager) view.findViewById(R.id.auto_scroll_view_pager);
        imageIdList = new ArrayList<Integer>();
        imageIdList.add(R.mipmap.ad1);
        imageIdList.add(R.mipmap.ad2);
        imageIdList.add(R.mipmap.ad3);


        autoScrollViewPager.setAdapter(new ImagePagerAdapter(getContext(), imageIdList).setInfiniteLoop(true));
        autoScrollViewPager.addOnPageChangeListener(new MyOnChangeListener());
        autoScrollViewPager.setInterval(2000);
        autoScrollViewPager.startAutoScroll();
        autoScrollViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % ListUtils.getSize(imageIdList));

        return view;
    }

    class MyOnChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
