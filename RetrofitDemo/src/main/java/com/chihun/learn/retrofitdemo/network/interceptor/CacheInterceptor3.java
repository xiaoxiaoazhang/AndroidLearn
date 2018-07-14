package com.chihun.learn.retrofitdemo.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/**
 * 设置在有网络的情况下的缓存时间
 *  在有网络的时候，会优先获取缓存
 * 通过：addNetworkInterceptor 设置
 */
public class CacheInterceptor3 implements Interceptor {
    private int maxCacheTimeSecond = 20;

    public CacheInterceptor3(int maxCacheTimeSecond) {
        this.maxCacheTimeSecond = maxCacheTimeSecond;
    }

    public CacheInterceptor3() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        int maxAge = maxCacheTimeSecond;    // 在线缓存,单位:秒
        return originalResponse.newBuilder()
                .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                .removeHeader("Cache-Control")
                .header("Cache-Control", "public, max-age=" + maxAge)
                .build();
//        关于max-age和max-stale
//        maxAge ：设置最大失效时间，失效则不使用
//        maxStale ：设置最大失效时间，失效则不使用
//        max-stale在请求头设置有效，在响应头设置无效。
//        max-stale和max-age同时设置的时候，缓存失效的时间按最长的算。
    }
}
