package com.zividig.ziv.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.zividig.ziv.R;
import com.zividig.ziv.bean.DeviceInfoBean;
import com.zividig.ziv.bean.Users;
import com.zividig.ziv.bean.UsersDao;
import com.zividig.ziv.customView.DropEditText;
import com.zividig.ziv.customView.LoadingProgressDialog;
import com.zividig.ziv.getui.GetuiPushService;
import com.zividig.ziv.rxjava.ZivApiManage;
import com.zividig.ziv.rxjava.model.LoginBody;
import com.zividig.ziv.rxjava.model.LoginResponse;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.JsonUtils;
import com.zividig.ziv.utils.MD5;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;
import static com.zividig.ziv.utils.SignatureUtils.token;

/**
 * 登录界面
 * Created by Administrator on 2016-06-14.
 */
public class Login extends BaseActivity {

    private static final String TAG = "Login ";

    public static final String ET_USER = "et_user";
    private static final String ET_PWD = "et_pwd";
    private static final String CB_USER = "cb_user";
    private static final String CB_PWD = "cb_pwd";

    private DropEditText etUser;
    private EditText etPassword;
    private Button btLogin;
    private SharedPreferences config;
    private CheckBox cbUser;
    private CheckBox cbPwd;
    private String user;
    private String password;

    private DeviceInfoBean deviceInfoBean;
    private static String devid;
    private static String carid;
    private static List<DeviceInfoBean.DevinfoBean> devinfoList;

    private static final int REQUEST_PERMISSION = 0;

    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = GetuiPushService.class;
    private int getuiCount = 0;

    private Dialog mDialog;

    private UsersDao mUsersDao;
    private ArrayAdapter<String> adapter;
    private List<Users> mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        config = getSharedPreferences("config", MODE_PRIVATE);
        mUsersDao = ZivApp.getInstance().getDaoSession().getUsersDao();

        initView();
        initGeTui();
    }

    private void initGeTui(){
        Log.d(TAG, "initializing sdk...");

        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission();
        } else {
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            } else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String getCid() {
        String cid = PushManager.getInstance().getClientid(this);
        Log.d(TAG, "当前应用的cid = " + cid);
        return cid;
    }
    /**
     * 初始化控件
     */
    private void initView(){

        etUser = (DropEditText) findViewById(R.id.et_user);
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
//                System.out.println("cbPwd被点击了");
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
                rxLogin();
            }
        });

        List<String> strings = new ArrayList<>();
        mUsersList = mUsersDao.loadAll();
        if (mUsersList.size() > 0){
            for (Users user : mUsersList){
                strings.add(user.getUsername());
            }
        }
        //从数据库查询数据填充
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
        etUser.setAdapter(adapter);
        etUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("被点击了");
                Users dbUser = mUsersList.get(position);
                etUser.setText(dbUser.getUsername());
                etPassword.setText(dbUser.getPassword());
                etUser.setDismiss();
            }
        });
    }

    //登录
    private void rxLogin(){

        //显示progress
        showProgressDialog();

        user =  etUser.getText().toString().trim();  //获取账号
        if (TextUtils.isEmpty(user)){
            ToastShow.showToast(Login.this,"用户名不能为空");
            return;
        }
        password = etPassword.getText().toString().trim();  //获取密码
        if (TextUtils.isEmpty(password)){
            ToastShow.showToast(Login.this,"密码不能为空");
            return;
        }
        String getuiId = getCid();
        System.out.println("clientID---" + getuiId);

        if (getuiId == null){
            ToastShow.showToast(this,"无法获取clientId,请退出应用重新登录");
            return;
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                user,
                MD5.getMD5(password),
                getuiId);

        //配置请求头
        Map<String, String> options = new HashMap<>();
        options.put(SignatureUtils.SIGNATURE_APP_KEY, Urls.APP_KEY);
        options.put(SignatureUtils.SIGNATURE_TIMESTAMP, timestamp);
        options.put(SignatureUtils.SIGNATURE_NONCESTTR, noncestr);
        options.put(SignatureUtils.SIGNATURE_STRING, signature);

        //配置请求体
        LoginBody bodyObject = new LoginBody();
        bodyObject.userName = user;
        bodyObject.password = MD5.getMD5(password);
        bodyObject.getuiId = getuiId;
        String loginBody = JsonUtils.serialize(bodyObject);
        RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginBody);

        ZivApiManage.getInstance().getZivApiService()
                .doLogin(options,jsonBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LoginResponse>() {
                    @Override
                    public void call(LoginResponse loginResponse) {
                        handleLoginResponse(loginResponse);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("登录请求错误" + throwable.getMessage());
                        closeDialog();
                        ToastShow.showToast(Login.this,"登录请求失败");
                    }
                });
    }


    //登录后的处理
    private void handleLoginResponse(LoginResponse loginResponse){

        int status = loginResponse.getStatus();
        if ( Urls.STATUS_CODE_200 == status){

            System.out.println("登录状态---" + loginResponse.toString());

            userNameIsChange(); //判断账号是否改换

            //保存账号密码
            config.edit().putString(ET_USER,user).apply();
            config.edit().putString(ET_PWD,password).apply();

            //保存在数据库，如果查询不到用户名就添加，否则值修改密码
            Users users = mUsersDao.queryBuilder()
                    .where(UsersDao.Properties.Username.eq(user))
                    .build().unique();
            if (users == null){
                Users users1 = new Users(null,user,password);
                mUsersDao.insert(users1);
                System.out.println("添加账号密码到数据库");
            }else {
                users.setPassword(password);
                mUsersDao.update(users);
                System.out.println("只修改密码");
            }

            //保存token
            token = loginResponse.getToken();
            config.edit().putString("token", token).apply();
            System.out.println("token---" + token);

            //保存推送免打扰开关状态
            String alarmSwitchState = loginResponse.getAlarmStatus();
            config.edit().putString("alarm_status",alarmSwitchState).apply();

            //进入主界面
            enterMainActivity();
            //获取设备信息
            getDeviceInfo();

        }else if (status == Urls.STATUS_CODE_403){
            System.out.println("登录失败");
            closeDialog();
            ToastShow.showToast(Login.this,"账号或密码错误");
        }else if (status == Urls.STATUS_CODE_404){
            closeDialog();
            ToastShow.showToast(Login.this,"手机号不存在");
        }else if (status == Urls.STATUS_CODE_500){
            closeDialog();
            ToastShow.showToast(Login.this,"数据库不存在");
        }
    }

    /**
     * 获取设备信息
     */
    public void getDeviceInfo(){
        System.out.println("执行获取设备信息");

        if (TextUtils.isEmpty(user)){
            System.out.println("获取设备时user为空");
            return;
        }

        if (TextUtils.isEmpty(token)){
            System.out.println("获取设备时token为空");
            return;
        }

        //配置json数据
        JSONObject json = new JSONObject();
        try {
            json.put("username",user);
            json.put(SIGNATURE_TOKEN, token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                user,
                token);

        RequestParams params = HttpParamsUtils.setParams(Urls.GET_DEVICE_LIST,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                System.out.println("设备信息---" + result);
                Gson gson = new Gson();
                deviceInfoBean =  gson.fromJson(result, DeviceInfoBean.class);
                int status = deviceInfoBean.getStatus();

                    if (status == Urls.STATUS_CODE_200){
                        devinfoList = deviceInfoBean.getDevinfo(); //设备列表
                        if (devinfoList.size() == 0){
                            config.edit().remove("devid").apply();
                            config.edit().remove("alarm_state").apply();
                            config.edit().remove("device_info").apply();
                        }else {
                            config.edit().putString("device_info",result).apply();
                            System.out.println("设备列表长度" + devinfoList.size());

                            //如果devid为空则去获取设备列表的设备，否则读取本地缓存的devid
                            if (config.getString("devid","").equals("")){
                                if (devinfoList.size() > 0){
                                    devid =   deviceInfoBean.getDevinfo().get(0).getDevid(); //设备ID
                                    config.edit().putString("devid",devid).apply();
                                    System.out.println("devid为空的时候");
                                }

                            }else {
                                System.out.println("devid有缓存");
                                devid = config.getString("devid","");
                                String tmp;
                                int count = 0;
                                for (DeviceInfoBean.DevinfoBean devinfoBean: devinfoList){
                                    tmp = devinfoBean.getDevid();
                                    if (devid.equals(tmp)){
                                        System.out.println("找到相等的");
                                        break;
                                    }else {
                                        count++;
                                        System.out.println("没有相等的");
                                        if (count == devinfoList.size()){
                                            if (devinfoList.size() > 0){
                                                devid =   deviceInfoBean.getDevinfo().get(0).getDevid(); //设备ID
                                                config.edit().putString("devid",devid).apply();
                                            }else {
                                                config.edit().putString("devid","").apply();
                                            }

                                        }
                                    }
                                }
                            }
                        }

                    }else if (status == Urls.STATUS_CODE_404){
                        System.out.println("用户不存在");
                    }

                System.out.println("设备的ID---" + devid);
                if (devid != null && devid.length() != 0){
                    System.out.println("获取设备信息成功");
                }else {
                    System.out.println("获取设备信息失败");
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
     * 获取用户名
     * @return
     */
    public String getUserName(){
        String userName = etUser.getText().toString().trim();
        System.out.println(userName);
        return userName;
    }

    /**
     * 检测账号是否更换
     */
    public void userNameIsChange(){
        //换账号之后删除devid
        String userName = config.getString(ET_USER,"");
        String currentUserName = getUserName();
        if (!currentUserName.equals(userName)){
            System.out.println("换账号了");
            System.out.println("清除一些数据");
            config.edit().remove("devid").apply();
            config.edit().remove("alarm_state").apply();
            config.edit().remove("device_info").apply();
        }
    }

    /**
     * 设置设备ID
     * @param currentDevid
     */
    public static void setDevid(String currentDevid){
        devid = currentDevid;
    }

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
            mDialog = LoadingProgressDialog.createLoadingDialog(Login.this,"正在登录中...",true,false,null);
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
