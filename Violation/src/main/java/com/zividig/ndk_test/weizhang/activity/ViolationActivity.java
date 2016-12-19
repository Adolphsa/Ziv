package com.zividig.ndk_test.weizhang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zividig.ndk_test.R;
import com.zividig.ndk_test.weizhang.model.CarInfo;

public class ViolationActivity extends AppCompatActivity {

    private TextView mTvCity;   //查询地
    private TextView mCarCodeAbbr;  //省简称
    private LinearLayout mVinLl;    //车架号行
    private EditText mVin;          //车架或
    private LinearLayout mEngineNumberLl;       //发动机行
    private EditText mEngineNumber;             //发动机号

    // 行驶证图
    private View popXSZ;
    private CarInfo mCarInfo;
    private TextView mCarCodeNumber;
    private Button mQuery;
    private String mCarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_avtivity);
        VStatusBarUtils.setColor(this, getResources().getColor(R.color.myColorPrimaryDark));

        initTitle();
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        mCarInfo = new CarInfo();

        String abbr = bundle.getString("abbr");
        String city = bundle.getString("city");
        String cityCode = bundle.getString("city_code");
        String isEngine = bundle.getString("is_engine");
        String engineCode = bundle.getString("engine_code");
        String isClassa = bundle.getString("is_classa");
        String classCode = bundle.getString("classno");

        mCarInfo.setAbbr(abbr);
        mCarInfo.setCity(city);
        mCarInfo.setCityCode(cityCode);
        mCarInfo.setIsEngine(isEngine);
        mCarInfo.setEngineCode(engineCode);
        mCarInfo.setIsClassa(isClassa);
        mCarInfo.setClassCode(classCode);

        System.out.println("简称---" + abbr);
        System.out.println("查询地---" + city);
        System.out.println("查询地代码---" + cityCode);
        System.out.println("是否需要发动机号---" + isEngine);
        System.out.println("发动机号---" + engineCode);
        System.out.println("是否需要车架号---" + isClassa);
        System.out.println("车架号---" + classCode);

        setQueryItems(mCarInfo);
    }

    private void initTitle(){
        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.v_txtTitle);
        txtTitle.setText("违章查询");

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

    private void initView() {

        //查询地
        mTvCity = (TextView) findViewById(R.id.cx_city);
        //车牌  省简称
        mCarCodeAbbr = (TextView) findViewById(R.id.chepai_sz);
        //车牌 号码
        mCarCodeNumber = (TextView) findViewById(R.id.chepai_number);
        //车架号行列
        mVinLl = (LinearLayout) findViewById(R.id.row_chejia);
        //车架号
        mVin = (EditText) findViewById(R.id.chejia_number);
        //发动机号行列
        mEngineNumberLl = (LinearLayout) findViewById(R.id.row_engine);
        //发动机号
        mEngineNumber = (EditText) findViewById(R.id.engine_number);
        //查询按钮
        mQuery = (Button) findViewById(R.id.btn_query);

        // 显示隐藏行驶证图示
        popXSZ = (View) findViewById(R.id.popXSZ);
        popXSZ.setOnTouchListener(new popOnTouchListener());
        hideShowXSZ();
    }

    private void initData() {

        mTvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("查询地");
                Intent intent = new Intent(ViolationActivity.this, ProvinceListActivity.class);
                startActivity(intent);
            }
        });

        //查询按钮点击事件
        mQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("点击查询按钮");
                boolean result = checkQueryItem(mCarInfo);
                if (result){
                    Intent intent = new Intent(ViolationActivity.this,ViolationResultActivity.class);
                    intent.putExtra("car_info",mCarInfo);
                    intent.putExtra("car_code",mCarCode);
                    if (mCarInfo != null){
                        if (mCarInfo.getIsClassa().equals("1")){
                            intent.putExtra("car_classa",mVin.getText().toString());
                        }
                        if (mCarInfo.getIsEngine().equals("1")){
                            intent.putExtra("car_engine",mEngineNumber.getText().toString());
                        }
                    }
                    startActivity(intent);
                }
            }
        });
    }

    //根据城市的配置设置查询项目
    private void setQueryItems(CarInfo carInfo){

        mTvCity.setText(carInfo.getCity());
        mCarCodeAbbr.setText(carInfo.getAbbr());

        //车架号行列
        if (carInfo.getIsClassa().equals("0")){
            mVinLl.setVisibility(View.GONE);
        }else {
            mVinLl.setVisibility(View.VISIBLE);
            mVin.setHint("请输入车架号后" + carInfo.getClassCode() + "位");
        }

        //发动机号行列
        if (carInfo.getIsEngine().equals("0")){
            mEngineNumberLl.setVisibility(View.GONE);
        }else {
            mEngineNumberLl.setVisibility(View.VISIBLE);
            mEngineNumber.setHint("请输入发动机号后" + carInfo.getEngineCode() + "位");
        }
    }

    /**
     * 提交查询表单检测
     * @param carInfo   查询信息
     * @return boolean
     */
    private boolean checkQueryItem(CarInfo carInfo){
        //检查查询地
        if (mTvCity.getText().toString().length() <= 0){
            Toast.makeText(ViolationActivity.this, "请选择查询地", Toast.LENGTH_SHORT).show();
            return false;
        }
        //检查车牌号
        mCarCode = carInfo.getAbbr() + mCarCodeNumber.getText().toString();
        if (mCarCode.length() != 7){
            Toast.makeText(ViolationActivity.this, "您输入的车牌号有误", Toast.LENGTH_SHORT).show();
            return  false;
        }

        //检查车架号
        if (carInfo.getIsClassa().equals("1")){
            if (mVin.getText().toString().equals("")){
                Toast.makeText(ViolationActivity.this, "输入车架号不为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mVin.getText().toString().length() !=  Integer.valueOf(carInfo.getClassCode())){
                Toast.makeText(ViolationActivity.this, "输入车架号后" + carInfo.getClassCode() + "位",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //检查发动机号
        if (carInfo.getIsEngine().equals("1")){
            if (mEngineNumber.getText().toString().equals("")){
                Toast.makeText(ViolationActivity.this, "输入发动机号不为空", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mEngineNumber.getText().toString().length() !=  Integer.valueOf(carInfo.getEngineCode())){
                Toast.makeText(ViolationActivity.this, "输入发动机号后" + carInfo.getEngineCode() + "位",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    // 显示隐藏行驶证图示
    private void hideShowXSZ() {
        View btn_help1 = (View) findViewById(R.id.ico_chejia);
        View btn_help2 = (View) findViewById(R.id.ico_engine);
        Button btn_closeXSZ = (Button) findViewById(R.id.btn_closeXSZ);

        btn_help1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.VISIBLE);
            }
        });
        btn_help2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.VISIBLE);
            }
        });
        btn_closeXSZ.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popXSZ.setVisibility(View.GONE);
            }
        });
    }

    // 避免穿透导致表单元素取得焦点
    private class popOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View arg0, MotionEvent arg1) {
            popXSZ.setVisibility(View.GONE);
            return true;
        }
    }
}
