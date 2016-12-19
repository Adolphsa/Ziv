package com.zividig.ndk_test.weizhang.presenter.implPresenter;

import com.zividig.ndk_test.weizhang.api.ApiManage;
import com.zividig.ndk_test.weizhang.model.ViolationResultBean;
import com.zividig.ndk_test.weizhang.presenter.IViolationResultPresenter;
import com.zividig.ndk_test.weizhang.presenter.implView.IViolationResultActivity;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adolph
 * on 2016-12-15.
 */

public class ViolationResultPresenterImpl extends BasePresenterImpl implements IViolationResultPresenter{

    private IViolationResultActivity mIViolationResultActivity;

    public ViolationResultPresenterImpl(IViolationResultActivity activity){
        mIViolationResultActivity = activity;
    }

    @Override
    public void getViolationResult(Map<String,String> options) {

        Subscription subscription = ApiManage.getInstance()
                .getApiService()
                .getViolationResult(options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ViolationResultBean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("获取违章查询结果完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mIViolationResultActivity.hidProgressDialog();
                        mIViolationResultActivity.showError(e.getMessage());
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(ViolationResultBean violationResultBean) {
                        mIViolationResultActivity.hidProgressDialog();
                        mIViolationResultActivity.showViolationResult(violationResultBean);
                        System.out.println("违章查询结果---" + violationResultBean.getResultcode());
                    }
                });
        addSubscription(subscription);
    }
}
