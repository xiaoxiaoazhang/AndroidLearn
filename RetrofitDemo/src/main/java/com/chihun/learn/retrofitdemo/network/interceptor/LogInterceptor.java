package com.chihun.learn.retrofitdemo.network.interceptor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LogInterceptor implements Interceptor {

    //统一的日志输出控制，可以构造方法传入，统一控制日志
    private boolean logOpen = true;
    //log的日志TAG
    private String logTag = "CommonLog";

    public LogInterceptor() {}

    public LogInterceptor(boolean logOpen) {
        this.logOpen = logOpen;
    }

    public LogInterceptor(String logTag) {
        this.logTag = logTag;
    }

    public LogInterceptor(boolean logOpen, String logTag) {
        this.logOpen = logOpen;
        this.logTag = logTag;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = chain.request();
        long t1 = System.currentTimeMillis();//请求发起的时间
        Response response = chain.proceed(request);
        long t2 = System.currentTimeMillis();//收到响应的时间

        if (logOpen) {
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            Log.i(logTag, response.request().url() + " , use-timeMs: " + (t2 - t1) + " , data: " + responseBody.string());
        }

        return response;
    }
}
