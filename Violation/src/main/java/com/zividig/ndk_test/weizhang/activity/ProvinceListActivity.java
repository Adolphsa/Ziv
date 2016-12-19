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
import com.zividig.ndk_test.weizhang.presenter.implPresenter.ProvinceListPresenterImpl;
import com.zividig.ndk_test.weizhang.presenter.implView.IProvinceListActivity;

import java.util.List;

public class ProvinceListActivity extends AppCompatActivity implements IProvinceListActivity{

    private ProvinceListPresenterImpl mProvinceListPresenter;
    private ListView mProvinceListView;
    private ProgressBar mProgressBar;

    List<String> mProvinceCodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weizhang_main_avtivity);

        setStatusBar();
        initTitle();

        initView();
        initData();
    }

    private void initView(){
        mProvinceListView = (ListView) findViewById(R.id.list_province);
        mProgressBar = (ProgressBar) findViewById(R.id.province_progress);

        mProvinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ProvinceListActivity.this,CityListActivity.class);
                intent.putExtra("province_code",mProvinceCodeList.get(i));
                startActivity(intent);
            }
        });
    }

    private void initData(){
        mProvinceListPresenter = new ProvinceListPresenterImpl(this);
        mProvinceListPresenter.getProvinceList();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProvinceListPresenter.unsubscribe();
    }


    @Override
    public void showProvinceList(List<String> provinceList,List<String> provinceCodeList) {
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,R.layout.array_item,provinceList);
        mProvinceCodeList = provinceCodeList;
        mProvinceListView.setAdapter(adapter);
    }


    @Override
    public void showProgressDialog() {
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hidProgressDialog() {
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showError(String error) {
        if (mProvinceListView != null){
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
        txtTitle.setText("省份列表");

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
