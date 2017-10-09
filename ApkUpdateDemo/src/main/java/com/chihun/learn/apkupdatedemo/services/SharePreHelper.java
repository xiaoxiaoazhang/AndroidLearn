package com.chihun.learn.apkupdatedemo.services;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * File Description
 * <p>
 * 作者：wzd on 2017年10月09日 20:19
 * 邮箱：wangzhenduo@yunnex.com
 */

public class SharePreHelper {

    private static SharePreHelper instance = null;
    private SharedPreferences sp;
    private SharePreHelper(Context context) {
        sp = context.getSharedPreferences("apkInfo", Context.MODE_PRIVATE);
    }
    public static SharePreHelper getInstance(Context context){
        if(null == instance){
            synchronized(SharePreHelper.class){
                if(null == instance){
                    instance = new SharePreHelper(context);
                }
            }
        }
        return instance;
    }

    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
