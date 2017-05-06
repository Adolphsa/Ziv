package com.zividig.ziv.function;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;

public class LightColor extends BaseActivity implements View.OnClickListener{

    private ImageView mLightImage;
    private ImageView mDuigou1;
    private ImageView mDuigou2;
    private ImageView mDuigou3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_color);

        initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText(R.string.light_title);

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLightImage = (ImageView) findViewById(R.id.light_color_light);
        ImageView lightSgreen = (ImageView) findViewById(R.id.light_color_sgreen);
        ImageView lightRed = (ImageView) findViewById(R.id.light_color_red);
        ImageView lightBlue = (ImageView) findViewById(R.id.light_color_blue);

        mDuigou1 = (ImageView) findViewById(R.id.duigou1);
        mDuigou2 = (ImageView) findViewById(R.id.duigou2);
        mDuigou3 = (ImageView) findViewById(R.id.duigou3);

        lightSgreen.setOnClickListener(this);
        lightRed.setOnClickListener(this);
        lightBlue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.light_color_sgreen:
                System.out.println("green");
                mLightImage.setBackgroundResource(R.mipmap.light_sgreen);
                mDuigou1.setVisibility(View.VISIBLE);
                mDuigou2.setVisibility(View.GONE);
                mDuigou3.setVisibility(View.GONE);
                break;
            case R.id.light_color_red:
                System.out.println("red");
                mLightImage.setBackgroundResource(R.mipmap.light_red);
                mDuigou1.setVisibility(View.GONE);
                mDuigou2.setVisibility(View.VISIBLE);
                mDuigou3.setVisibility(View.GONE);
                break;
            case R.id.light_color_blue:
                System.out.println("blue");
                mLightImage.setBackgroundResource(R.mipmap.light_blue);
                mDuigou1.setVisibility(View.GONE);
                mDuigou2.setVisibility(View.GONE);
                mDuigou3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
