package com.iskyun.im.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.common.Constant;

import java.io.File;

public class AppUtils {

    /**
     * 版本号
     *
     * @return
     */
    public static int getVersionCode() {
        PackageManager manager = ImLiveApp.get().getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(ImLiveApp.get().getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.getMessage());
        }
        return code;
    }


    /**
     * 版本名
     *
     * @return
     */
    public static String getVersionName() {
        PackageManager manager = ImLiveApp.get().getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(ImLiveApp.get().getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.getMessage());
        }
        return name;
    }

    /**
     * @return
     */
    public static String getPackageName() {
        return ImLiveApp.get().getPackageName();
    }

    public static void installApk(String apkPath) {
        File apk = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(ImLiveApp.get(),
                    Constant.AUTHORITY, apk);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        }
        ImLiveApp.get().getTopActivity().startActivity(intent);
    }

    public static String getChannel() {
        PackageManager pm = ImLiveApp.get().getPackageManager();
        try {
            ApplicationInfo packageInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String channel = packageInfo.metaData.getString("CHANNEL");
            if(!TextUtils.isEmpty(channel)){
                return channel;
            }
        } catch (PackageManager.NameNotFoundException e) {
           LogUtils.e(e.getMessage());
        }
        return Constant.DEFAULT_CHANNEL;
    }

    /**
     * 屏幕亮度
     * @param context
     * @return
     */
    public static int getSystemBrightness(Context context) {
        if (null != context) {
            try {
                return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
