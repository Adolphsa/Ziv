package com.zividig.ziv.function;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.Login;

public class MyAccountInfo extends BaseActivity {

    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_info);

        spf = getSharedPreferences("config", MODE_PRIVATE);

        initView();
    }

    private void initView(){

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText(R.string.my_account_title);

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //我的账号信息
        TextView myAccount = (TextView) findViewById(R.id.my_account_tv);
        String user = spf.getString(Login.ET_USER,"");
        if (!user.equals(""))
            myAccount.setText(user);

    }
}
