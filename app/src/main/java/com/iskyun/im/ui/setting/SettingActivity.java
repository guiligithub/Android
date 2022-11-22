package com.iskyun.im.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.repos.IMClientRepository;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.databinding.ActivitySettingBinding;
import com.iskyun.im.ui.dialog.AppLoadingDialog;
import com.iskyun.im.ui.frag.VersionFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.AppUtils;
import com.iskyun.im.viewmodel.VersionViewModel;

/**
 * 设置
  */
public class SettingActivity extends BaseActivity<ActivitySettingBinding, VersionViewModel> {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public VersionViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(VersionViewModel.class);
    }

    @Override
    public ActivitySettingBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivitySettingBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mViewBinding.settingCurVersion.setContent(AppUtils.getVersionName());

        initListener();
    }

    private void initListener() {
        mViewBinding.settingAboutUs.setOnClickListener(this::onClick);
        mViewBinding.settingLogout.setOnClickListener(this::onClick);
        mViewBinding.settingCurVersion.setOnClickListener(this::onClick);
        mViewBinding.settingBlacklist.setOnClickListener(this::onClick);
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.setting_about_us) {
            Intent intent = new Intent();
            intent.setClass(this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.setting_logout) {
            logout();
        } else if (id == R.id.setting_cur_version) {
            getNewVersion();
        } else if(id == R.id.setting_blacklist){
            ActivityUtils.launcher(this, BlacklistActivity.class);
        }
    }

    private void getNewVersion(){
        mViewModel.showNewestTips(true);
        mViewModel.observerNewVersion().observe(this, version -> {
            VersionFragment fragment = VersionFragment.newInstance(version);
            fragment.showNow(getSupportFragmentManager(), "version");
        });
    }

    private void logout() {
        AppLoadingDialog loadingDialog =  AppLoadingDialog.get(this);
        loadingDialog.show();
        new IMClientRepository().logout(true).observe(this,
                new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> resource) {
                parseResource(resource, new OnResourceParseCallback<Boolean>() {
                    @Override
                    public void onSuccess(@Nullable Boolean data) {
                        ActivityUtils.logout();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        showToast(getString(R.string.logout_fail));
                        loadingDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getTitleText() {
        return R.string.setting;
    }
}