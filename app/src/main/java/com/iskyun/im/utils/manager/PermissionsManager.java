package com.iskyun.im.utils.manager;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.tbruyelle.rxpermissions3.RxPermissions;


public class PermissionsManager {

    private static PermissionsManager rxPermissionsManager;

    private PermissionsManager() {}

    public static PermissionsManager getInstance() {
        if (rxPermissionsManager == null) {
            synchronized (PermissionsManager.class) {
                if (rxPermissionsManager == null) {
                    rxPermissionsManager = new PermissionsManager();
                }
            }
        }

        return rxPermissionsManager;
    }

    public interface OnPermissionsCallback {
        void granted();//允许
    }

    public void permissions(FragmentActivity activity, OnPermissionsCallback callback, String... permissions) {
        RxPermissions rxPermissionsActivity = new RxPermissions(activity);
        rxPermissionsActivity.requestEachCombined(permissions)
                .subscribe(permission -> {
                    if (permission.granted) {
                        LogUtils.i("granted:" + permission.name);
                        callback.granted();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        LogUtils.i("ask each:" + permission.name);
                    } else {
                        LogUtils.i("denied:"+permission.name);
                        openSetting();
                    }
                });
    }

    public void permissions(Fragment fragment, OnPermissionsCallback callback,
                                      String... permissions) {
        RxPermissions rxPermissionsFragment = new RxPermissions(fragment);
        rxPermissionsFragment.requestEachCombined(permissions)
                .subscribe(permission -> {
                    if (permission.granted) {
                        LogUtils.e("granted:" + permission.name);
                        callback.granted();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        LogUtils.e("ask each:" + permission.name);
                    } else {
                        LogUtils.e("denied:"+permission.name);
                        openSetting();
                    }
                });
    }

    private void openSetting(){
        ToastUtils.showToast(R.string.open_permissions);
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + ImLiveApp.get().getPackageName()));
        settingIntent.addCategory(Intent.CATEGORY_DEFAULT);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ImLiveApp.get().startActivity(settingIntent);
    }
}