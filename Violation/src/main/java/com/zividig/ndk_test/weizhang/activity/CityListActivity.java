package com.zividig.ndk_test.weizhang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zividig.ndk_test.R;
import com.zividig.ndk_test.weizhang.presenter.implPresenter.CityListPresenterImpl;
import com.zividig.ndk_test.weizhang.presenter.implView.ICityListActivity;

import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends AppCompatActivity implements ICityListActivity {

    private CityListPresenterImpl mCityListPresenter;
    private ListView mCityListView;
    private ProgressBar mProgressBar;
    String province_code = "";

    List<String> abbrList = new ArrayList<>();
    List<String> cityNameList = new ArrayList<>();
    List<String> cityCodeList = new ArrayList<>();
    List<String> engineList = new ArrayList<>();
    List<String> enginenoList = new ArrayList<>();
    List<String> classaList = new ArrayList<>();
    List<String> classnoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        Bundle bundle = getIntent().getExtras();
        province_code = bundle.getString("province_code");

        setStatusBar();
        initTitle();

        initView();
        initData();
    }

    private void initView() {

        mCityListView = (ListView) findViewById(R.id.list_city);
        mProgressBar = (ProgressBar) findViewById(R.id.city_progress);

        mCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println("省会简称" + abbrList.get(0));
//                System.out.println("城市名称" + cityNameList.get(i));
//                System.out.println("城市代码名称" + cityCodeList.get(i));
//                System.out.println("是否需要发动机号" + engineList.get(i));
//                System.out.println("发动机号" + enginenoList.get(i));
//                System.out.println("是否需要车架号" + classaList.get(i));
//                System.out.println("车架号" + classnoList.get(i));

                Intent intent = new Intent(CityListActivity.this,ViolationActivity.class);
                intent.putExtra("abbr",abbrList.get(0));
                intent.putExtra("city",cityNameList.get(i));
                intent.putExtra("city_code",cityCodeList.get(i));
                intent.putExtra("is_engine",engineList.get(i));
                intent.putExtra("engine_code",enginenoList.get(i));
                intent.putExtra("is_classa",classaList.get(i));
                intent.putExtra("classno",classnoList.get(i));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mCityListPresenter = new CityListPresenterImpl(this);
        mCityListPresenter.getCityList(province_code);

        abbrList = new ArrayList<>();
        cityNameList = new ArrayList<>();
        cityCodeList = new ArrayList<>();
        engineList = new ArrayList<>();
        enginenoList = new ArrayList<>();
        classaList = new ArrayList<>();
        classnoList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCityListPresenter.unsubscribe();
    }

    @Override
    public void showCityList(List<List<String>> cityList) {

        abbrList = cityList.get(0);
        cityNameList = cityList.get(1);
        cityCodeList = cityList.get(2);
        engineList = cityList.get(3);
        enginenoList = cityList.get(4);
        classaList = cityList.get(5);
        classnoList = cityList.get(6);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.array_item, cityNameList);
        mCityListView.setAdapter(adapter);
    }

    @Override
    public void showProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        if (mCityListView != null){
            Toast.makeText(this,error,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setStatusBar() {
        VStatusBarUtils.setColor(this, getResources().getColor(R.color.myColorPrimaryDark));
    }

    @Override
    public void initTitle() {

        // 标题
        TextView txtTitle = (TextView) findViewById(R.id.v_txtTitle);
        txtTitle.setText("城市列表");

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
