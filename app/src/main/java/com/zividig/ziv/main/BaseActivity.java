package com.zividig.ziv.main;

import android.support.v7.app.AppCompatActivity;

import com.zividig.ziv.R;
import com.zividig.ziv.utils.StatusBarUtils;

/**
 * Created by adolph
 * on 2016-10-11.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.black_russian));
    }
}
