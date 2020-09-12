package com.shen.baidu.doglost.utils;

import android.util.Log;

public class LogUtils {
    private static int curLevel = 4;
    private static final int DEBUG_LEVEL = 4;
    private static final int INFO_LEVEL = 3;
    private static final int WARRING_LEVEL = 2;
    private static final int ERROR_LEVEL = 1;

    public static void d(Object object, String log) {
        if (curLevel >= DEBUG_LEVEL) {
            Log.d(object.getClass().getName(),log);
        }
    }

    public static void i(Object object, String log) {
        if (curLevel >= INFO_LEVEL) {
            Log.i(object.getClass().getName(),log);
        }
    }

    public static void w(Object object, String log) {
        if (curLevel >= WARRING_LEVEL) {
            Log.w(object.getClass().getName(),log);
        }
    }

    public static void e(Object object, String log) {
        if (curLevel >= ERROR_LEVEL) {
            Log.e(object.getClass().getName(),log);
        }
    }
}
