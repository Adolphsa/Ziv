package com.zividig.ziv.function;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.SplashActivity;

import java.util.Locale;

public class Language extends AppCompatActivity {

    RadioGroup lanRg;
    RadioButton cbChinese;
    RadioButton cbRussian;
    Button lanSure;

    private SharedPreferences mSpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mSpf = getSharedPreferences("config",MODE_PRIVATE);
        //设置获取设备状态为真，以便在Activity销毁时能重新获取设备状态
        mSpf.edit().putBoolean("is_keeping_get_device_state",true).apply();

        initView();
    }

    private void initView(){

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("语言");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lanRg = (RadioGroup) findViewById(R.id.lan_rg);
        cbChinese = (RadioButton) findViewById(R.id.lan_chinese);
        cbRussian = (RadioButton) findViewById(R.id.lan_russian);
        lanSure = (Button) findViewById(R.id.lan_sure);


//        lanRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                System.out.println("checkedId---" + checkedId);
//            }
//        });

        String languageIsChecked = mSpf.getString("ziv_language","");
        if (languageIsChecked.equals("default")){
            cbChinese.setChecked(true);
        }else if (languageIsChecked.equals("ru")){
            cbRussian.setChecked(true);
        }


        cbChinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cbChinese is checked");

                Resources resources = getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                mSpf.edit().putString("ziv_language","default").apply();
                config.locale = Locale.getDefault();
                resources.updateConfiguration(config, dm);
            }
        });

        cbRussian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cbRussian is checked");

                Resources resources = getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();
                Configuration config = resources.getConfiguration();
                config.locale = new Locale("ru");
                mSpf.edit().putString("ziv_language","ru").apply();
                resources.updateConfiguration(config, dm);
            }
        });

        lanSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Language.this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }
}
