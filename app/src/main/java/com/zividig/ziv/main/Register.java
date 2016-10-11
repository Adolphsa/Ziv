package com.zividig.ziv.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.RegisterMd5Bean;
import com.zividig.ziv.customView.CountDownTimer;
import com.zividig.ziv.utils.MD5;
import com.zividig.ziv.utils.Urls;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class Register extends BaseActivity {

//    private static String REGISTER_URL = "http://api.caowei.name/user";  //注册的URL
//    public static String GET_YZM_URL = "http://api.caowei.name/sms";    //获取验证码的URL

    private EditText etUser;
    private EditText etPwd;
    private EditText etYzm;

    private String user;
    private String pwd;
    private String yzm;
    private String md5Yzm;
    private RegisterMd5Bean registerMd5Bean;

    private Button getYzmButton;

    CountDownTimer countDownTimer = new CountDownTimer(60000,1000) {  //按钮倒计时
        @Override
        public void onTick(long millisUntilFinished) {
            getYzmButton.setText(millisUntilFinished/1000 + "秒");
        }

        @Override
        public void onFinish() {
            getYzmButton.setEnabled(true);
            getYzmButton.setText("获取验证码");
        }
    };


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
        getYzmButton = (Button) findViewById(R.id.register_item_bt);
        etUser = (EditText) findViewById(R.id.register_et_user);
        etPwd = (EditText) findViewById(R.id.register_et_pwd);
        etYzm = (EditText) findViewById(R.id.register_et_yzm);
    }

    //检查输入的合法性
    public boolean checkValidity(){
        user = etUser.getText().toString().trim();
        pwd = etPwd.getText().toString().trim();
        yzm = etYzm.getText().toString().trim();
        System.out.println(user + "\n" + pwd + "\n" + yzm);
        if (TextUtils.isEmpty(user) || user.length() != 11){
            System.out.println("usr的长度" + user.length());
            Toast.makeText(Register.this,"请输入手机号码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(pwd)){
            Toast.makeText(Register.this,"请输入密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //获取验证码
    public void getRegisterYzm(View view){
        if (checkValidity()){
            RequestParams params = new RequestParams(Urls.GET_YZM_URL + "/" + user);
            System.out.println("获取注册验证码params:" + params);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("获取注册验证码成功" + result);
                    Gson gson = new Gson();
                    registerMd5Bean = gson.fromJson(result,RegisterMd5Bean.class);
                    md5Yzm = registerMd5Bean.getCode(); //获取加密的验证码
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("获取注册验证码错误");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    getYzmButton.setEnabled(false);
                    countDownTimer.start();
                }
            });
        }
    }

    //注册
    public void register(View view){
        if (checkValidity()){
            user = etUser.getText().toString().trim();
            pwd = etPwd.getText().toString().trim();
            yzm = etYzm.getText().toString().trim();
            if (TextUtils.isEmpty(yzm)){
                Toast.makeText(Register.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                return;
            }
            String tempYzm = yzm + "#$";
            System.out.println(tempYzm);

            try {
                System.out.println("md5Yzm:" + md5Yzm + "\n" + MD5.getMD5(tempYzm));
                if (md5Yzm.equals(MD5.getMD5(tempYzm))){  //判断MD5值是否相等
                    System.out.println("相等");
                    System.out.println("user---" + user + ",pwd---" + pwd);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("username",user);
                        json.put("password",MD5.getMD5(pwd));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestParams params = new RequestParams(Urls.REGISTER_URL);
                    params.setAsJsonContent(true);
                    params.setBodyContent(json.toString());

                    System.out.println(params.toString());
                    x.http().post(params, new Callback.CommonCallback<String>() {

                        @Override
                        public void onSuccess(String result) {
                            System.out.println("提交成功---" + result);
                            try {
                                JSONObject json = new JSONObject(result);
                                boolean issuccess = json.getBoolean("issuccess");
                                if (issuccess){  //注册成功
                                    Toast.makeText(Register.this,"注册成功",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this,Login.class));
                                    finish();

                                }else {
                                    Toast.makeText(Register.this,"注册失败",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            System.out.println("注册错误");
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });

                }else {
                    System.out.println("不相等");
                    Toast.makeText(Register.this,"验证码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}
