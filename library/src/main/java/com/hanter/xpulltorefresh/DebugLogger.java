package com.hanter.xpulltorefresh;

import android.util.Log;

/**
 * 类名：DebugLogger <br/>
 * 描述：调试使用Log
 * 创建时间：2017/02/19 12:22
 *
 * @author hanter
 * @version 1.0
 */
public class DebugLogger {

    private static final boolean DEBUG = false;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

}
