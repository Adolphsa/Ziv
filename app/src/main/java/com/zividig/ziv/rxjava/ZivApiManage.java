package com.zividig.ziv.rxjava;

import com.zividig.ziv.main.ZivApp;
import com.zividig.ziv.utils.NetWorkUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adolph
 * on 2017-03-17.
 */

public class ZivApiManage {

    public static final String BASE_URL = "http://api.zivdigi.com/v1/";

    //缓存策略  有网时获取网络数据   没网时获取缓存
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(ZivApp.getInstance())){
                int maxAge = 60; // 在线缓存在1分钟内可读取
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            }else {
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    public static ZivApiManage sZivApiManage;
    //缓存相关
    private static File httpCacheDirectory = new File(ZivApp.getInstance().getCacheDir(),"zhihuCachesss");
    private static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    private static Cache cache = new Cache(httpCacheDirectory,cacheSize);
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .build();

    public ZivApi zivApi;
    private Object zivMonitor = new Object();

    public static ZivApiManage getInstance(){
        if (sZivApiManage == null){
            synchronized (ZivApiManage.class){
                if (sZivApiManage == null){
                    sZivApiManage = new ZivApiManage();
                }
            }
        }
        return sZivApiManage;
    }

    public ZivApi getZivApiService(){
        if (zivApi == null){
            synchronized (zivMonitor){
                if (zivApi == null){
                    zivApi = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build().create(ZivApi.class);
                }
            }
        }
        return zivApi;
    }

}
