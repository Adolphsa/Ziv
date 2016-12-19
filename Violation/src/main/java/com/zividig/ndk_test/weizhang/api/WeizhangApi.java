package com.zividig.ndk_test.weizhang.api;

import com.zividig.ndk_test.weizhang.model.TestBean;
import com.zividig.ndk_test.weizhang.model.ViolationResultBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by adolph
 * on 2016-12-08.
 */

public interface WeizhangApi {

    //获取省
    @GET("wz/citys")
    Observable<TestBean>getProvinceList(@Query("key") String key);

    //获取城市
    @GET("wz/citys")
    Observable<TestBean>getCityList(@Query("province") String province, @Query("key") String key);

    //获取违章查询结果
    @GET("wz/query")
    Observable<ViolationResultBean>getViolationResult(@QueryMap Map<String,String> options);
}
