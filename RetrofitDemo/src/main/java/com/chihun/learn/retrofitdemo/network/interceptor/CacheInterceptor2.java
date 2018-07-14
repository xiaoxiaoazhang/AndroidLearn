package com.chihun.learn.retrofitdemo.network.interceptor;

import android.content.Context;

import com.chihun.learn.retrofitdemo.utils.NetworkUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * 设置没有网络的情况下，
 *  的缓存时间
 *  通过：addInterceptor 设置
 */
public class CacheInterceptor2 implements Interceptor {
    private Context context;
    private int maxCacheTimeSecond = 4 * 7 * 24 * 60; // 离线时缓存保存4周,单位:秒

    public CacheInterceptor2(Context context, int maxCacheTimeSecond) {
        this.context = context;
        this.maxCacheTimeSecond = maxCacheTimeSecond;
    }

    public CacheInterceptor2(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtil.isNetworkConnected(context)) {
            int maxStale = maxCacheTimeSecond;
            CacheControl tempCacheControl = new CacheControl.Builder()
                    .onlyIfCached()
                    .maxStale(maxStale, TimeUnit.SECONDS)
                    .build();
            request = request.newBuilder()
                    .cacheControl(tempCacheControl)
                    .build();
        }
        return chain.proceed(request);
    }
}
