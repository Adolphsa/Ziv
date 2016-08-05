package com.dtr.zxing.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dtr.zxing.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class ResultActivity extends Activity {

    private static String URL_SET_TWO_CODE = "http://192.168.1.1/api/setqrcode";

    private TextView mResultText;
    private SharedPreferences spf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        spf = getSharedPreferences("config",MODE_PRIVATE);

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
            String result = extras.getString("result");
            mResultText.setText(result);
        }
    }

    /**
     * 设置二维码到设备
     * @param view
     */
    public void addTwoCode(View view){

        RequestParams params = new RequestParams(URL_SET_TWO_CODE);
        params.addParameter("code", "12345678");
        params.addParameter("username", "13480995624");

        System.out.println("二维码请求的params:  " + params);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("二维码设置成功" + result);
                if (!result.isEmpty()){
                    try {
                        JSONObject json = new JSONObject(result);
                        String code = json.getString("code");
                        //保存二维码
                        spf.edit().putString("two_code",code).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("二维码设置错误" + ex);
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
