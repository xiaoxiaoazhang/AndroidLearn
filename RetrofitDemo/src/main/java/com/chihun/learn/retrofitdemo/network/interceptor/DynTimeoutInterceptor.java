package com.chihun.learn.retrofitdemo.network.interceptor;

import com.chihun.learn.retrofitdemo.network.Urls;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/****************************
 * @Description okHttp 添加动态的 超时时间 处理
 * @author chihun
 * @date 2018/7/14 16:27
 * @modifier
 * @modify_time
 *****************************/
public class DynTimeoutInterceptor implements Interceptor {
    private static final int CONNECT_LONG_TIMEOUT = 20;
    private static final int CONNECT_SHORT_TIMEOUT = 10;

    private Retrofit mRetrofit;

    public DynTimeoutInterceptor(Retrofit mRetrofit) {
        this.mRetrofit = mRetrofit;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        setDynamicConnectTimeout(oldRequest, mRetrofit, Urls.BASE_URL+Urls.GET_IMAGE);
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(oldRequest.url())
                .build();
        return chain.proceed(newRequest);
    }

    /**
     * 根据所需接口、进行动态设置网络超时时间
     * @param oldRequest
     * @param retrofit
     */
    private void setDynamicConnectTimeout(Request oldRequest, Retrofit retrofit, String url) {
        //动态设置超时时间
        final String questUrl = oldRequest.url().url().toString();
        try {
            //1、private final okhttp3.Call.Factory callFactory;   Retrofit 的源码 构造方法中
            Field callFactoryField = retrofit.getClass().getDeclaredField("callFactory");
            callFactoryField.setAccessible(true);
            //2、callFactory = new OkHttpClient();   Retrofit 的源码 build()中
            OkHttpClient client = (OkHttpClient) callFactoryField.get(retrofit);
            //3、OkHttpClient(Builder builder)     OkHttpClient 的源码 构造方法中
            Field connectTimeoutField = client.getClass().getDeclaredField("connectTimeout");
            connectTimeoutField.setAccessible(true);
            //4、根据所需要的时间进行动态设置超时时间
            if (questUrl.contains(url)) {
                connectTimeoutField.setInt(client,CONNECT_LONG_TIMEOUT * 1000);
            } else {
                connectTimeoutField.setInt(client,CONNECT_SHORT_TIMEOUT * 1000);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
