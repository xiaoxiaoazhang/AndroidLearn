/*
 * Copyright (c) 2008-2018 UBT Corporation.  All rights reserved.  Redistribution,
 * modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *
 */

package com.chihun.learn.retrofitdemo.network;

import android.util.Log;

import com.chihun.learn.retrofitdemo.MyApp;
import com.chihun.learn.retrofitdemo.network.interceptor.CacheInterceptor;
import com.chihun.learn.retrofitdemo.network.interceptor.CacheInterceptor2;
import com.chihun.learn.retrofitdemo.network.interceptor.CacheInterceptor3;
import com.chihun.learn.retrofitdemo.network.interceptor.CommonHeaderInterceptor;
import com.chihun.learn.retrofitdemo.network.interceptor.DynTimeoutInterceptor;
import com.chihun.learn.retrofitdemo.network.interceptor.LogInterceptor;
import com.chihun.learn.retrofitdemo.network.interceptor.RetryInterceptor;

import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.internal.tls.OkHostnameVerifier;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    public static boolean DEBUG = true;
    public static final int DEFAULT_TIMEOUT = 5;

    private static final Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static final CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();

    private static Retrofit retrofit;
    private static ImageService imageService;
    private Network() {}

    public static ImageService imageService() {
        if(imageService == null) {
            imageService = getRetrofit(getOkHttpClient()).create(ImageService.class);
        }
        return imageService;
    }

    private static Retrofit getRetrofit(OkHttpClient okHttpClient) {
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Urls.BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
        return retrofit;
    }

    private static OkHttpClient getOkHttpClient() {
        // 声明缓存地址和大小
        Cache cache = new Cache(MyApp.getInstance().getCacheDir(),10*1024*1024);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
//                .addInterceptor(new LogInterceptor()) //自定义日志
                .addInterceptor(new HttpLoggingInterceptor().setLevel(DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new CommonHeaderInterceptor()) //通用请求头
                // addNetworkInterceptor和addInterceptor
                // 两个方法同样是添加拦截器，addNetworkInterceptor添加的是网络拦截器，在网络畅通的时候会调用，而addInterceptor则都会调用。所以我们应该是在addInterceptor去写逻辑。
//                .addNetworkInterceptor(new CacheInterceptor())
                // 而，现在我们需要这样的一个策略：在无网络的情况下读取缓存，而且网络下的缓存也有过期时间，有网络的情况下根据缓存的过期时间重新请求，修改拦截器的逻辑：
//                .addInterceptor(new CacheInterceptor2(MyApp.getInstance()))
//                .addNetworkInterceptor(new CacheInterceptor3())
//                .cache(cache)
//                .addInterceptor(new DynTimeoutInterceptor(retrofit)) //动态设置某个url的超时时间
//                .addInterceptor(new RetryInterceptor(3))
//                .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        onHttps(okHttpClientBuilder);
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }

    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        //创建一个不验证证书链的证书信任管理器。
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
        }};

        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts,
                new java.security.SecureRandom());
        // Create an ssl socket factory with our all-trusting manager
        return sslContext.getSocketFactory();
    }


    //使用自定义SSLSocketFactory
    private static void onHttps(OkHttpClient.Builder builder) {
        try {
//            builder.sslSocketFactory(getSSLSocketFactory()).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpsManager.SSLParams sslParams = HttpsManager.getSslSocketFactory(null, null, null);
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .hostnameVerifier(OkHostnameVerifier.INSTANCE);
            //      .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//                  .hostnameVerifier(new HostnameVerifier() {
//                        @Override
//                        public boolean verify(String hostname, SSLSession session) {
//                            Log.d("Network", "hostname: " + hostname);
//        //                    return hostname.startsWith("chihun.com");
//                            return true;
//                        }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
