package com.zividig.ziv.function;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.main.Login;
import com.zividig.ziv.main.MainActivity;
import com.zividig.ziv.utils.DialogUtils;
import com.zividig.ziv.utils.HttpParamsUtils;
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
    private Login mLogin;

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 100;

    private static final int REQUEST_CODE_SETTING = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        spf = getSharedPreferences("config", MODE_PRIVATE);
        mLogin = new Login();

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
                startActivity(new Intent(AddDevice.this, CaptureActivity.class));
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
        System.out.println("设备绑定的签名---" + signature);

        RequestParams params = HttpParamsUtils.setParams(Urls.URL_BIND_DEVICE, timestamp, noncestr, signature);
        params.setAsJsonContent(true);
        params.setBodyContent(json.toString());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("绑定设备---" + result);
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_200) {
                        final String username = json.getString("username");
                        System.out.println("username---" + username);

                        final String devid = json.getString("deviceid");
                        System.out.println("devid---" + devid);
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备成功,请设置车牌号和别名", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除已保存的二维码
                                spf.edit().remove("two_code").apply();
                                //请设置车牌号和别名
                                getFormDialog(username,devid);

//                                startActivity(new Intent(AddDevice.this, Login.class));
                            }
                        });
                    } else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_403) {
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败,无权限，token错误", "确定", new DialogInterface.OnClickListener() {
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

                    }else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_405){
                        System.out.println("绑定结果---" + result);
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，二维码不正确", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_406){
                        System.out.println("绑定结果---" + result);
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，已被他人绑定", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }else if (!AddDevice.this.isFinishing() && status == Urls.STATUS_CODE_500){
                        System.out.println("绑定结果---" + result);
                        DialogUtils.showPrompt(AddDevice.this, "提示", "绑定设备失败，数据库错误", "确定", new DialogInterface.OnClickListener() {
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

    /**
     * 二维码设置成功后弹出的表单对话框
     * @param usrname   用户名
     * @param devid     设备Id
     */
    private void getFormDialog(final String usrname, final String devid){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("设置车牌号和别名(可选)");//设置标题
        View view = LayoutInflater.from(this).inflate(R.layout.layout_form_dialog,null);
        final EditText etCarid = (EditText) view.findViewById(R.id.et_carid);//车牌号
        final EditText etAlias = (EditText) view.findViewById(R.id.et_alias);//别名
        builder.setView(view);//给对话框设置布局
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //点击确定按钮的操作
                System.out.println("确定");
                String carid = etCarid.getText().toString().trim();
                if (carid == null){
                    carid = "";
                }
                System.out.println("carid---" + carid);
                String alias = etAlias.getText().toString().trim();
                if (alias == null){
                    alias = "";
                }
                setCarId(usrname,devid,carid,alias);
                System.out.println("alias---" + alias);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("设置车牌号和别名取消");
                //获取设备列表信息
                mLogin.getDeviceInfo(usrname,spf);
                startActivity(new Intent(AddDevice.this, MainActivity.class));

            }
        });
        builder.show();
    }

    /**
     * 设置车牌号和别名
     * @param username  用户名
     * @param devid     设备ID
     * @param carid     车牌号
     * @param alias     别名
     */
    public void setCarId(final String username, final String devid, String carid, String alias){
        System.out.println("setCarId");
        //配置json数据
        final JSONObject json = new JSONObject();
        try {
            json.put("username",username);
            json.put("devid", devid);
            json.put("carid",carid);
            json.put("alias",alias);
            json.put(SIGNATURE_TOKEN, SignatureUtils.token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //计算signature
        String timestamp = UtcTimeUtils.getTimestamp();
        String noncestr = HttpParamsUtils.getRandomString(10);
        String signature = SignatureUtils.getSinnature(timestamp,
                noncestr,
                Urls.APP_KEY,
                username,
                devid,
                carid,
                alias,
                SignatureUtils.token);

        RequestParams params = HttpParamsUtils.setParams(Urls.SETTING_CARID,timestamp,noncestr,signature);
        params.setBodyContent(json.toString());
        System.out.println("params---" + params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    int status = json.getInt("status");
                    if (200 == status){
                        if (!AddDevice.this.isFinishing()){
                            DialogUtils.showPrompt(AddDevice.this, "提示", "设置车牌号和别名成功", "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //获取设备列表信息
                                    mLogin.getDeviceInfo(username,spf);
                                    spf.edit().putString("devid",devid).apply();
                                    startActivity(new Intent(AddDevice.this, MainActivity.class));
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("设置车牌号错误");
                if (!AddDevice.this.isFinishing()) {
                    DialogUtils.showPrompt(AddDevice.this, "提示", "设置车牌号失败", "确定", new DialogInterface.OnClickListener() {
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
}
