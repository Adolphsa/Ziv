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

public class FindPassWord extends Activity {

    private EditText etUser;
    private EditText etPwd;
    private EditText etYzm;

    private String fdUser;
    private String fdPwd;
    private String fdYzm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pass_word);

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("重置密码");

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

        etUser = (EditText) findViewById(R.id.findPwd_et_user);
        etPwd = (EditText) findViewById(R.id.findPwd_et_pwd);
        etYzm = (EditText) findViewById(R.id.findPwd_et_yzm);

    }

    public void getFindPwdYzm(View view){
        fdUser = etUser.getText().toString().trim();
        fdPwd = etPwd.getText().toString().trim();
        fdYzm = etYzm.getText().toString().trim();
        if (TextUtils.isEmpty(fdUser)){
            System.out.println(fdUser + "-----");
            Toast.makeText(FindPassWord.this,"请先输入手机号码",Toast.LENGTH_SHORT).show();
        }
    }
}
