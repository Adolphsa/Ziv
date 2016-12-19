package com.zividig.ndk_test.weizhang.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adolph
 * on 2016-12-08.
 */

public class ApiManage {

    static ApiManage apiManage;

    public static ApiManage getInstance(){
        if (apiManage == null){
            synchronized (ApiManage.class){
                if (apiManage == null){
                    apiManage = new ApiManage();
                }
            }
        }
        return apiManage;
    }

    private WeizhangApi mWeizhangApi;
    private Object weizhangMonitor = new Object();

    private static OkHttpClient client = new OkHttpClient.Builder().build();

    //获取省份
    public WeizhangApi getApiService(){
        if (mWeizhangApi == null){
            synchronized (weizhangMonitor){
                if (mWeizhangApi == null){
                    mWeizhangApi = new Retrofit.Builder()
                                    .baseUrl("http://v.juhe.cn/")
                                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                    .client(client)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build().create(WeizhangApi.class);
                }
            }
        }
        return mWeizhangApi;
    }


}
