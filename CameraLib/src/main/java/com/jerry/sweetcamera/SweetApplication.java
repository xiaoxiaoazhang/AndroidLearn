package com.jerry.sweetcamera;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * @author jerry
 * @date 2016/03/11
 */
public class SweetApplication {

    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;

    public static Context CONTEXT;


    public static void onCreate(Context context) {

        // DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        DisplayMetrics mDisplayMetrics = context.getApplicationContext().getResources()
                .getDisplayMetrics();
        SweetApplication.mScreenWidth = mDisplayMetrics.widthPixels;
        SweetApplication.mScreenHeight = mDisplayMetrics.heightPixels;

        CONTEXT = context.getApplicationContext();
    }
}
