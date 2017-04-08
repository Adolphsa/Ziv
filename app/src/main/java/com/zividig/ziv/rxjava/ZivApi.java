package com.zividig.ziv.rxjava;

import com.zividig.ziv.rxjava.model.DeviceStateResponse;
import com.zividig.ziv.rxjava.model.DeviceWakeResponse;
import com.zividig.ziv.rxjava.model.LocationResponse;
import com.zividig.ziv.rxjava.model.SnapResponse;
import com.zividig.ziv.rxjava.model.VideoResponse;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by adolph
 * on 2017-03-17.
 */

public interface ZivApi {

    //获取设备状态
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/info")
    Observable<DeviceStateResponse> getDeviceStateInfo(@QueryMap Map<String, String> options, @Body RequestBody body);

    //获取实时位置
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("location/realtime")
    Observable<LocationResponse> getLocationInfo(@QueryMap Map<String, String> options, @Body RequestBody body);

    //设备唤醒 device/wake
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/wake")
    Observable<DeviceWakeResponse> sendWakeupOrder(@QueryMap Map<String, String> options, @Body RequestBody body);

    //图片抓拍
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/snap")
    Observable<SnapResponse> getImageUrl(@QueryMap Map<String, String> options, @Body RequestBody body);

    //下载图片
    @GET
    Observable<ResponseBody> downLoadImage(@Url String imageUrl);

    //实时视频
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("device/rtsp")
    Observable<VideoResponse> startVideo(@QueryMap Map<String, String> options, @Body RequestBody body);
}
