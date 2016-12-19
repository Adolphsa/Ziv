package com.zividig.ndk_test.weizhang.presenter.implView;

/**
 * Created by adolph
 * on 2016-12-08.
 */

public interface IBaseActivity {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);

    void setStatusBar();

    void initTitle();
}
