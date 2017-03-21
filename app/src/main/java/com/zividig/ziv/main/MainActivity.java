package com.zividig.ziv.main;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zividig.ziv.R;
import com.zividig.ziv.customView.CustomsViewPager;
import com.zividig.ziv.fragments.MessageFragment;
import com.zividig.ziv.fragments.MyCarFragment;
import com.zividig.ziv.fragments.MyFragment;
import com.zividig.ziv.fragments.SettingFragment;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.StatusBarUtils;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{

    public AdvancedPagerSlidingTabStrip mTabs;
    public CustomsViewPager mViewPager;

    private static final int VIEW_FIRST 	= 0;
    private static final int VIEW_SECOND	= 1;
    private static final int VIEW_THIRD     = 2;
    private static final int VIEW_FOURTH    = 3;

    private static final int VIEW_SIZE = 4;

    private MyCarFragment mMyCarFragment = null; //我的车
    private MessageFragment mMessageFragment = null; //消息
    private SettingFragment mSettingFragment = null; //设置
    private MyFragment mMyFragment = null; //我

    private long exitTime = 0;
    private SharedPreferences mSpf;

    private int currentPage;
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.setColor(this, getResources().getColor(R.color.black_russian));

        mSpf = getSharedPreferences("config", MODE_PRIVATE);

        findViews();
        init();

        String xiaomi = MiPushClient.getRegId(this);
        System.out.println("mainActivity---小米" + xiaomi);
    }

    private void findViews(){
        mTabs = (AdvancedPagerSlidingTabStrip)findViewById(R.id.tabs);
        mViewPager = (CustomsViewPager)findViewById(R.id.vp_main);
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
        mViewPager.setPagingEnabled(false);      //可以滑动
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        System.out.println("position" + position);
        setCurrentPage(position);
        if (position != 0){
            if (mMyCarFragment != null){
//                mMyCarFragment.stopTimer();
                mSpf.edit().putBoolean("is_keeping_get_device_state",false).apply();
            }

        }else {
            if (mMyCarFragment != null){
//                mMyCarFragment.startTimer();
                mSpf.edit().putBoolean("is_keeping_get_device_state",true).apply();
                mMyCarFragment.RxGetDeviceState();
            }
        }
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

    public void setDot(boolean isShow){
        if (isShow){
            mTabs.showDot(VIEW_SECOND); //设置消息
        }else {
            mTabs.hideDot(VIEW_SECOND);
        }

    }
    /**
     * 用户登出
     */
    private void logout(){

        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        String userName = config.getString(Login.ET_USER,"");
        System.out.println("token---" + SignatureUtils.token);

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("username",userName);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                userName,
                SignatureUtils.token);

        RequestParams params = HttpParamsUtils.setParams(Urls.LOGOUT_URL,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
//        System.out.println(params.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (status == Urls.STATUS_CODE_200){
                        System.out.println("成功退出");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {}
        });
    }

    //退出程序前的提示
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
//                logout();
                SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                sp.edit().remove("token").apply();
                sp.edit().remove("is_keeping_get_device_state").apply();
                sp.edit().remove("lat").apply();
                sp.edit().remove("lon").apply();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
