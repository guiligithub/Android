package com.iskyun.im.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.ui.SplashActivity;
import com.iskyun.im.utils.manager.SpManager;

import java.util.List;
import java.util.Stack;

public class ActivityUtils {

    private static Stack<Activity> activities = new Stack<>();


    public static void add(Activity activity){
//        if(activities.search(activity))
    }

    public static void launcher(Activity activity, @NonNull Class<?> clz) {
        Intent intent = new Intent(activity, clz);
        if(activity != null) {
            activity.startActivity(intent);
        }
    }

    public static void launcher(Activity activity, Bundle bundle, @NonNull Class<? extends Activity> clz) {
        Intent intent = new Intent(activity, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if(activity != null) {
            activity.startActivity(intent);
        }
    }

    public static void launcherForResult(Activity activity, int requestCode, @NonNull Class<? extends Activity> clz) {
        Intent intent = new Intent(activity, clz);
        if(activity != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void launcherForResult(Activity activity, int requestCode, Bundle bundle, @NonNull Class<? extends Activity> clz) {
        Intent intent = new Intent(activity, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if(activity != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 登录token 失效
     */
    public static void tokenInvalid(){
        //解绑环信登录设备
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                SpManager.getInstance().clearUserInfo();
                Activity topActivity = ImLiveApp.get().getTopActivity();
                ActivityUtils.launcher(topActivity, SplashActivity.class);
                List<Activity> activities = ImLiveApp.get().getActivities();
                for (Activity activity : activities){
                    if(!(activity instanceof SplashActivity)){
                        activity.finish();
                    }
                }
            }

            @Override
            public void onError(int code, String error) {
                ToastUtils.showToast(error);
            }
        });
    }

    /**
     * 退出登录
     */
    public static void logout(){
        SpManager.getInstance().clearUserInfo();
        SpManager.getInstance().setToken("");

        Activity topActivity = ImLiveApp.get().getTopActivity();
        ActivityUtils.launcher(topActivity, SplashActivity.class);
        List<Activity> activities = ImLiveApp.get().getActivities();
        for (Activity activity : activities){
            if(!(activity instanceof SplashActivity)){
                activity.finish();
            }
        }
    }

    /**
     * 登录成功 销毁 main以前的activity
     */
    public static void finishActivityOfMain(){
        List<Activity> activities = ImLiveApp.get().getActivities();
        for (Activity activity : activities){
            if(!(activity instanceof MainActivity)){
                activity.finish();
            }
        }
    }

    /**
     * 栈内是否有MainActivity
     * @return
     */
    public static boolean taskHasMainActivity(){
        boolean hasMain= false;
        List<Activity> activities = ImLiveApp.get().getActivities();
        for (Activity activity : activities){
            if(activity instanceof MainActivity){
                hasMain = true;
                break;
            }
        }
        return hasMain;
    }

}
