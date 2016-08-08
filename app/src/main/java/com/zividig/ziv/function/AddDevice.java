package com.zividig.ziv.function;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.utils.ToastShow;

public class AddDevice extends Activity {

    private TextView deviceId;
    private TextView tvTwoCode;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        spf = getSharedPreferences("config",MODE_PRIVATE);

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

        //设置二维码
        Button setTwoCode = (Button) findViewById(R.id.bt_set_two_code);
        setTwoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isWifiOrMobile = ((ZivApp) getApplication()).getIsWifiOrMobile();
                System.out.println("判断是设备wifi还是手机网络:---" + isWifiOrMobile);
                if (isWifiOrMobile){
                    startActivity(new Intent(AddDevice.this, CaptureActivity.class));
                }else {
                    ToastShow.showToast(AddDevice.this,"请连接设备WIFI");
                }
            }
        });

        //显示设备ID
        deviceId = (TextView) findViewById(R.id.tv_device_id);
        String devId = Login.getDevId();
        if (devId != null){
            deviceId.setText(Login.getDevId());
        }

        //显示二维码
        String twoCode = spf.getString("two_code", "");
        System.out.println("重新创建");
        tvTwoCode = (TextView) findViewById(R.id.tv_two_code);
        if (!TextUtils.isEmpty(twoCode)){
            tvTwoCode.setText(twoCode);
        }
    }


}
