package com.iskyun.im.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.iskyun.im.R;

public class AppLoadingDialog extends Dialog {

    private AppLoadingDialog(@NonNull Context context) {
        super(context);
        Window window = getWindow();
        if (null != window) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public static AppLoadingDialog get(Context context) {
        AppLoadingDialog loading = new AppLoadingDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        loading.setContentView(view);
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(true);
        return loading;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (null != window) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setBackgroundDrawableResource(R.color.transparent);
            window.setGravity(Gravity.CENTER);
            attributes.dimAmount = 0;
            attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        super.show();
    }

}
