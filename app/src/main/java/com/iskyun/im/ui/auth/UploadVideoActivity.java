package com.iskyun.im.ui.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.sdk.android.oss.OSS;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.AuthStatus;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.CallShowBody;
import com.iskyun.im.data.req.EffectType;
import com.iskyun.im.databinding.ActivityUploadVideoBinding;
import com.iskyun.im.ui.dialog.AppLoadingDialog;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.common.VideoPlayActivity;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.OSSUploadUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.ExecutorManager;
import com.iskyun.im.utils.manager.PermissionsManager;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.auth.viewomodel.CallShowViewModel;
import com.iskyun.im.viewmodel.FileUploadViewModel;

import java.util.ArrayList;

/**
 * 来电展示
 */
public class UploadVideoActivity extends BaseActivity<ActivityUploadVideoBinding, FileUploadViewModel> {

    public static final String UPLOAD_TYPE = "TYPE";
    public static final String UPLOAD_PATH = "PATH";
    public static final int VIDEO_AUTH = 0x10;
    public static final int VIDEO_CALL_SHOW = 0x11;
    private int uploadType;
    private final MutableLiveData<String> videoPathLiveData = new MediatorLiveData<>();
    //上传后的路径
    private final MutableLiveData<String> uploadLiveData = new MediatorLiveData<>();
    private AppLoadingDialog loadingDialog;
    private OSS oss;
    private User user;
    private String urlPath;
    private int authStatus;
    private CallShowViewModel callShowViewModel;
    private CallShowBody callShowBody;

    @Override
    public FileUploadViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(FileUploadViewModel.class);
    }

    @Override
    public ActivityUploadVideoBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityUploadVideoBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        user = SpManager.getInstance().getCurrentUser();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uploadType = bundle.getInt(UPLOAD_TYPE, VIDEO_AUTH);
            urlPath = bundle.getString(AuthActivity.AUTH_TYPE);
            authStatus = bundle.getInt(AuthActivity.AUTH_STATUS);
            if(!TextUtils.isEmpty(urlPath)){
                if(authStatus == AuthStatus.UN_PASS.getAuthStatus() || authStatus == 0){
                    //setVideoVisibility(false);
                    mViewBinding.videoUploadSubmit.setEnabled(true);

                }else {
                    mViewBinding.viewUploadIvDelete.setVisibility(View.GONE);
                    mViewBinding.videoUploadSubmit.setEnabled(false);
                }
                setVideoVisibility(true);
                ImageLoader.get().load(mViewBinding.viewUploadIv, urlPath);
            }else {
                setVideoVisibility(false);
            }
        }else {
            setVideoVisibility(false);
        }
        initData();
        mViewBinding.viewUploadIvDelete.setOnClickListener(this::onClick);
        mViewBinding.viewUploadIv.setOnClickListener(this::onClick);

        videoPathLiveData.observe(this, s ->
                mViewBinding.videoUploadSubmit.setEnabled(!TextUtils.isEmpty(s))
        );

        loadingDialog = AppLoadingDialog.get(this);

        getStsToken();

        uploadLiveData.observe(this, s -> {
            Intent data = new Intent();
            data.putExtra(UPLOAD_TYPE, uploadType);
            data.putExtra(AuthActivity.AUTH_TYPE, s);
            setResult(RESULT_OK, data);
            finish();
        });

        callShowViewModel = new ViewModelProvider(this).get(CallShowViewModel.class);
        callShowViewModel.observerCallShow().observe(this, s -> {
            if(callShowBody!= null){
                runOnUiThread(() -> {
                    uploadLiveData.setValue(callShowBody.getFileUrl());
                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                });
            }
        });
    }

    private void getStsToken() {
        mViewModel.getStsToken().observe(this, credentials -> {
            oss = OSSUploadUtils.createOssClient(credentials.getAccessKeyId(),
                    credentials.getAccessKeySecret(),
                    credentials.getSecurityToken());
        });
    }


    @Override
    public int getTitleText() {
        return R.string.video_auth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode || data == null) {
            return;
        }
        if (requestCode == UploadType.VIDEO.getValue()) {
            ArrayList<Photo> resultPhotos =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
            if (resultPhotos != null && !resultPhotos.isEmpty()) {
                Photo photo = resultPhotos.get(0);
                if (photo.uri != null) {
                    onActivityResultForVideo(photo.uri);
                }
            }
        } else if (requestCode == UploadType.CAMERA_VIDEO.getValue()) {
            Uri uri = data.getData();
            if (uri != null) {
                onActivityResultForVideo(uri);
            }
        }
    }

    private void onActivityResultForVideo(Uri uri) {
        String path = FileUtils.getRealPathFromURI(uri);
        MediaMetadataRetriever mmr = FileUtils.getMediaMetadataRetriever(path);
        Bitmap bitmap = mmr.getFrameAtTime();
        String metadataDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int duration = Integer.parseInt(metadataDuration);
        if (duration > Constant.VIDEO_MAX_DURATION) {
            ToastUtils.showToast(String.format(DisplayUtils.getString(R.string.video_too_long_tips),
                    Constant.VIDEO_DURATION));
            return;
        }
        setVideoVisibility(true);
        videoPathLiveData.setValue(path);
        mViewBinding.viewUploadIv.setImageBitmap(bitmap);
    }

    private void setVideoVisibility(boolean visible) {
        mViewBinding.videoUploadVideoGroup.setVisibility(visible ? View.VISIBLE : View.GONE);
        mViewBinding.videoUploadIconGroup.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_upload_view_upload:
                if (TextUtils.isEmpty(videoPathLiveData.getValue())) {
                    selectVideos();
                }
                break;
            case R.id.video_upload_submit:
                submit();
                break;
            case R.id.view_upload_iv:
                if (!TextUtils.isEmpty(videoPathLiveData.getValue())) {
                    VideoPlayActivity.launcherVideoPlay(this, videoPathLiveData.getValue());
                }else if(!TextUtils.isEmpty(urlPath)){
                    VideoPlayActivity.launcherVideoPlay(this, urlPath);
                }
                break;
            case R.id.view_upload_iv_delete:
                videoPathLiveData.setValue("");
                mViewBinding.viewUploadIv.setImageDrawable(null);
                setVideoVisibility(false);
                break;
        }
    }

    private void submit() {
        if (oss == null)
            return;
        /*
         * 压缩上传
         */
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        ExecutorManager.getInstance().runOnIOThread(() -> {
            final String compressVideoPath = FileUtils.compressVideo(videoPathLiveData.getValue());
            String uploadFileAddress = OSSUploadUtils.ossUpload(oss, compressVideoPath);
            if(uploadType == VIDEO_CALL_SHOW){
                callShowBody = new CallShowBody();
                callShowBody.setAuthStatus(0);
                callShowBody.setEffectType(EffectType.CALL_SHOW.getEffectType());
                callShowBody.setFileUrl(uploadFileAddress);
                callShowViewModel.callShow(callShowBody);
            }else {
                runOnUiThread(() -> {
                    uploadLiveData.setValue(uploadFileAddress);
                    if (loadingDialog != null && loadingDialog.isShowing())
                        loadingDialog.dismiss();
                });
            }
        });

    }

    private void selectVideos() {
        PermissionsManager.getInstance().permissions(this, this::uploadGrantedLater,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void uploadGrantedLater() {
        if (uploadType == VIDEO_AUTH) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //设置质量
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            //限制大小
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1);
            //限制时长s
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
            startActivityForResult(intent, UploadType.CAMERA_VIDEO.getValue());
        } else {
            EasyPhotos.createAlbum(this, true, true, GlideEngine.getInstance())
                    .setFileProviderAuthority(Constant.AUTHORITY)
                    .setCount(1)
                    .filter(Type.VIDEO)
                    .start(UploadType.VIDEO.getValue());
        }
    }

    private void initData() {
        if (uploadType == VIDEO_AUTH) {
            mViewBinding.videoUploadTvTips.setText(R.string.auth_requirements);
            mViewBinding.videoUploadTvExplain.setText(getAuthVideoTips());
            mTvTitle.setText(R.string.video_auth);
        } else {
            mViewBinding.videoUploadTvTips.setText(R.string.call_show_video_explain);
            mViewBinding.videoUploadTvExplain.setText(getCallShowTips());
            mTvTitle.setText(R.string.call_show_video);
        }
    }

    private String getCallShowTips() {
        String symbol = "；";
        String lineBreak = "\n";
        String text1 = DisplayUtils.getString(R.string.call_show_video_1);
        String text2 = DisplayUtils.getString(R.string.call_show_video_2);
        String text3 = DisplayUtils.getString(R.string.call_show_video_3);
        String text4 = DisplayUtils.getString(R.string.call_show_video_4);
        StringBuilder sb = new StringBuilder();
        sb.append("1、").append(text1).append(symbol).append(lineBreak)
                .append("2、").append(text2).append(symbol).append(lineBreak)
                .append("3、").append(text3).append(symbol).append(lineBreak)
                .append("4、").append(text4);
        return sb.toString();
    }

    private String getAuthVideoTips() {
        String symbol = "。";
        String lineBreak = "\n";
        String text1 = DisplayUtils.getString(R.string.auth_video_1);
        String text2 = DisplayUtils.getString(R.string.auth_video_2);
        StringBuilder sb = new StringBuilder();
        sb.append("1、").append(text1).append(symbol).append(lineBreak)
                .append("2、").append(text2).append(symbol);
        return sb.toString();
    }

}