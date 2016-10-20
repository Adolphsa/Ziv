package com.zividig.ziv.function;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.zividig.ziv.R;
import com.zividig.ziv.main.BaseActivity;
import com.zividig.ziv.utils.UtcTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrackQueryDateChoose extends BaseActivity {

    private EditText startDateTime;
    private EditText endDateTime;

    private String initStartDateTime = "2016-10-19 09:00"; // 初始化开始时间
    private String initEndDateTime = "2016-10-19 11:00"; // 初始化结束时间

    TimePickerView startPv;
    TimePickerView endPv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_query_date_choose);

       initView();
    }

    private void initView(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.tv_title);
        txtTitle.setText("选择查询时间");

        //返回按钮
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 两个输入框
        startDateTime = (EditText) findViewById(R.id.inputDate);
        endDateTime = (EditText) findViewById(R.id.inputDate2);

        startDateTime.setText(initStartDateTime);
        endDateTime.setText(initEndDateTime);

        //时间选择器
        startPv = getTpvInstance(TrackQueryDateChoose.this);
        endPv = getTpvInstance(TrackQueryDateChoose.this);

        //时间选择后回调
        startPv.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                startDateTime.setText(getTime(date));
            }
        });
        //弹出时间选择器
        startDateTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startPv.show();
            }
        });

        endPv.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                endDateTime.setText(getTime(date));
            }
        });
        endDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endPv.show();
            }
        });
    }

    /**
     * 点击查询  进去查询页面
     * @param view
     */
    public void dataQuery(View view){
        Intent intent = new Intent(TrackQueryDateChoose.this,TrackQuery.class);
        System.out.println("开始时间---" + startDateTime.getText().toString());
        System.out.println("结束时间---" + endDateTime.getText().toString());

        String start = startDateTime.getText().toString();
        String end = endDateTime.getText().toString();
        Calendar calendarStart = UtcTimeUtils.DateTimeToUTC(start);
        Calendar calendarEnd = UtcTimeUtils.DateTimeToUTC(end);

        long timeInMillisStart = calendarStart.getTimeInMillis();
        long timeInMillisEnd = calendarEnd.getTimeInMillis();
        System.out.println("UTC开始时间---" + timeInMillisStart);
        System.out.println("UTC结束时间---" + timeInMillisEnd);

        intent.putExtra("start_time",timeInMillisStart/1000);
        intent.putExtra("end_time",timeInMillisEnd/1000);
        startActivity(intent);

    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private TimePickerView getTpvInstance(Context context){
        TimePickerView timePickerView = new TimePickerView(context, TimePickerView.Type.ALL);
        //控制时间范围
//        Calendar calendar = Calendar.getInstance();
//        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        return timePickerView;
    }
}
