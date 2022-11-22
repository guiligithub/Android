package com.iskyun.im.ui.frag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.databinding.FragmentChatOtherTipBinding;

import java.lang.reflect.Field;

public class ChatOtherTipDialogFragment extends BaseDialogFragment<FragmentChatOtherTipBinding> implements View.OnClickListener {

    public TextView mTvDialogTitle,mTvDialogSubContent;
    public Button mBtnDialogCancel;
    public Button mBtnDialogConfirm;
    public ChatOtherTipDialogFragment.OnConfirmClickListener mOnConfirmClickListener;
    public ChatOtherTipDialogFragment.onCancelClickListener mOnCancelClickListener;

    public String title;
    private int confirmColor;
    private String confirm;
    private boolean showCancel;
    private int titleColor;
    private String cancel;
    public String content, subContent;


    @Override
    public void onStart() {
        super.onStart();
        setDialogPaddingParams();
    }

    @Override
    public FragmentChatOtherTipBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentChatOtherTipBinding.inflate(inflater);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mBtnDialogCancel =  mViewBinding.vipTipBtnCancel;
        mBtnDialogConfirm =  mViewBinding.vipTipBtnConfirm;
        mTvDialogTitle = mViewBinding.chatOtherTipTvContent;
        mTvDialogSubContent = mViewBinding.chatOtherTipTvSubContent;

        mBtnDialogCancel.setOnClickListener(this);
        mBtnDialogConfirm.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            mTvDialogTitle.setText(title);
        }
        if (titleColor != 0) {
            mTvDialogTitle.setTextColor(titleColor);
        }
        if (!TextUtils.isEmpty(confirm)) {
            mBtnDialogConfirm.setText(confirm);
        }
        if (!TextUtils.isEmpty(subContent)) {
            mTvDialogSubContent.setText(subContent);
        }
        if (confirmColor != 0) {
            mBtnDialogConfirm.setTextColor(confirmColor);
        }
        if (!TextUtils.isEmpty(cancel)) {
            mBtnDialogCancel.setText(cancel);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vip_tip_btn_cancel:
                onCancelClick(v);
                break;
            case R.id.vip_tip_btn_confirm:
                onConfirmClick(v);
                break;
        }
    }

    /**
     * 点击了取消按钮
     *
     * @param v
     */
    public void onCancelClick(View v) {
        dismiss();
        if (mOnCancelClickListener != null) {
            mOnCancelClickListener.onCancelClick(v);
        }
    }

    /**
     * 点击了确认按钮
     *
     * @param v
     */
    public void onConfirmClick(View v) {
        dismiss();
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onConfirmClick(v);
        }
    }

    /**
     * 确定事件的点击事件
     */
    public interface OnConfirmClickListener {
        void onConfirmClick(View view);
    }

    /**
     * 点击取消
     */
    public interface onCancelClickListener {
        void onCancelClick(View view);
    }

    /**
     * 设置确定按钮的点击事件
     *
     * @param listener
     */
    public void setOnConfirmClickListener(ChatOtherTipDialogFragment.OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    /**
     * 设置取消事件
     *
     * @param cancelClickListener
     */
    public void setOnCancelClickListener(ChatOtherTipDialogFragment.onCancelClickListener cancelClickListener) {
        this.mOnCancelClickListener = cancelClickListener;
    }

    public static class Builder {
        public FragmentActivity context;
        private String title;
        private int titleColor;
        private float titleSize;
        private boolean showCancel;
        private String confirmText;
        private OnConfirmClickListener listener;
        private onCancelClickListener cancelClickListener;
        private int confirmColor;
        private Bundle bundle;
        private String cancel;
        private String content;
        private String subContent;

        public Builder(FragmentActivity context) {
            this.context = context;
        }

        public ChatOtherTipDialogFragment.Builder setTitle(@StringRes int title) {
            this.title = context.getString(title);
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setSubContent(String subContent) {
            this.subContent = subContent;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setTitleColor(@ColorRes int color) {
            this.titleColor = ContextCompat.getColor(context, color);
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setTitleColorInt(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setTitleSize(float size) {
            this.titleSize = size;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setContent(@StringRes int content) {
            this.content = context.getString(content);
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder showCancelButton(boolean showCancel) {
            this.showCancel = showCancel;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnConfirmClickListener(@StringRes int confirm, OnConfirmClickListener listener) {
            this.confirmText = context.getString(confirm);
            this.listener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnConfirmClickListener(String confirm, OnConfirmClickListener listener) {
            this.confirmText = confirm;
            this.listener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnConfirmClickListener(OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setConfirmColor(@ColorRes int color) {
            this.confirmColor = ContextCompat.getColor(context, color);
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setConfirmColorInt(@ColorInt int color) {
            this.confirmColor = color;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnCancelClickListener(@StringRes int cancel, onCancelClickListener listener) {
            this.cancel = context.getString(cancel);
            this.cancelClickListener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnCancelClickListener(String cancel, onCancelClickListener listener) {
            this.cancel = cancel;
            this.cancelClickListener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setOnCancelClickListener(onCancelClickListener listener) {
            this.cancelClickListener = listener;
            return this;
        }

        public ChatOtherTipDialogFragment.Builder setArgument(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public ChatOtherTipDialogFragment build() {
            ChatOtherTipDialogFragment fragment = getFragment();
            fragment.setTitle(title);
            fragment.setTitleColor(titleColor);
            fragment.setContent(content);
            fragment.setSubContent(subContent);
            fragment.showCancelButton(showCancel);
            fragment.setConfirmText(confirmText);
            fragment.setOnConfirmClickListener(this.listener);
            fragment.setConfirmColor(confirmColor);
            fragment.setCancelText(cancel);
            fragment.setOnCancelClickListener(cancelClickListener);
            fragment.setArguments(bundle);
            return fragment;
        }

        protected ChatOtherTipDialogFragment getFragment() {
            return new ChatOtherTipDialogFragment();
        }

        public ChatOtherTipDialogFragment show() {
            ChatOtherTipDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.showAllowingStateLoss(transaction, null);
            return fragment;
        }
    }


    private void setCancelText(String cancel) {
        this.cancel = cancel;
    }

    private void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private void showCancelButton(boolean showCancel) {
        this.showCancel = showCancel;
    }

    private void setConfirmText(String confirmText) {
        this.confirm = confirmText;
    }

    private void setConfirmColor(int confirmColor) {
        this.confirmColor = confirmColor;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setContent(String content) {
        this.content = content;
    }
    private void setSubContent(String subContent) {
        this.subContent = subContent;
    }

    private int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = ChatOtherTipDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = ChatOtherTipDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = ChatOtherTipDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = ChatOtherTipDialogFragment.class.getDeclaredField("mBackStackId");
            backStackId.setAccessible(true);
            backStackId.set(this, mBackStackId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mBackStackId;
    }
}
