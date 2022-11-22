package com.iskyun.im.ui.frag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.R;
import com.iskyun.im.base.BaseDialogFragment;
import com.iskyun.im.data.bean.AppVersion;
import com.iskyun.im.data.bean.DownloadInfo;
import com.iskyun.im.databinding.FragmentUpdateVersionBinding;
import com.iskyun.im.utils.AppUtils;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.download.DownloadProgressHandler;
import com.iskyun.im.utils.manager.PermissionsManager;
import com.iskyun.im.viewmodel.VersionViewModel;

import java.io.File;


/**
 * 版本更新
 */
public class VersionFragment extends BaseDialogFragment<FragmentUpdateVersionBinding> {

    private static final String KEY_OF_VERSION = "version";
    private DownloadProgressHandler progressHandler;
    private VersionViewModel viewModel;
    private String url;

    private VersionFragment() {
    }

    public static VersionFragment newInstance(AppVersion version) {
        VersionFragment fragment = new VersionFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_OF_VERSION, version);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public FragmentUpdateVersionBinding onCreateViewBinding(LayoutInflater inflater) {
        return FragmentUpdateVersionBinding.inflate(inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogPaddingParams();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VersionViewModel.class);

        progressHandler = new DownloadProgressHandler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgress(DownloadInfo downloadInfo) {
                mViewBinding.versionPb.setProgress(downloadInfo.getProgress());
                String currentSize = FileUtils.formatFileSize(downloadInfo.getCurrentSize());
                String fileSize = FileUtils.formatFileSize(downloadInfo.getFileSize());
                mViewBinding.versionTvFileSize.setText(currentSize + "/" + fileSize);
                mViewBinding.versionTvProgress.setText(downloadInfo.getProgress() + "%");
            }

            @Override
            public void onCompleted(File file) {
                AppUtils.installApk(file.getAbsolutePath());
                dismiss();
                viewModel.cancel();
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(e.getMessage());
                ToastUtils.showToast(R.string.download_fail);
                dismiss();
                viewModel.cancel();
            }
        };
    }

    @Override
    public void initData() {
        super.initData();
        if (getArguments() == null) {
            return;
        }
        AppVersion version = getArguments().getParcelable(KEY_OF_VERSION);
        url = version.getDownloadUrl();
        mViewBinding.versionTvVersionName.setText("V"+version.getVersions()+"版本新升级");
        mViewBinding.versionTvContent.setText(version.getUpdateContent());
        if (version.isMust()) {
            mViewBinding.versionBtnCancel.setVisibility(View.GONE);
        }
        setCancelable(!version.isMust());
    }

    @Override
    public void initListener() {
        super.initListener();
        mViewBinding.versionBtnUpdate.setOnClickListener(this::onClick);
        mViewBinding.versionBtnCancel.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        int id = view.getId();
        if (id == R.id.version_btn_update) {
            PermissionsManager.getInstance().permissions(this, this::download,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if (id == R.id.version_btn_cancel) {
            dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void download() {
        mViewBinding.versionGroupPb.setVisibility(View.VISIBLE);
        mViewBinding.versionBtnCancel.setVisibility(View.GONE);
        mViewBinding.versionBtnUpdate.setVisibility(View.GONE);
        mViewBinding.versionTvFileSize.setText("0kb/0kb");
        mViewBinding.versionTvProgress.setText("0%");
        viewModel.download(url, progressHandler);
    }
}
