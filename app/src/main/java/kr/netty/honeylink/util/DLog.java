package kr.netty.honeylink.util;

import android.util.Log;

import kr.netty.honeylink.BuildConfig;

public class DLog {

    private static final boolean IS_DEBUG = BuildConfig.DEBUG;

    public static void d(String tag, String message) {
        if (IS_DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (IS_DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (IS_DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (IS_DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (IS_DEBUG) {
            Log.w(tag, message);
        }
    }

}
