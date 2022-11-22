package com.iskyun.im.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.utils.manager.ExecutorManager;

import java.lang.reflect.Field;

/**
 * Toast工具类，统一Toast样式，处理重复显示的问题，处理7.1.x版本crash的问题
 */
public class ToastUtils {

    private static final int TOAST_LAST_TIME = 1000;
    private static Toast toast;


    /**
     * 弹出默认的toast
     */
    public static void showToast(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        showBottomToast(message, TOAST_LAST_TIME);
    }

    /**
     * 弹出默认的toast
     */
    public static void showToast(@StringRes int message) {
        showBottomToast(message, TOAST_LAST_TIME);
    }

    /**
     * 弹出toast，无图标，无标题，可以设置显示时长
     */
    public static void showToast(String message, int duration) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        showCenterToast(message, duration);
    }

    /**
     * 弹出toast，无图标，无标题，可以设置显示时长
     *
     */
    public static void showToast(@StringRes int message, int duration) {
        showCenterToast(message, duration);
    }

    /**
     * 在屏幕中部显示，在此处传入application
     *
     */
    public static void showCenterToast(String message, int duration) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        showToast(ImLiveApp.get(), message, duration, Gravity.CENTER);
    }

    /**
     * 在屏幕中部显示，在此处传入application
     *
     */
    public static void showCenterToast(@StringRes int message, int duration) {
        showToast(ImLiveApp.get(), message, duration, Gravity.CENTER);
    }

    /**
     * 在屏幕底部显示，在此处传入application
     */
    public static void showBottomToast(String message, int duration) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        showToast(ImLiveApp.get(), message, duration, Gravity.BOTTOM);
    }

    /**
     * 在屏幕底部显示，在此处传入application
     */
    public static void showBottomToast(@StringRes int message, int duration) {
        showToast(ImLiveApp.get(), message, duration, Gravity.BOTTOM);
    }

    /**
     * 此处判断toast不为空，选择cancel，是因为toast因为类型不同（是否显示图片）或者是否有标题，会导致不同的toast展示
     *
     */
    public static void showToast(Context context, @StringRes int message, int duration, int gravity) {
        showToast(context, context.getString(message), duration, gravity);
    }

    /**
     * 此处判断toast不为空，选择cancel，是因为toast因为类型不同（是否显示图片）或者是否有标题，会导致不同的toast展示
     */
    public static void showToast(Context context, String message, int duration, int gravity) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        //保证在主线程中展示toast
        ExecutorManager.getInstance().runOnMainThread(() -> {
            if (toast != null) {
                toast.cancel();
            }
            toast = getToast(context, message, duration, gravity);
            toast.show();
        });

    }

    private static Toast getToast(Context context, String message, int duration, int gravity) {
        Toast toast = new Toast(context);
        View toastView = LayoutInflater.from(context).inflate(R.layout.view_toast_layout, null);
        toast.setView(toastView);
        TextView tvToastContent = toastView.findViewById(R.id.toast_tv_content);

        if (!TextUtils.isEmpty(message)) {
            tvToastContent.setText(message);
        }
        int yOffset = 0;
        if (gravity == Gravity.BOTTOM || gravity == Gravity.TOP) {
            yOffset = (int) DisplayUtils.dp2px(50);
        }
        toast.setDuration(duration);
        toast.setGravity(gravity, 0, yOffset);
        hookToast(toast);
        return toast;
    }

    /**
     * 为了解决7.1.x版本toast可以导致crash的问题
     *
     */
    private static void hookToast(Toast toast) {
        Class<Toast> cToast = Toast.class;
        try {
            //TN是private的
            @SuppressLint("SoonBlockedPrivateApi") Field fTn = cToast.getDeclaredField("mTN");
            fTn.setAccessible(true);

            //获取tn对象
            Object oTn = fTn.get(toast);
            //获取TN的class，也可以直接通过Field.getType()获取。
            Class<?> cTn = oTn.getClass();
            Field fHandle = cTn.getDeclaredField("mHandler");

            //重新set->mHandler
            fHandle.setAccessible(true);
            fHandle.set(oTn, new HandlerProxy((Handler) fHandle.get(oTn)));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class HandlerProxy extends Handler {

        private final Handler mHandler;

        public HandlerProxy(Handler handler) {
            this.mHandler = handler;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                mHandler.handleMessage(msg);
            } catch (WindowManager.BadTokenException e) {
                //ignore
            }
        }
    }

}
