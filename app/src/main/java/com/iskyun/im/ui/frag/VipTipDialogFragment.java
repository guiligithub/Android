package com.iskyun.im.ui.frag;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.databinding.FragmentVipTipBinding;
import com.iskyun.im.ui.common.RechargeActivity;
import com.iskyun.im.utils.ActivityUtils;

import java.lang.reflect.Field;

public class VipTipDialogFragment extends BaseDialogFragment<FragmentVipTipBinding> {

    @Override
    public FragmentVipTipBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentVipTipBinding.inflate(inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogPaddingParams();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mViewBinding.vipTipBtnCancel.setOnClickListener(v -> dismiss());

        mViewBinding.vipTipBtnConfirm.setOnClickListener(v -> {
            ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RechargeActivity.class);
            dismiss();
        });
    }

    public static VipTipDialogFragment show() {
        FragmentActivity activity = (FragmentActivity) ImLiveApp.get().getTopActivity();
        VipTipDialogFragment fragment = new VipTipDialogFragment();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragment.showAllowingStateLoss(transaction, null);
        return fragment;
    }

    private int showAllowingStateLoss(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            Field dismissed = CommonDialogFragment.class.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Field shown = CommonDialogFragment.class.getDeclaredField("mShownByMe");
            shown.setAccessible(true);
            shown.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        transaction.add(this, tag);
        try {
            Field viewDestroyed = CommonDialogFragment.class.getDeclaredField("mViewDestroyed");
            viewDestroyed.setAccessible(true);
            viewDestroyed.set(this, false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        int mBackStackId = transaction.commitAllowingStateLoss();
        try {
            Field backStackId = CommonDialogFragment.class.getDeclaredField("mBackStackId");
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
