package com.chihun.learn.retrofitdemo;

import android.app.Application;

public class MyApp extends Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }
}
