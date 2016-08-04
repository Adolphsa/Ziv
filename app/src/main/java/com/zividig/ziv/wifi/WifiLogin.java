package com.zividig.ziv.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dtr.zxing.activity.CaptureActivity;
import com.zividig.ziv.R;

public class WifiLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_login);
    }

    public void setTwoDimensionCode(View view){
        startActivity(new Intent(WifiLogin.this, CaptureActivity.class));
    }
}
