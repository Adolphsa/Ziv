package com.zividig.ziv.main;

import android.app.Activity;

import com.baidu.mapapi.SDKInitializer;
import com.zividig.ziv.R;
import com.zividig.ziv.utils.StatusBarUtils;

/**
 * Created by adolph
 * on 2016-10-11.
 */

public class BaseActivity extends Activity {

    @Override
    public void setContentView(int layoutResID) {
        SDKInitializer.initialize(this.getApplicationContext()); //初始化百度地图
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.myColorPrimaryDark));
    }
}
