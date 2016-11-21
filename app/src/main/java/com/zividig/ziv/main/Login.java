package com.zividig.ziv.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.utils.MD5;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.WifiDirectUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * 登录界面
 * Created by Administrator on 2016-06-14.
 */
public class Login extends BaseActivity {

//    private static String LOGIN_URL = "http://api.caowei.name/login";
//    private static String GET_DEVICE_LIST = "http://api.caowei.name/devicelist";

    public static final String ET_USER = "et_user";
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
    private static List<DeviceInfoBean.DevinfoBean> devinfoList;
    private PushManager pushManager;

    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pushManager = PushManager.getInstance(); //获取个推Manager
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
                if (NetworkTypeUtils.getConnectWifiSsid(ZivApp.getInstance()).contains("car_")){
                    WifiDirectUtils.WifiDirect(Login.this,MainActivity.class);
                }else {
                    login();
                }
            }
        });
    }

    /**
     * 登录
     */
    private void login(){
        final String user =  etUser.getText().toString().trim();  //获取账号
        final String password = etPassword.getText().toString().trim();  //获取密码
        final String getuiId = pushManager.getClientid(Login.this);
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)){
            //配置json数据
            JSONObject json = new JSONObject();
            try {
                json.put("username",user);
                json.put("password", MD5.getMD5(password));
                json.put("getuiid",getuiId);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            ToastShow.setToatBytTime(Login.this,"登录中...",500);
            showProgressDialog();

            //发起请求
            RequestParams params = new RequestParams(Urls.LOGIN_URL);
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

//                            ToastShow.showToast(Login.this,"登录成功");
                          //进入主菜单
//                            closeDialog();
                            enterMainActivity();
                            System.out.println("clientId: " + getuiId);
                        }else {
                            System.out.println("登录失败");
                            closeDialog();
                            ToastShow.showToast(Login.this,"账号或密码错误");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    System.out.println("登录请求错误" + ex);
                    closeDialog();
                    ToastShow.showToast(Login.this,"登录请求失败");

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

        RequestParams params = new RequestParams(Urls.GET_DEVICE_LIST + "/" + user);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("设备信息---" + result);
                Gson gson = new Gson();
                deviceInfoBean =  gson.fromJson(result, DeviceInfoBean.class);
                devinfoList = deviceInfoBean.getDevinfo(); //设备列表
                config.edit().putString("device_info",result).apply();
                System.out.println("设备列表长度" + devinfoList.size());


                //如果devid为空则去获取设备列表的设备，否则读取本地缓存的devid
                if (config.getString("devid","").equals("")){
                    devid =   deviceInfoBean.getDevinfo().get(0).getDevid(); //设备ID
                    config.edit().putString("devid",devid).apply();
                }else {
                    devid = config.getString("devid","");
                }

                System.out.println("设备的ID---" + devid);
                if (!devid.isEmpty()){
                    System.out.println("获取设备信息成功" + devid);
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("错误" + ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {}

            @Override
            public void onFinished() {}
        });

    }

    /**
     * 设置设备ID
     * @param currentDevid
     */
    public static void setDevid(String currentDevid){
        devid = currentDevid;
    }

    /**
     * 获取设备ID
     * @return devid
     */
//    public static String getDevId(){
//            return devid;
//    }

    /**
     * 获取设备列表
     */
    public static List<DeviceInfoBean.DevinfoBean> getDeviceList(){
        return devinfoList;
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

    /**
     * 显示登录进度条
     */
    private void showProgressDialog(){
        if (mDialog == null){
            mDialog = LoadingProgressDialog.createLoadingDialog(Login.this,"正在登陆中...");
            mDialog.show();
        }
    }

    /**
     * 关闭登录进度条
     */
    private void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDialog();
    }
}
