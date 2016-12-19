package com.zividig.ndk_test.weizhang.presenter.implPresenter;

import com.zividig.ndk_test.weizhang.presenter.BasePresenter;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by adolph
 * on 2016-11-09.
 */

public class BasePresenterImpl implements BasePresenter{

    private CompositeSubscription mCompositeSubscription;

    protected void addSubscription(Subscription s){

        if (this.mCompositeSubscription == null){
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    @Override
    public void unsubscribe() {
        if (this.mCompositeSubscription != null){
            this.mCompositeSubscription.unsubscribe();
        }
    }
}
