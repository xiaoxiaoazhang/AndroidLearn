/*
 * Copyright (c) 2008-2018 UBT Corporation.  All rights reserved.  Redistribution,
 * modification, and use in source and binary forms are not permitted unless otherwise authorized by UBT.
 *
 */

package com.chihun.learn.retrofitdemo.network;

import android.util.Log;

public class LogUtil {

    private static final String TAG = "RecognitionService";
    private final static boolean DEBUG = true;

    public static void i(String str) {

        if(!DEBUG){
            return;
        }
        Log.i(TAG, str);
    }

    /**
     * The Log Level:d
     * @param str 输出内容
     */
    public static void d(String str) {

        if(!DEBUG){
            return;
        }

        Log.i(TAG, str);
    }

    /**
     * The Log Level:v
     * @param str 输出内容
     */
    public static void v(String str) {

        if(!DEBUG){
            return;
        }
        Log.v(TAG, str);
    }

    /**
     * The Log Level:w
     * @param str 输出内容
     */
    public static void w(String str) {

        if(!DEBUG){
            return;
        }

        Log.w(TAG, str);
    }

    /**
     * The Log Level:e
     * @param str 输出内容
     */
    public static void e(String str) {

        if(!DEBUG){
            return;
        }

        Log.e(TAG, str);
    }

    public static void w(Throwable e) {
        if(!DEBUG){
            return;
        }

        Log.w(TAG, e);
    }
}
