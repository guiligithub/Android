package com.iskyun.im.utils;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;

import com.iskyun.im.ImLiveApp;

public final class DisplayUtils {

    static Resources res;

    static {
        res = ImLiveApp.get().getResources();
    }

    public static int dp2px(float dp) {
        final float scale = res.getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(float px) {
        final float scale = res.getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(float sp) {
        final float scale = res.getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public static int getWidth() {
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.widthPixels;

    }

    public static int getHeight() {
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getWidthPixels() {
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getHeightPixels() {
        DisplayMetrics dm = res.getDisplayMetrics();
        return dm.heightPixels;
    }

    public static int getDimensionPixelSize(@DimenRes int resId) {
        return res.getDimensionPixelSize(resId);
    }

    public static float getDimension(@DimenRes int resId) {
        return res.getDimension(resId);
    }

    public static String getString(@StringRes int resId) {
        return res.getString(resId);
    }

    @ColorInt
    public static int getColor(@ColorRes int resId) {
        return res.getColor(resId);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getDrawable(@DrawableRes int resId) {
        return res.getDrawable(resId);
    }

    public static int getInteger(@IntegerRes int resId) {
        return res.getInteger(resId);
    }

}
