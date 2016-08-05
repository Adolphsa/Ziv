package com.zividig.ziv.function;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.Login;

public class AddDevice extends Activity {

    private TextView deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("添加设备");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deviceId = (TextView) findViewById(R.id.tv_device_id);
        String devId = Login.getDevId();
        if (devId != null){
            deviceId.setText(Login.getDevId());
        }
    }
}
