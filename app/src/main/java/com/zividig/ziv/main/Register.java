package com.zividig.ziv.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zividig.ziv.R;

public class Register extends Activity {

    private EditText etUser;
    private EditText etPwd;
    private EditText etYzm;

    private String user;
    private String pwd;
    private String yzm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("注册");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
    }

    private void initView(){
        etUser = (EditText) findViewById(R.id.register_et_user);
        etPwd = (EditText) findViewById(R.id.register_et_pwd);
        etYzm = (EditText) findViewById(R.id.register_et_yzm);
    }



    //获取验证码
    public void getRegisterYzm(View view){
        user = etUser.getText().toString().trim();
        pwd = etPwd.getText().toString().trim();
        yzm = etYzm.getText().toString().trim();
            if (TextUtils.isEmpty(user)){
                Toast.makeText(Register.this,"请先输入手机号码",Toast.LENGTH_SHORT).show();
            }
    }

}
