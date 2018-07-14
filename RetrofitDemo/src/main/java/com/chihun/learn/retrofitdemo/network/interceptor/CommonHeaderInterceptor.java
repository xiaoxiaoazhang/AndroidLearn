package com.chihun.learn.retrofitdemo.network.interceptor;

import com.chihun.learn.retrofitdemo.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class CommonHeaderInterceptor implements Interceptor {
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        /**添加header这几步一定要分开写 不然header会无效 别问我为什么
         *我看了build源码 看返回了一个新的对象 猜想是要一个新的对象来接收
         * 我就只定义了一个新的对象来接受新的Request
         * 后面应该就可以，但是我没确定是否成功 ，然后我就全部都拆开了吧buider对象
         * request的新的对象都分开之后 就能看到成功了。。。。巨大的bug 真是让人头疼
         */
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("x-versionname", BuildConfig.VERSION_NAME)
                .addHeader("x-versioncode", BuildConfig.VERSION_CODE + "");
        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }
}
