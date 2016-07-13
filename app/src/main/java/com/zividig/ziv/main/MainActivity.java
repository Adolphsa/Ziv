package com.zividig.ziv.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;
import com.zividig.ziv.R;
import com.zividig.ziv.customView.CustomViewPager;
import com.zividig.ziv.fragments.MyCarFragment;
import com.zividig.ziv.fragments.MyFragment;
import com.zividig.ziv.fragments.MessageFragment;
import com.zividig.ziv.fragments.SettingFragment;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{

    public AdvancedPagerSlidingTabStrip mTabs;
    public CustomViewPager mViewPager;

    private static final int VIEW_FIRST 	= 0;
    private static final int VIEW_SECOND	= 1;
    private static final int VIEW_THIRD     = 2;
    private static final int VIEW_FOURTH    = 3;

    private static final int VIEW_SIZE = 4;

    private MyCarFragment mMyCarFragment = null; //我的车
    private MessageFragment mMessageFragment = null; //消息
    private SettingFragment mSettingFragment = null; //设置
    private MyFragment mMyFragment = null; //我

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        init();
    }

    private void findViews(){
        mTabs = (AdvancedPagerSlidingTabStrip)findViewById(R.id.tabs);
        mViewPager = (CustomViewPager)findViewById(R.id.vp_main);
    }

    //初始化
    private void init(){
        mViewPager.setOffscreenPageLimit(VIEW_SIZE);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        adapter.notifyDataSetChanged();
        mTabs.setViewPager(mViewPager);
        mTabs.setOnPageChangeListener(this);

        mViewPager.setCurrentItem(VIEW_FIRST); //设置默认选中的选项卡
        mTabs.showDot(VIEW_SECOND,"99+"); //设置消息
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FragmentAdapter extends FragmentStatePagerAdapter implements AdvancedPagerSlidingTabStrip.IconTabProvider{

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position >= 0 && position < VIEW_SIZE){
                switch (position){
                    case  VIEW_FIRST:
                        if(null == mMyCarFragment)
                            mMyCarFragment = MyCarFragment.instance();
                        return mMyCarFragment;

                    case VIEW_SECOND:
                        if(null == mMessageFragment)
                            mMessageFragment = MessageFragment.instance();
                        return mMessageFragment;

                    case VIEW_THIRD:
                        if(null == mSettingFragment)
                            mSettingFragment = SettingFragment.instance();
                        return mSettingFragment;

                    case VIEW_FOURTH:
                        if(null == mMyFragment)
                            mMyFragment = MyFragment.instance();
                        return mMyFragment;
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return VIEW_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {  //设置选项卡文字
            if(position >= 0 && position < VIEW_SIZE){
                switch (position){
                    case  VIEW_FIRST:
                        return  "我的车";
                    case  VIEW_SECOND:
                        return  "消息";
                    case  VIEW_THIRD:
                        return  "设置";
                    case  VIEW_FOURTH:
                        return  "我";
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public Integer getPageIcon(int index) { //设置选项卡没选中的图片
            if(index >= 0 && index < VIEW_SIZE){
                switch (index){
                    case  VIEW_FIRST:
                        return  R.mipmap.mycar_white;
                    case VIEW_SECOND:
                        return  R.mipmap.message_white;
                    case VIEW_THIRD:
                        return  R.mipmap.setting_white;
                    case VIEW_FOURTH:
                        return  R.mipmap.my_white;
                    default:
                        break;
                }
            }
            return 0;
        }

        @Override
        public Integer getPageSelectIcon(int index) { //设置选项卡选中时候的图片
            if(index >= 0 && index < VIEW_SIZE){
                switch (index){
                    case  VIEW_FIRST:
                        return  R.mipmap.mycar_white_select;
                    case VIEW_SECOND:
                        return  R.mipmap.message_white_select;
                    case VIEW_THIRD:
                        return  R.mipmap.setting_white_select;
                    case VIEW_FOURTH:
                        return  R.mipmap.my_white_select;
                    default:
                        break;
                }
            }
            return 0;
        }

        @Override
        public Rect getPageIconBounds(int position) {
            return null;
        }
    }
}
