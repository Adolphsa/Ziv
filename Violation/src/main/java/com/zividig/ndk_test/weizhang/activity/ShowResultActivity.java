package com.zividig.ndk_test.weizhang.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zividig.ndk_test.R;
import com.zividig.ndk_test.weizhang.model.ViolationResultBean;

public class ShowResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        VStatusBarUtils.setColor(this, getResources().getColor(R.color.violation_black_russian));

        Bundle bundle = getIntent().getExtras();
        ViolationResultBean.ResultBean.ListsBean listsBean = bundle.getParcelable("list_bean");

        initTitle();
        initView(listsBean);
    }

    private void initView(ViolationResultBean.ResultBean.ListsBean listsBean){

        TextView sraTvTime = (TextView) findViewById(R.id.asr_tv_time);
        TextView sraTvPlace = (TextView) findViewById(R.id.asr_tv_place);
        TextView sraTvContent = (TextView) findViewById(R.id.asr_tv_content);
        TextView sraTvMoney = (TextView) findViewById(R.id.asr_tv_money);
        TextView sraTvScores = (TextView) findViewById(R.id.asr_tv_scores);
        TextView sraTvHandle = (TextView) findViewById(R.id.asr_tv_handle);

        sraTvTime.setText(listsBean.getDate());
        sraTvPlace.setText(listsBean.getArea());
        sraTvContent.setText(listsBean.getAct());
        sraTvMoney.setText(listsBean.getMoney());
        sraTvScores.setText(listsBean.getFen());
        if (listsBean.getHandled().equals("0")){
            sraTvHandle.setText("未处理");
        }else {
            sraTvHandle.setText("已处理");
        }

    }

    private void initTitle(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.v_txtTitle);
        txtTitle.setText("违章详情");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.v_btnBack);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
