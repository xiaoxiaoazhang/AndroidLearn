package com.chihun.learn.audiorecorddemo.logger;

import com.chihun.learn.audiorecorddemo.BuildConfig;

public class Log {
    private static String TAG = "AUDIO_RECORD_DEMO";
    private static boolean ENABLE = BuildConfig.DEBUG;

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void d(String log) {
        if (ENABLE) {
            android.util.Log.d(TAG, log);
        }
    }

    public static void d(String tag, String log) {
        if (ENABLE) {
            android.util.Log.d(tag, log);
        }
    }

    public static void i(String log) {
        if (ENABLE) {
            android.util.Log.i(TAG, log);
        }
    }

    public static void i(String tag, String log) {
        if (ENABLE) {
            android.util.Log.i(tag, log);
        }
    }

    public static void e(String log) {
        if (ENABLE) {
            android.util.Log.e(TAG, log);
        }
    }

    public static void e(String tag, String log) {
        if (ENABLE) {
            android.util.Log.e(tag, log);
        }
    }
}
