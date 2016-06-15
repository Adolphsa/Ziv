package com.zividig.ziv.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.LoginBean;
import com.zividig.ziv.service.LocationService;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 登录界面
 * Created by Administrator on 2016-06-14.
 */
public class Login extends Activity {

    private static final String ET_USER = "et_user";
    private static final String CB_USER = "cb_user";
    private static final String CB_PWD = "cb_pwd";

    private EditText etUser;
    private EditText etPassword;
    private Button btLogin;
    private static LoginBean loginBean;
    private SharedPreferences config;
    private CheckBox cbUser;
    private CheckBox cbPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        config = getSharedPreferences("config", MODE_PRIVATE);

       initView();
    }

    /**
     * 初始化控件
     */
    private void initView(){

        etUser = (EditText) findViewById(R.id.et_user);
        etPassword = (EditText) findViewById(R.id.et_pwd);
        cbUser = (CheckBox) findViewById(R.id.cb_user);
        cbPwd = (CheckBox) findViewById(R.id.cb_password);
        btLogin = (Button) findViewById(R.id.bt_login);

        String configUser = config.getString(ET_USER, "");
        boolean configCbUser = this.config.getBoolean(CB_USER, false);
        if (configCbUser){
            cbUser.setChecked(true);  //为真就设置成勾选
            if (!configUser.isEmpty()){
                etUser.setText(configUser);
            }
        }else {
            cbUser.setChecked(false);
        }

        cbUser.setOnClickListener(new View.OnClickListener() { //记住用户
            @Override
            public void onClick(View v) {
                System.out.println("cbUser被点击了");
                if (cbUser.isChecked()){
                    System.out.println("勾选");
                    Login.this.config.edit().putBoolean(CB_USER,true).apply();
                }else {
                    System.out.println("非勾选");
                    Login.this.config.edit().putBoolean(CB_USER,false).apply();
                }
            }
        });
        cbPwd.setOnClickListener(new View.OnClickListener() { //记住密码
            @Override
            public void onClick(View v) {
                System.out.println("cbPwd被点击了");
                if (cbPwd.isChecked()){

                    Login.this.config.edit().putBoolean(CB_PWD,true).apply();
                }else {
                    Login.this.config.edit().putBoolean(CB_PWD,false).apply();
                }
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
    private void login(){
        final String user = etUser.getText().toString().trim();
        if (!user.isEmpty()){
            RequestParams params = new RequestParams("http://dev.caowei.name/mytest/uploadtest/getdevinfo.php");
            params.addBodyParameter("userid", user);
            x.http().get(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {

                        Gson gson = new Gson();
                        loginBean =  gson.fromJson(result, LoginBean.class);
                        String devid =  loginBean.getDevinfo().get(0).getDevid();

                        if (!devid.isEmpty()){

                            System.out.println("登录成功" + loginBean.getDevinfo().get(0).getDevid());

                            //开启获取GPS信息的服务
                            Intent intent = new Intent(Login.this,LocationService.class);
                            intent.putExtra("devid",loginBean.getDevinfo().get(0).getDevid());
                            startService(intent);
                        }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(Login.this,"请输入正确的用户名",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    if (!loginBean.getDevinfo().get(0).getDevid().isEmpty()){
                        //保存用户账号
                        config.edit().putString(ET_USER,user).apply();
                        enterMainActivity();
                    }else {
                        Toast.makeText(Login.this,"请输入正确的用户名",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else {
            Toast.makeText(Login.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进入登录界面
     */
    private void enterMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public static String getDevId(){
        return loginBean.getDevinfo().get(0).getDevid();
    }
}
