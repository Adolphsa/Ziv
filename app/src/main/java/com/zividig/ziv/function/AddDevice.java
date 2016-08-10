package com.zividig.ziv.function;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.ToastShow;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class AddDevice extends Activity {

    private static String URL_BIND_DEVICE = "http://api.caowei.name/devicebind";

    private TextView deviceId;
    private TextView tvTwoCode;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        spf = getSharedPreferences("config",MODE_PRIVATE);


        initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("添加设备");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置二维码
        Button setTwoCode = (Button) findViewById(R.id.bt_set_two_code);
        setTwoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkTypeUtils.getNetworkType(AddDevice.this).equals(NetworkTypeUtils.WIFI)){
                    System.out.println("连接设备");
                    RequestParams params = new RequestParams("http://192.168.1.1/api/getdevinfo");
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            System.out.println("wifi直连" + result);
                            if (!result.isEmpty()){
                                startActivity(new Intent(AddDevice.this, CaptureActivity.class));
                            }

                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            System.out.println("wifi直连错误" + ex);
                            ToastShow.showToast(AddDevice.this,"请连接设备WIFI");

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }else {
                    ToastShow.showToast(AddDevice.this,"请连接设备WIFI");
                }
            }
        });

        //显示设备ID
        deviceId = (TextView) findViewById(R.id.tv_device_id);
        String devId = Login.getDevId();
        if (devId != null){
            deviceId.setText(Login.getDevId());
        }

        //显示二维码
        String twoCode = spf.getString("two_code", "");
        System.out.println("重新创建");
        tvTwoCode = (TextView) findViewById(R.id.tv_two_code);
        if (!TextUtils.isEmpty(twoCode)){
            tvTwoCode.setText(twoCode);
        }
    }

    //提交用户名和二维码
    public void BindDevice(View v){
        String userName = spf.getString(Login.ET_USER,"");
        String code = spf.getString("two_code","");
        if (code.isEmpty()){
            ToastShow.showToast(AddDevice.this,"请先添加二维码");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("username",userName);
            json.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(URL_BIND_DEVICE);
        params.setAsJsonContent(true);
        params.setBodyContent(json.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("绑定设备---" + result);
                if (!result.isEmpty()){
                    ToastShow.showToast(AddDevice.this,"绑定设备成功");
                }else {
                    ToastShow.showToast(AddDevice.this,"绑定设备失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("绑定设备--" + ex);
                ToastShow.showToast(AddDevice.this,"绑定设备失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //切换网络
    public void changeNetwork(View view){
//      startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

}
