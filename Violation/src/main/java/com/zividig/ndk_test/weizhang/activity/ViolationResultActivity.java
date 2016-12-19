package com.zividig.ndk_test.weizhang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zividig.ndk_test.R;
import com.zividig.ndk_test.weizhang.api.ViolationKey;
import com.zividig.ndk_test.weizhang.model.CarInfo;
import com.zividig.ndk_test.weizhang.model.ViolationResultAdapter;
import com.zividig.ndk_test.weizhang.model.ViolationResultBean;
import com.zividig.ndk_test.weizhang.presenter.implPresenter.ViolationResultPresenterImpl;
import com.zividig.ndk_test.weizhang.presenter.implView.IViolationResultActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViolationResultActivity extends AppCompatActivity implements IViolationResultActivity{

    private ViolationResultPresenterImpl mViolationResultPresenter;
    private ProgressBar mProgressBar;
    private ListView mViolationListView;
    private ViolationResultAdapter mAdapter;
    private TextView mTotalVio;
    private TextView mTotalScores;
    private TextView mTotalMoney;

    private int totalVio = 0;           //总违章
    private int totalScores = 0;        //总扣分
    private int totalMoney = 0;         //总罚款金

    private LinearLayout mViolationll;
    private TextView mNoViolation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vialation_result);
        VStatusBarUtils.setColor(this, getResources().getColor(R.color.myColorPrimaryDark));

        Bundle bundle = getIntent().getExtras();
        Map<String, String> options = setQueryResultParam(bundle);

        setStatusBar();
        initTitle();

        initView();
        initData(options);
    }

    private void initView(){

        mViolationll = (LinearLayout) findViewById(R.id.violation_ll);
        mNoViolation = (TextView) findViewById(R.id.violation_no);

        //总违章
        mTotalVio = (TextView) findViewById(R.id.acr_tv__vio);
        //总扣分
        mTotalScores = (TextView) findViewById(R.id.acr_tv_scores);
        //总罚款金额
        mTotalMoney = (TextView) findViewById(R.id.acr_tv_money);

        mViolationListView = (ListView) findViewById(R.id.violation_list_view);

        mProgressBar = (ProgressBar) findViewById(R.id.violation_progress);
    }

    /**
     * 初始化数据
     * @param options
     */
    private void initData(Map<String,String> options){

        mViolationResultPresenter = new ViolationResultPresenterImpl(this);
        mViolationResultPresenter.getViolationResult(options);

        mAdapter = new ViolationResultAdapter(ViolationResultActivity.this);
    }

    /**
     * 设置获取违章结果的请求参数
     * @param bundle bundle
     * @return Map<String,String>
     */
    private Map<String,String> setQueryResultParam(Bundle bundle){

        Map<String,String> options = new HashMap<>();
        options.put("dtype","json");

        CarInfo carInfo = bundle.getParcelable("car_info");
        String carNumbers = bundle.getString("car_code");   //车牌号
        System.out.println("车牌号---" + carNumbers);

        if (carInfo != null){

            options.put("city",carInfo.getCityCode());
            System.out.println("城市代码---" + carInfo.getCityCode());

            options.put("hphm",carNumbers);
            options.put("hpzl","02");

            if (carInfo.getIsClassa().equals("1")){
                String carVinCode = bundle.getString("car_classa"); //车架号
                options.put("classno",carVinCode);
                System.out.println("车架号---" + carVinCode);
            }
            if (carInfo.getIsEngine().equals("1")){
                String carEngineCode = bundle.getString("car_engine");  //发动机号
                options.put("engineno",carEngineCode);
                System.out.println("发动机号---" + carEngineCode);
            }
        }
        options.put("key", ViolationKey.key);
        return options;
    }

    @Override
    public void showViolationResult(ViolationResultBean violationResultBean) {

       final List<ViolationResultBean.ResultBean.ListsBean> resultList = violationResultBean.getResult().getLists();

        //设置总违章数
        setTotal(resultList);

        //如果违章数为0 ,显示没有违章记录
        if (totalVio == 0){
            mViolationll.setVisibility(View.INVISIBLE);
            mNoViolation.setVisibility(View.VISIBLE);
        }

        //设置Adapter
        mAdapter.addItem(resultList);
        mViolationListView.setAdapter(mAdapter);

        mViolationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViolationResultActivity.this, ShowResultActivity.class);
                intent.putExtra("list_bean",resultList.get(i));
                startActivity(intent);
            }
        });
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
        if (mViolationListView!= null){
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
        txtTitle.setText("违章结果列表");

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViolationResultPresenter.unsubscribe();
    }


    private void setTotal(List<ViolationResultBean.ResultBean.ListsBean> results){
        for (ViolationResultBean.ResultBean.ListsBean result : results){
            totalScores += Integer.valueOf(result.getFen()) ;
            totalMoney += Integer.valueOf(result.getMoney());
        }
        totalVio = results.size();

        mTotalVio.setText(String.valueOf(totalVio));
        mTotalScores.setText(String.valueOf(totalScores));
        mTotalMoney.setText(String.valueOf(totalMoney));
    }
}
