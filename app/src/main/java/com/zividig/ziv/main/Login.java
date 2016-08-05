package com.zividig.ziv.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.service.LocationService;
import com.zividig.ziv.utils.MD5;
import com.zividig.ziv.utils.ToastShow;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 登录界面
 * Created by Administrator on 2016-06-14.
 */
public class Login extends Activity {

    private static String LOGIN_URL = "http://api.caowei.name/login";

    private static final String ET_USER = "et_user";
    private static final String ET_PWD = "et_pwd";
    private static final String CB_USER = "cb_user";
    private static final String CB_PWD = "cb_pwd";

    private EditText etUser;
    private EditText etPassword;
    private Button btLogin;
    private SharedPreferences config;
    private CheckBox cbUser;
    private CheckBox cbPwd;

    private DeviceInfoBean deviceInfoBean;
    private static String devid;

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

        //获取保存的账号密码
        String configUser = config.getString(ET_USER, "");
        String configPwd = config.getString(ET_PWD,"");
        boolean configCbUser = this.config.getBoolean(CB_USER, false);
        boolean configCbPwd = config.getBoolean(CB_PWD,false);
        //根据记住用户名是否勾选来填充用户名
        if (configCbUser){
            cbUser.setChecked(true);
            if (!configUser.isEmpty()){
                etUser.setText(configUser);
            }
        }else {
            cbUser.setChecked(false);
        }
        //根据记住密码是否勾选来填充密码
        if (configCbPwd){
            cbPwd.setChecked(true);
            if (!configPwd.isEmpty()){
                etPassword.setText(configPwd);
            }
        }else {
            cbPwd.setChecked(false);
        }
        //记住用户名勾选框
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

        //记住密码勾选框
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

    /**
     * 登录
     */
    private void login(){
        final String user = etUser.getText().toString().trim();  //获取账号
        final String password = etPassword.getText().toString().trim();  //获取密码
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
            //配置json数据
            JSONObject json = new JSONObject();
            try {
                json.put("username",user);
                json.put("password", MD5.getMD5(password));
            } catch (Exception e) {
                e.printStackTrace();
            }

            ToastShow.setToatBytTime(Login.this,"登录中...",500);

            //发起请求
            RequestParams params = new RequestParams(LOGIN_URL);
            params.setAsJsonContent(true);
            params.setBodyContent(json.toString());
            x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    System.out.println("登录" + result);
                    try {
                        JSONObject json = new JSONObject(result);
                        String isSuccess = json.getString("loginstatus");
                        if (isSuccess.equals("success")){
                            System.out.println("登录成功");
                            //保存账号密码
                            config.edit().putString(ET_USER,user).apply();
                            config.edit().putString(ET_PWD,password).apply();

                            ToastShow.showToast(Login.this,"登录成功");
                            //进入主菜单
                            enterMainActivity();
                            System.out.println("啦啦啦");
                        }else {
                            ToastShow.showToast(Login.this,"账号或密码错误");
                            System.out.println("登录失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("登录请求错误" + ex);
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    //获取设备信息
                    getDeviceInfo(user);

                }
            });
        }else {

            ToastShow.showToast(Login.this,"用户名或密码不能为空");
        }
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo(String user){
        System.out.println("执行获取设备信息");
        RequestParams params = new RequestParams("http://dev.caowei.name/mytest/uploadtest/getdevinfo.php");
        params.addBodyParameter("userid", user);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                deviceInfoBean =  gson.fromJson(result, DeviceInfoBean.class);
                devid =   deviceInfoBean.getDevinfo().get(0).getDevid();
                System.out.println("设备的ID---" + devid);
                if (!devid.isEmpty()){

                    System.out.println("获取设备信息成功" + deviceInfoBean.getDevinfo().get(0).getDevid());

                    //开启获取GPS信息的服务
                    Intent intent = new Intent(Login.this,LocationService.class);
                    intent.putExtra("devid", deviceInfoBean.getDevinfo().get(0).getDevid());
                    startService(intent);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {
            }
        });

    }

    /**
     * 获取设备ID
     * @return devid
     */
    public static String getDevId(){
            return devid;
    }

    /**
     * 注册账号
     */
    public void loginRegister(View view){
            startActivity(new Intent(Login.this,Register.class));
    }

    /**
     * 找回密码
     * @param view
     */
    public void loginFindPassWord(View view){
        startActivity(new Intent(Login.this,FindPassWord.class));
    }

    /**
     * 进入登录界面
     */
    private void enterMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
