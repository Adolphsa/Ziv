package com.zividig.ziv.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zividig.ziv.R;
import com.zividig.ziv.bean.RegisterMd5Bean;
import com.zividig.ziv.customView.CountDownTimer;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.MD5;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class FindPassWord extends BaseActivity {

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

            JSONObject json = new JSONObject();
            try {
                json.put("phonenum",fdUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //计算signature
            String timestamp = UtcTimeUtils.getTimestamp();
            String noncestr = HttpParamsUtils.getRandomString(10);
            String signature = SignatureUtils.getSinnature(timestamp,
                    noncestr,
                    Urls.APP_KEY,
                    fdUser);

            RequestParams params = HttpParamsUtils.setParams(Urls.GET_YZM_URL,timestamp,noncestr,signature);
            params.setBodyContent(json.toString());
            System.out.println("获取注册验证码params:" + params);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("获取注册验证码成功" + result);
                    try {
                        JSONObject json = new JSONObject(result);
                        int status = json.getInt("status");
                        if (status == Urls.STATUS_CODE_200){
                            System.out.println("获取注册验证码成功" + result);
                            md5Yzm = json.getString("code");
                        }else {
                            ToastShow.showToast(FindPassWord.this,"获取验证码失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
            String tempYzm = fdYzm + "#$" + fdUser;
            System.out.println(tempYzm);
            try {
                if (md5Yzm.equals(MD5.getMD5(tempYzm))){
                    System.out.println("相等");

                    JSONObject json = new JSONObject();
                    try {
                        json.put("username",fdUser);
                        json.put("password",MD5.getMD5(fdPwd));
                        json.put("code",fdYzm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //计算signature
                    String timestamp = UtcTimeUtils.getTimestamp();
                    String noncestr = HttpParamsUtils.getRandomString(10);
                    String signature = SignatureUtils.getSinnature(timestamp,
                            noncestr,
                            Urls.APP_KEY,
                            fdUser,
                            MD5.getMD5(fdPwd),
                            fdYzm);

                    RequestParams params = HttpParamsUtils.setParams(Urls.RESET_PASSWORD_URL,timestamp,noncestr,signature);
                    params.setBodyContent(json.toString());
                    //开始请求
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            System.out.println("重置密码---" + result);

                            try {
                                JSONObject json = new JSONObject(result);
                                int status = json.getInt("status");
                                if (status == Urls.STATUS_CODE_200){
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
                        public void onCancelled(CancelledException cex) {}

                        @Override
                        public void onFinished() {}
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
