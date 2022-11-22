package com.iskyun.im.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.iskyun.im.utils.DisplayUtils;

public abstract class BaseDialogFragment<VB extends ViewBinding> extends DialogFragment {
    public FragmentActivity mContext;
    protected VB mViewBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArgument();
        mViewBinding = onCreateViewBinding(inflater);
        setChildView(mViewBinding.getRoot());
        setDialogAttrs();
        return mViewBinding.getRoot();
    }

    public void setChildView(View view) {}

    public abstract VB onCreateViewBinding(LayoutInflater inflater);

    private void setDialogAttrs() {
        try {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initArgument() {}

    public void initView(Bundle savedInstanceState) {}

    public void initListener() {}

    public void initData() {}

    /**
     * 通过id获取当前view控件，需要在onViewCreated()之后的生命周期调用
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    /**
     * 默认0.6的阴影宽度占满
     */
    public void setDialogParams() {
        setDialogParams(0.6f);
    }

    /**
     * dialog宽度占满，高度自定义 自定义阴影
     */
    public void setDialogParams(float dimAmount){
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = dimAmount;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.gravity =  Gravity.BOTTOM;
            setDialogParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogPaddingParams(){
        int width = DisplayUtils.getWidthPixels() - 4*DisplayUtils.dp2px(16);
        setDialogParams(width, ViewGroup.LayoutParams.WRAP_CONTENT, 0.6f);
    }

    /**
     * dialog全屏
     */
    public void setDialogFullParams() {
        int dialogHeight = getContextRect(mContext);
        int height = dialogHeight == 0? ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight;
        setDialogParams(ViewGroup.LayoutParams.MATCH_PARENT, height, 0.0f);
    }

    public void setDialogParams(int width, int height, float dimAmount) {
        try {
            Window dialogWindow = getDialog().getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.dimAmount = dimAmount;
            lp.width = width;
            lp.height = height;
            dialogWindow.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDialogParams(WindowManager.LayoutParams layoutParams) {
        try {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取内容区域
    private int getContextRect(Activity activity){
        //应用区域
        Rect outRect1 = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        return outRect1.height();
    }

}
