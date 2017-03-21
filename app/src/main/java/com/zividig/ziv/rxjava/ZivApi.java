package com.zividig.ziv.rxjava;

import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.rxjava.model.DeviceWakeResponse;
import com.zividig.ziv.rxjava.model.LocationResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by adolph
 * on 2017-03-17.
 */

public interface ZivApi {

    //获取设备状态
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/info")
    Observable<DeviceStateResponse> getDeviceStateInfo(@QueryMap Map<String,String> options, @Body RequestBody body);

    //获取实时位置
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("location/realtime")
    Observable<LocationResponse> getLocationInfo(@QueryMap Map<String,String> options, @Body RequestBody body);

    //设备唤醒 device/wake
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/wake")
    Observable<DeviceWakeResponse> sendWakeupOrder(@QueryMap Map<String,String> options, @Body RequestBody body);
}
