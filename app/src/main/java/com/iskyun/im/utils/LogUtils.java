package com.iskyun.im.utils;

import android.util.Log;

import com.iskyun.im.BuildConfig;


public class LogUtils {

    public final static boolean DEBUG = BuildConfig.DEBUG;

    private static String className;
    private static String methodName;
    private static int lineNumber;

    private LogUtils() {
        /* Protect from instantiations */
    }

    private static String createLog(String log) {
        StringBuilder buffer = new StringBuilder();
        //buffer.append(methodName);
        buffer.append("(").append(className).append(":")
                .append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (DEBUG) {
            // Throwable instance must be created before any methods
            getMethodNames(new Throwable().getStackTrace());
            Log.e(className, createLog(message));
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(className, createLog(message));
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(className, createLog(message));
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.v(className, createLog(message));
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.w(className, createLog(message));
        }
    }

    public static void wtf(String message) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.wtf(className, createLog(message));
        }
    }
}
