package com.zividig.ziv.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private EditText etUser;
    private EditText etPassword;
    private Button btLogin;
    private static LoginBean loginBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = (EditText) findViewById(R.id.et_user);
        etPassword = (EditText) findViewById(R.id.et_pwd);
        btLogin = (Button) findViewById(R.id.bt_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        String user = etUser.getText().toString().trim();
        if (!user.isEmpty()){
            RequestParams params = new RequestParams("http://dev.caowei.name/mytest/uploadtest/getdevinfo.php");
            params.addBodyParameter("userid", user);
            x.http().get(params, new Callback.CommonCallback<String>() {

                @Override

                public void onSuccess(String result) {
                    System.out.println("登录成功" + result);
                    Gson gson = new Gson();
                    loginBean =  gson.fromJson(result, LoginBean.class);
//                    loginBean.getDevinfo().get(0).getDevid();
                    System.out.println("登录成功" + loginBean.getDevinfo().get(0).getDevid());

                    //开启获取GPS信息的服务
                    Intent intent = new Intent(Login.this,LocationService.class);
                    intent.putExtra("devid",loginBean.getDevinfo().get(0).getDevid());
                    startService(intent);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(Login.this,"网络异常",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    enterMainActivity();
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
