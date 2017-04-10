package com.zbar.lib;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class ResultActivity extends Activity {

    private TextView mResultText;
    private SharedPreferences spf;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ToastShow.setColor(ResultActivity.this,getResources().getColor(R.color.zXing_black_russian));

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
            result = extras.getString("qrcode");
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
        String code = mResultText.getText().toString();

        //保存二维码并跳转到添加AddDevice
        spf.edit().putString("two_code", code).apply();
        Intent intent = new Intent();
        intent.setClassName(ResultActivity.this, "com.zividig.ziv.function.AddDevice");
        startActivity(intent);
    }
}
