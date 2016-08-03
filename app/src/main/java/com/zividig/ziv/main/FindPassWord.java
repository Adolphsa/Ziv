package com.zividig.ziv.main;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class FindPassWord extends Activity {

    private static String RESET_PWD = "http://api.caowei.name/user";

    private EditText etUser;
    private EditText etPwd;
    private EditText etYzm;

    private String fdUser;
    private String fdPwd;
    private String fdYzm;
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
        getYzmButton = (Button) findViewById(R.id.findPwd_item_bt);
        etUser = (EditText) findViewById(R.id.findPwd_et_user);
        etPwd = (EditText) findViewById(R.id.findPwd_et_pwd);
        etYzm = (EditText) findViewById(R.id.findPwd_et_yzm);

    }

    //检查输入数据的合法性
    private boolean checkValidity(){
        fdUser = etUser.getText().toString().trim();
        fdPwd = etPwd.getText().toString().trim();
        System.out.println(fdUser + "\n" + fdPwd + "\n" + fdYzm);
        if (TextUtils.isEmpty(fdUser) || fdUser.length() != 11){
            Toast.makeText(FindPassWord.this,"请输入手机号码",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(fdPwd)){
            Toast.makeText(FindPassWord.this,"请输入新密码",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //获取验证码
    public void getFindPwdYzm(View view){
        if (checkValidity()){
            RequestParams params = new RequestParams(Register.GET_YZM_URL + "/" + fdUser);
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

    //重置密码
    public void resetPassword(View view) throws JSONException {
        if (checkValidity()){
            fdUser = etUser.getText().toString().trim();
            fdPwd = etPwd.getText().toString().trim();
            fdYzm = etYzm.getText().toString().trim();
            if (TextUtils.isEmpty(fdYzm)){
                Toast.makeText(FindPassWord.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                return;
            }
            String tempYzm = fdYzm + "#$";
            System.out.println(tempYzm);
            try {
                if (md5Yzm.equals(MD5.getMD5(tempYzm))){
                    System.out.println("相等");
                    //放入请求的json数据
                    JSONObject json = new JSONObject();
                    json.put("username",fdUser);
                    json.put("password",MD5.getMD5(fdPwd));
                    //配置请求参数
                    RequestParams params = new RequestParams(RESET_PWD + "/" + fdUser);
                    params.setAsJsonContent(true);
                    params.setBodyContent(json.toString());
                    //开始请求
                    x.http().request(HttpMethod.PUT, params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            System.out.println("重置密码成功");
                            try {
                                JSONObject json = new JSONObject(result);
                                boolean isSuccess = json.getBoolean("issuccess");
                                if (isSuccess){
                                    Toast.makeText(FindPassWord.this,"重置密码成功",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(FindPassWord.this,Login.class));
                                    finish();
                                }else {
                                    Toast.makeText(FindPassWord.this,"重置密码失败",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            System.out.println("重置密码错误");
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
                    Toast.makeText(FindPassWord.this,"验证码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
