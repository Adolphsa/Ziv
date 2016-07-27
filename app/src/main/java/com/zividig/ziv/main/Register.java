package com.zividig.ziv.main;

import android.app.Activity;
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
import com.zividig.ziv.utils.MD5;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class Register extends Activity {

    private static String REGISTER_URL = "http://api.caowei.name/user";  //注册的URL
    private static String GET_YZM_URL = "http://api.caowei.name/sms";    //获取验证码的URL

    private EditText etUser;
    private EditText etPwd;
    private EditText etYzm;

    private String user;
    private String pwd;
    private String yzm;
    private String md5Yzm;
    private RegisterMd5Bean registerMd5Bean;

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

    //检查输入的合法性
    public boolean checkValidity(){
        user = etUser.getText().toString().trim();
        pwd = etPwd.getText().toString().trim();
        yzm = etYzm.getText().toString().trim();
        System.out.println(user + "/n" + pwd + "/n" + yzm);
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
            RequestParams params = new RequestParams(GET_YZM_URL + "/" + user);
            System.out.println("获取验证码params:" + params);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("获取验证码成功" + result);
                    Gson gson = new Gson();
                    registerMd5Bean = gson.fromJson(result,RegisterMd5Bean.class);
                    md5Yzm = registerMd5Bean.getCode(); //获取加密的验证码
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("获取验证码错误");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    //注册
    public void register(View view){
        if (checkValidity()){
            yzm = etYzm.getText().toString().trim();
            if (TextUtils.isEmpty(yzm)){
                Toast.makeText(Register.this,"请输入验证码",Toast.LENGTH_SHORT).show();
                return;
            }
            String tempYzm = yzm + "#$";
            System.out.println(tempYzm);

            try {
                System.out.println("md5Yzm:" + md5Yzm + "\n" + MD5.getMD5(tempYzm));
                if (md5Yzm.equals(MD5.getMD5(tempYzm))){
                    System.out.println("相等");
                }else {
                    System.out.println("不相等");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


//        System.out.println("user---" + user + ",pwd---" + pwd);
//        JSONObject json = new JSONObject();
//        try {
//            json.put("username",user);
//            json.put("password",pwd);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestParams params = new RequestParams(REGISTER_URL);
//        params.setAsJsonContent(true);
//        params.setBodyContent(json.toString());
//
//        System.out.println(params.toString());
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                System.out.println("提交---" + result);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                System.out.println("注册错误");
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

}
