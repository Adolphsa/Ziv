package com.zividig.ziv.function;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
import com.zividig.ziv.utils.NetworkTypeUtils;
import com.zividig.ziv.utils.SignatureUtils;
import com.zividig.ziv.utils.ToastShow;
import com.zividig.ziv.utils.Urls;
import com.zividig.ziv.utils.UtcTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import static com.zividig.ziv.utils.SignatureUtils.SIGNATURE_TOKEN;

public class AddDevice extends BaseActivity {

    private TextView deviceId;
    private TextView tvTwoCode;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        spf = getSharedPreferences("config", MODE_PRIVATE);


        initView();
    }

    private void initView() {
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

                if (NetworkTypeUtils.getNetworkType(AddDevice.this).equals(NetworkTypeUtils.WIFI)) {
                    System.out.println("连接设备");
                    RequestParams params = new RequestParams(Urls.GET_DEVICE_INFO_WIFI);
                    x.http().get(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            System.out.println("wifi直连" + result);
                            try {
                                JSONObject json = new JSONObject(result);
                                String devid = json.getString("devid");
                                if (!devid.equals("")) {
                                    startActivity(new Intent(AddDevice.this, CaptureActivity.class));
                                } else {
                                    if (!AddDevice.this.isFinishing()){
                                        DialogUtils.showPrompt(AddDevice.this, "提示", "获取devid失败", "确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (!AddDevice.this.isFinishing()){
                                    DialogUtils.showPrompt(AddDevice.this, "提示", "解析数据失败", "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                }

                            }


                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            System.out.println("wifi直连错误" + ex);
                            ToastShow.showToast(AddDevice.this, "请连接设备WIFI");

                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                } else {
                    ToastShow.showToast(AddDevice.this, "请连接设备WIFI");
                }
            }
        });

        //显示设备ID
        deviceId = (TextView) findViewById(R.id.tv_device_id);

        String devid = spf.getString("devid", "");
        if (!devid.equals("")) {
            deviceId.setText(devid);
        }

        //显示二维码
        String twoCode = spf.getString("two_code", "");
        System.out.println("重新创建");
        tvTwoCode = (TextView) findViewById(R.id.tv_two_code);
        if (!TextUtils.isEmpty(twoCode)) {
            tvTwoCode.setText(twoCode);
        }
    }

    //提交用户名和二维码   设备绑定
    public void BindDevice(View v) {
        String userName = spf.getString(Login.ET_USER, "");
        String code = spf.getString("two_code", "");
        System.out.println("二维码是：" + code);
        if (code.isEmpty()) {
            ToastShow.showToast(AddDevice.this, "请先添加二维码");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("username", userName);
            json.put("code", code);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                userName,
                code,
                SignatureUtils.token);
        RequestParams params = HttpParamsUtils.setParams(Urls.URL_BIND_DEVICE, timestamp, noncestr, signature);
        params.setAsJsonContent(true);
//        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("绑定设备---" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_200) {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备成功,请重新登录", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除已保存的二维码
                                spf.edit().remove("two_code").apply();
                                startActivity(new Intent(AddDevice.this, Login.class));
                            }
                        });
                    } else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_403) {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，无权限，已被他人绑定", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_404) {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，用户不存在或者设备不存在", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    }else {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，异常错误", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (!AddDevice.this.isFinishing()) {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，数据解析失败", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("绑定设备--" + ex);
                if (!AddDevice.this.isFinishing()) {
                    DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败,网络错误,请检查网络", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }

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
    public void changeNetwork(View view) {
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

}
