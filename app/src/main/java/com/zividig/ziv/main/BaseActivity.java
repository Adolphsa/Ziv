package com.zividig.ziv.main;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.zividig.ziv.R;
import com.zividig.ziv.utils.StatusBarUtils;

import java.util.Locale;

/**
 * Created by adolph
 * on 2016-10-11.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
//        getLanguage();
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getResources().getColor(R.color.black_russian));
    }

    private void getLanguage(){

        SharedPreferences spf = getSharedPreferences("config", MODE_PRIVATE);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        String language = spf.getString("ziv_language","");
        System.out.println("语言---" + language);
        switch (language){
            case "default":
                System.out.println("default");
                configuration.locale = Locale.getDefault();
                resources.updateConfiguration(configuration, dm);
                break;
            case "ru":
                System.out.println("ru");
                configuration.locale = new Locale("ru");
                resources.updateConfiguration(configuration, dm);
                break;
        }
    }
}
