package com.zividig.ndk_test.weizhang.presenter.implPresenter;

import com.zividig.ndk_test.weizhang.api.ApiManage;
import com.zividig.ndk_test.weizhang.api.ViolationKey;
import com.zividig.ndk_test.weizhang.model.TestBean;
import com.zividig.ndk_test.weizhang.presenter.IProvinceListPresenter;
import com.zividig.ndk_test.weizhang.presenter.implView.IProvinceListActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adolph
 * on 2016-12-08.
 */

public class ProvinceListPresenterImpl extends BasePresenterImpl implements IProvinceListPresenter{

    private IProvinceListActivity mIProvinceListActivity;

    public ProvinceListPresenterImpl(IProvinceListActivity activity){
        mIProvinceListActivity = activity;
    }

    @Override
    public void getProvinceList() {
        Subscription subscription = ApiManage.getInstance()
                .getApiService()
                .getProvinceList(ViolationKey.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TestBean>() {
                               @Override
                               public void onCompleted() {
                                   System.out.println("获取省份完成");
                               }

                               @Override
                               public void onError(Throwable e) {
                                   mIProvinceListActivity.hidProgressDialog();
                                   System.out.println("错误" + e);
                                   mIProvinceListActivity.showError(e.getMessage());
                               }

                               @Override
                               public void onNext(TestBean testBean) {
                                   mIProvinceListActivity.hidProgressDialog();
                                   mIProvinceListActivity.showProvinceList(getStringPro(testBean),getStringProCode(testBean));
                               }
                           }
                );
        addSubscription(subscription);
    }

    //获取省份
    private List<String> getStringPro(TestBean testBean){

        List<String> provinceList = new ArrayList<String>();

        String bj = testBean.getResult().getBJ().getProvince();
        String sh = testBean.getResult().getSH().getProvince();
        String sc = testBean.getResult().getSC().getProvince();
        String zj = testBean.getResult().getZJ().getProvince();
        String fj = testBean.getResult().getFJ().getProvince();
        String hb = testBean.getResult().getHB().getProvince();
        String jl = testBean.getResult().getJL().getProvince();
        String an = testBean.getResult().getAH().getProvince();
        String nmg = testBean.getResult().getNMG().getProvince();
        String ln = testBean.getResult().getLN().getProvince();
        String sd = testBean.getResult().getSD().getProvince();
        String hn = testBean.getResult().getHN().getProvince();
        String js = testBean.getResult().getJS().getProvince();
        String sx = testBean.getResult().getSX().getProvince();
        String qh = testBean.getResult().getQH().getProvince();
        String gd = testBean.getResult().getGD().getProvince();
        String fb = testBean.getResult().getFB().getProvince();
        String hlj = testBean.getResult().getHLJ().getProvince();
        String yn = testBean.getResult().getYN().getProvince();
        String han = testBean.getResult().getHAN().getProvince();
        String nx = testBean.getResult().getNX().getProvince();
        String cq = testBean.getResult().getCQ().getProvince();
        String gx = testBean.getResult().getGX().getProvince();

        provinceList.add(bj);
        provinceList.add(sh);
        provinceList.add(sc);
        provinceList.add(zj);
        provinceList.add(fj);
        provinceList.add(hb);
        provinceList.add(jl);
        provinceList.add(an);
        provinceList.add(nmg);
        provinceList.add(ln);
        provinceList.add(sd);
        provinceList.add(hn);
        provinceList.add(js);
        provinceList.add(sx);
        provinceList.add(qh);
        provinceList.add(gd);
        provinceList.add(fb);
        provinceList.add(hlj);
        provinceList.add(yn);
        provinceList.add(han);
        provinceList.add(nx);
        provinceList.add(cq);
        provinceList.add(gx);

        return provinceList;
    }

    //获取省份简称
    private List<String> getStringProCode(TestBean testBean){

        List<String> provinceCodeList = new ArrayList<String>();

        String bj_code = testBean.getResult().getBJ().getProvince_code();
        String sh_code = testBean.getResult().getSH().getProvince_code();
        String sc_code = testBean.getResult().getSC().getProvince_code();
        String zj_code = testBean.getResult().getZJ().getProvince_code();
        String fj_code = testBean.getResult().getFJ().getProvince_code();
        String hb_code = testBean.getResult().getHB().getProvince_code();
        String jl_code = testBean.getResult().getJL().getProvince_code();
        String an_code = testBean.getResult().getAH().getProvince_code();
        String nmg_code = testBean.getResult().getNMG().getProvince_code();
        String ln_code = testBean.getResult().getLN().getProvince_code();
        String sd_code = testBean.getResult().getSD().getProvince_code();
        String hn_code = testBean.getResult().getHN().getProvince_code();
        String js_code = testBean.getResult().getJS().getProvince_code();
        String sx_code = testBean.getResult().getSX().getProvince_code();
        String qh_code = testBean.getResult().getQH().getProvince_code();
        String gd_code = testBean.getResult().getGD().getProvince_code();
        String fb_code = testBean.getResult().getFB().getProvince_code();
        String hlj_code = testBean.getResult().getHLJ().getProvince_code();
        String yn_code = testBean.getResult().getYN().getProvince_code();
        String han_code = testBean.getResult().getHAN().getProvince_code();
        String nx_code = testBean.getResult().getNX().getProvince_code();
        String cq_code = testBean.getResult().getCQ().getProvince_code();
        String gx_code = testBean.getResult().getGX().getProvince_code();

        provinceCodeList.add(bj_code);
        provinceCodeList.add(sh_code);
        provinceCodeList.add(sc_code);
        provinceCodeList.add(zj_code);
        provinceCodeList.add(fj_code);
        provinceCodeList.add(hb_code);
        provinceCodeList.add(jl_code);
        provinceCodeList.add(an_code);
        provinceCodeList.add(nmg_code);
        provinceCodeList.add(ln_code);
        provinceCodeList.add(sd_code);
        provinceCodeList.add(hn_code);
        provinceCodeList.add(js_code);
        provinceCodeList.add(sx_code);
        provinceCodeList.add(qh_code);
        provinceCodeList.add(gd_code);
        provinceCodeList.add(fb_code);
        provinceCodeList.add(hlj_code);
        provinceCodeList.add(yn_code);
        provinceCodeList.add(han_code);
        provinceCodeList.add(nx_code);
        provinceCodeList.add(cq_code);
        provinceCodeList.add(gx_code);

        return provinceCodeList;

    }
}
