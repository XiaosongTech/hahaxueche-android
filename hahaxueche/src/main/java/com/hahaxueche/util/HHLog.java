package com.hahaxueche.util;

import android.util.Log;

import com.hahaxueche.BuildConfig;

/**
 * Created by wangshirui on 16/9/8.
 */
public class HHLog {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "gibxin";

    public static void i(String msg) {
        if (DEBUG)
            Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (DEBUG)
            Log.e(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG)
            Log.d(TAG, msg);
    }

    public static void v(String msg) {
        if (DEBUG)
            Log.v(TAG, msg);
    }

    public static void w(String msg) {
        if (DEBUG)
            Log.w(TAG, msg);
    }
}
