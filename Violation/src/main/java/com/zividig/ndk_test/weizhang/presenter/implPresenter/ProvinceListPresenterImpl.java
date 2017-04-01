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

        if (testBean != null && testBean.getResult() != null){

            TestBean.ResultBean resultBean = testBean.getResult();

            if (resultBean != null){

                if (resultBean.getBJ() != null){
                    String bj = resultBean.getBJ().getProvince();
                    provinceList.add(bj);
                }

                if (resultBean.getSH() != null){
                    String sh = resultBean.getSH().getProvince();
                    provinceList.add(sh);
                }

                if (resultBean.getSC() != null){
                    String sc = resultBean.getSC().getProvince();
                    provinceList.add(sc);
                }

                if (resultBean.getZJ() != null){
                    String zj = resultBean.getZJ().getProvince();
                    provinceList.add(zj);
                }

                if (resultBean.getSH() != null){
                    String fj = resultBean.getFJ().getProvince();
                    provinceList.add(fj);
                }

                if (resultBean.getHB() != null){
                    String hb = resultBean.getHB().getProvince();
                    provinceList.add(hb);
                }

                if (resultBean.getJL() != null){
                    String jl = resultBean.getJL().getProvince();
                    provinceList.add(jl);
                }

                if (resultBean.getAH() != null){
                    String an = resultBean.getAH().getProvince();
                    provinceList.add(an);
                }

                if (resultBean.getNMG() != null){
                    String nmg = resultBean.getNMG().getProvince();
                    provinceList.add(nmg);
                }

                if (resultBean.getLN() != null){
                    String ln = resultBean.getLN().getProvince();
                    provinceList.add(ln);
                }

                if (resultBean.getSD() != null){
                    String sd = resultBean.getSD().getProvince();
                    provinceList.add(sd);
                }

                if (resultBean.getHN() != null){
                    String hn = resultBean.getHN().getProvince();
                    provinceList.add(hn);
                }

                if (resultBean.getJS() != null){
                    String js = resultBean.getJS().getProvince();
                    provinceList.add(js);
                }

                if (resultBean.getSX() != null){
                    String sx = resultBean.getSX().getProvince();
                    provinceList.add(sx);
                }

                if (resultBean.getQH() != null){
                    String qh = resultBean.getQH().getProvince();
                    provinceList.add(qh);
                }

                if (resultBean.getGD() != null){
                    String gd = resultBean.getGD().getProvince();
                    provinceList.add(gd);
                }

                if (resultBean.getFB() != null){
                    String fb = resultBean.getFB().getProvince();
                    provinceList.add(fb);
                }

                if (resultBean.getHLJ() != null){
                    String hlj = resultBean.getHLJ().getProvince();
                    provinceList.add(hlj);
                }

                if (resultBean.getYN() != null){
                    String yn = resultBean.getYN().getProvince();
                    provinceList.add(yn);
                }

                if (resultBean.getHAN() != null){
                    String han = resultBean.getHAN().getProvince();
                    provinceList.add(han);
                }

                if (resultBean.getNX() != null){
                    String nx = resultBean.getNX().getProvince();
                    provinceList.add(nx);
                }

                if (resultBean.getCQ() != null){
                    String cq = resultBean.getCQ().getProvince();
                    provinceList.add(cq);
                }

                if (resultBean.getGX() != null){
                    String gx = resultBean.getGX().getProvince();
                    provinceList.add(gx);
                }

            }
        }

        return provinceList;
    }

    //获取省份简称
    private List<String> getStringProCode(TestBean testBean){

        List<String> provinceCodeList = new ArrayList<String>();

        if (testBean != null && testBean.getResult() != null){

            TestBean.ResultBean resultBean = testBean.getResult();

            if (resultBean != null){

                if (resultBean.getBJ() != null){
                    String bj = resultBean.getBJ().getProvince_code();
                    provinceCodeList.add(bj);
                }

                if (resultBean.getSH() != null){
                    String sh = resultBean.getSH().getProvince_code();
                    provinceCodeList.add(sh);
                }

                if (resultBean.getSC() != null){
                    String sc = resultBean.getSC().getProvince_code();
                    provinceCodeList.add(sc);
                }

                if (resultBean.getZJ() != null){
                    String zj = resultBean.getZJ().getProvince_code();
                    provinceCodeList.add(zj);
                }

                if (resultBean.getSH() != null){
                    String fj = resultBean.getFJ().getProvince_code();
                    provinceCodeList.add(fj);
                }

                if (resultBean.getHB() != null){
                    String hb = resultBean.getHB().getProvince_code();
                    provinceCodeList.add(hb);
                }

                if (resultBean.getJL() != null){
                    String jl = resultBean.getJL().getProvince_code();
                    provinceCodeList.add(jl);
                }

                if (resultBean.getAH() != null){
                    String an = resultBean.getAH().getProvince_code();
                    provinceCodeList.add(an);
                }

                if (resultBean.getNMG() != null){
                    String nmg = resultBean.getNMG().getProvince_code();
                    provinceCodeList.add(nmg);
                }

                if (resultBean.getLN() != null){
                    String ln = resultBean.getLN().getProvince_code();
                    provinceCodeList.add(ln);
                }

                if (resultBean.getSD() != null){
                    String sd = resultBean.getSD().getProvince_code();
                    provinceCodeList.add(sd);
                }

                if (resultBean.getHN() != null){
                    String hn = resultBean.getHN().getProvince_code();
                    provinceCodeList.add(hn);
                }

                if (resultBean.getJS() != null){
                    String js = resultBean.getJS().getProvince_code();
                    provinceCodeList.add(js);
                }

                if (resultBean.getSX() != null){
                    String sx = resultBean.getSX().getProvince_code();
                    provinceCodeList.add(sx);
                }

                if (resultBean.getQH() != null){
                    String qh = resultBean.getQH().getProvince_code();
                    provinceCodeList.add(qh);
                }

                if (resultBean.getGD() != null){
                    String gd = resultBean.getGD().getProvince_code();
                    provinceCodeList.add(gd);
                }

                if (resultBean.getFB() != null){
                    String fb = resultBean.getFB().getProvince_code();
                    provinceCodeList.add(fb);
                }

                if (resultBean.getHLJ() != null){
                    String hlj = resultBean.getHLJ().getProvince_code();
                    provinceCodeList.add(hlj);
                }

                if (resultBean.getYN() != null){
                    String yn = resultBean.getYN().getProvince_code();
                    provinceCodeList.add(yn);
                }

                if (resultBean.getHAN() != null){
                    String han = resultBean.getHAN().getProvince_code();
                    provinceCodeList.add(han);
                }

                if (resultBean.getNX() != null){
                    String nx = resultBean.getNX().getProvince_code();
                    provinceCodeList.add(nx);
                }

                if (resultBean.getCQ() != null){
                    String cq = resultBean.getCQ().getProvince_code();
                    provinceCodeList.add(cq);
                }

                if (resultBean.getGX() != null){
                    String gx = resultBean.getGX().getProvince_code();
                    provinceCodeList.add(gx);
                }

            }
        }
        return provinceCodeList;

    }
}
