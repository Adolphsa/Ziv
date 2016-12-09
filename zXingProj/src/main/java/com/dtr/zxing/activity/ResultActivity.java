package com.dtr.zxing.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtr.zxing.R;
import com.dtr.zxing.utils.ToastShow;
import com.dtr.zxing.utils.TwoCodeDialogUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class ResultActivity extends Activity {

    private static String URL_SET_TWO_CODE = "http://192.168.1.1/api/setqrcode";

    private TextView mResultText;
    private SharedPreferences spf;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        spf = getSharedPreferences("config", MODE_PRIVATE);

        Bundle extras = getIntent().getExtras();

        Button back = (Button) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mResultText = (TextView) findViewById(R.id.result_text);

        if (null != extras) {
            result = extras.getString("result");
            mResultText.setText(result);
        }
    }

    /**
     * 设置二维码到设备
     *
     * @param view
     */
    public void addTwoCode(View view) {
        ToastShow.setToatBytTime(ResultActivity.this, "请等待...", 500);
        RequestParams params = new RequestParams(URL_SET_TWO_CODE);
        params.addParameter("code", result);

        System.out.println("二维码请求的params:  " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    final String code = json.getString("code");
                    if (!code.equals("")) {
                        TwoCodeDialogUtils.showPrompt(ResultActivity.this, "提示", "设置成功", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存二维码并跳转到添加AddDevice
                                spf.edit().putString("two_code", code).apply();
                                Intent intent = new Intent();
                                intent.setClassName(ResultActivity.this, "com.zividig.ziv.function.AddDevice");
                                startActivity(intent);
                            }
                        });

                    }else {
                        TwoCodeDialogUtils.showPrompt(ResultActivity.this, "提示", "设置失败，获取二维码失败，请重新设置", "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    TwoCodeDialogUtils.showPrompt(ResultActivity.this, "提示", "设置失败，解析数据失败，请检测网络连接", "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("二维码设置错误" + ex);
                ToastShow.showToast(ResultActivity.this, "设置失败，请检查是否为设备WIFI");
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
