package com.zividig.ndk_test.weizhang.presenter.implPresenter;

import com.zividig.ndk_test.weizhang.api.ApiManage;
import com.zividig.ndk_test.weizhang.api.ViolationKey;
import com.zividig.ndk_test.weizhang.model.TestBean;
import com.zividig.ndk_test.weizhang.presenter.ICityListPresenter;
import com.zividig.ndk_test.weizhang.presenter.implView.ICityListActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adolph
 * on 2016-12-09.
 */

public class CityListPresenterImpl extends BasePresenterImpl implements ICityListPresenter{

    private ICityListActivity mICityListActivity;

    public CityListPresenterImpl(ICityListActivity activity){
        mICityListActivity = activity;
    }


    @Override
    public void getCityList(final String province) {
        Subscription subscription = ApiManage.getInstance()
                .getApiService()
                .getCityList(province, ViolationKey.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TestBean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("获取城市完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mICityListActivity.hidProgressDialog();
                        System.out.println("获取城市错误" + e);
                        mICityListActivity.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(TestBean testBean) {
                        mICityListActivity.hidProgressDialog();
                        mICityListActivity.showCityList(getStringCity(testBean,province));
                    }
                });

        addSubscription(subscription);
    }

    private List<List<String>> getStringCity(TestBean testBean, String province){

        List<List<String>> cityList = new ArrayList<>();

        List<String> abbrList = new ArrayList<>();
        List<String> cityNameList = new ArrayList<>();
        List<String> cityCodeList = new ArrayList<>();
        List<String> engineList = new ArrayList<>();
        List<String> enginenoList = new ArrayList<>();
        List<String> classaList = new ArrayList<>();
        List<String> classnoList = new ArrayList<>();


        if (province.equals("BJ")){
            List<TestBean.ResultBean.BJBean.CitysBean> citys = testBean.getResult().getBJ().getCitys();

            String abbr = citys.get(0).getAbbr();
            String cityName = citys.get(0).getCity_name();
            String cityCode = citys.get(0).getCity_code();
            String engine = citys.get(0).getEngine();
            String 	engineno = citys.get(0).getEngineno();
            String 	classa = citys.get(0).getClassa();
            String 	classno = citys.get(0).getClassno();

            abbrList.add(abbr);
            cityNameList.add(cityName);
            cityCodeList.add(cityCode);
            engineList.add(engine);
            enginenoList.add(engineno);
            classaList.add(classa);
            classnoList.add(classno);

        }else if (province.equals("SH")){
            List<TestBean.ResultBean.SHBean.CitysBeanX> citys = testBean.getResult().getSH().getCitys();

            String abbr = citys.get(0).getAbbr();
            String cityName = citys.get(0).getCity_name();
            String cityCode = citys.get(0).getCity_code();
            String engine = citys.get(0).getEngine();
            String 	engineno = citys.get(0).getEngineno();
            String 	classa = citys.get(0).getClassa();
            String 	classno = citys.get(0).getClassno();

            abbrList.add(abbr);
            cityNameList.add(cityName);
            cityCodeList.add(cityCode);
            engineList.add(engine);
            enginenoList.add(engineno);
            classaList.add(classa);
            classnoList.add(classno);
        }else if (province.equals("SC")){
            List<TestBean.ResultBean.SCBean.CitysBeanXX> citys = testBean.getResult().getSC().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.SCBean.CitysBeanXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());

            }
        }else if (province.equals("ZJ")){
            List<TestBean.ResultBean.ZJBean.CitysBeanXXX> citys = testBean.getResult().getZJ().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.ZJBean.CitysBeanXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("FJ")){
            List<TestBean.ResultBean.FJBean.CitysBeanXXXX> citys = testBean.getResult().getFJ().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.FJBean.CitysBeanXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("HB")){
            List<TestBean.ResultBean.HBBean.CitysBeanXXXXX> citys = testBean.getResult().getHB().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.HBBean.CitysBeanXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("JL")){
            List<TestBean.ResultBean.JLBean.CitysBeanXXXXXX> citys = testBean.getResult().getJL().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.JLBean.CitysBeanXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("AH")){
            List<TestBean.ResultBean.AHBean.CitysBeanXXXXXXX> citys = testBean.getResult().getAH().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.AHBean.CitysBeanXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("NMG")){
            List<TestBean.ResultBean.NMGBean.CitysBeanXXXXXXXX> citys = testBean.getResult().getNMG().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.NMGBean.CitysBeanXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("LN")){
            List<TestBean.ResultBean.LNBean.CitysBeanXXXXXXXXX> citys = testBean.getResult().getLN().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.LNBean.CitysBeanXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("SD")){
            List<TestBean.ResultBean.SDBean.CitysBeanXXXXXXXXXX> citys = testBean.getResult().getSD().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.SDBean.CitysBeanXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("HN")){
            List<TestBean.ResultBean.HNBean.CitysBeanXXXXXXXXXXX> citys = testBean.getResult().getHN().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.HNBean.CitysBeanXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("JS")){
            List<TestBean.ResultBean.JSBean.CitysBeanXXXXXXXXXXXX> citys = testBean.getResult().getJS().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.JSBean.CitysBeanXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("SX")){
            List<TestBean.ResultBean.SXBean.CitysBeanXXXXXXXXXXXXX> citys = testBean.getResult().getSX().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.SXBean.CitysBeanXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("QH")){
            List<TestBean.ResultBean.QHBean.CitysBeanXXXXXXXXXXXXXX> citys = testBean.getResult().getQH().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.QHBean.CitysBeanXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("GD")){
            List<TestBean.ResultBean.GDBean.CitysBeanXXXXXXXXXXXXXXX> citys = testBean.getResult().getGD().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.GDBean.CitysBeanXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("FB")){
            List<TestBean.ResultBean.FBBean.CitysBeanXXXXXXXXXXXXXXXX> citys = testBean.getResult().getFB().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.FBBean.CitysBeanXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        } else if (province.equals("HLJ")){
            List<TestBean.ResultBean.HLJBean.CitysBeanXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getHLJ().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.HLJBean.CitysBeanXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("YN")){
            List<TestBean.ResultBean.YNBean.CitysBeanXXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getYN().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.YNBean.CitysBeanXXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("HAN")){
            List<TestBean.ResultBean.HANBean.CitysBeanXXXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getHAN().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.HANBean.CitysBeanXXXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("NX")){
            List<TestBean.ResultBean.NXBean.CitysBeanXXXXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getNX().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.NXBean.CitysBeanXXXXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("CQ")){
            List<TestBean.ResultBean.CQBean.CitysBeanXXXXXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getCQ().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.CQBean.CitysBeanXXXXXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }else if (province.equals("GX")){
            List<TestBean.ResultBean.GXBean.CitysBeanXXXXXXXXXXXXXXXXXXXXXX> citys = testBean.getResult().getGX().getCitys();
            String abbr = citys.get(0).getAbbr();
            abbrList.add(abbr);

            for (TestBean.ResultBean.GXBean.CitysBeanXXXXXXXXXXXXXXXXXXXXXX city : citys){
                cityNameList.add(city.getCity_name());
                cityCodeList.add(city.getCity_code());
                engineList.add(city.getEngine());
                enginenoList.add(city.getEngineno());
                classaList.add(city.getClassa());
                classnoList.add(city.getClassno());
            }
        }

        cityList.add(abbrList);
        cityList.add(cityNameList);
        cityList.add(cityCodeList);
        cityList.add(engineList);
        cityList.add(enginenoList);
        cityList.add(classaList);
        cityList.add(classnoList);

        return cityList;
    }
}
