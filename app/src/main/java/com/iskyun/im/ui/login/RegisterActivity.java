package com.iskyun.im.ui.login;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ActivityRegisterBinding;
import com.iskyun.im.ui.common.GuideActivity;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.frag.UserInfoItemFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.FileUtils;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.PermissionsManager;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.FileUploadViewModel;
import com.iskyun.im.viewmodel.UpdateUserViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding, UpdateUserViewModel> {

    private File cropFile;//头像
    private User user;
    private FileUploadViewModel uploadViewModel;

    @Override
    public UpdateUserViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(UpdateUserViewModel.class);
    }

    @Override
    public ActivityRegisterBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityRegisterBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        initListener();
        user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            user.setAge(25);
            user.setSex(Sex.MAN.ordinal());
            if (!TextUtils.isEmpty(user.getNickname()) && user.getNickname().length() > 10) {
                user.setNickname(user.getNickname().substring(0, 10));
            }
            mViewBinding.setUser(user);
            if (!TextUtils.isEmpty(user.getHeadUrl())) {
                ImageLoader.get().loadCropCircle(mViewBinding.registerIvAvatar, user.getHeadUrl());
            }

            if (TextUtils.isEmpty(user.getWxId())) {
                mViewModel.userRandomName();
            }
        }
        uploadViewModel = new ViewModelProvider(this).get(FileUploadViewModel.class);

        uploadViewModel.observerPaths().observe(this, strings -> {
            if (strings != null && !strings.isEmpty()) {
                user.setHeadUrl(strings.get(0));
            }
        });

        mViewModel.observerUpdate().observe(this, user -> {
            ActivityUtils.launcher(RegisterActivity.this, GuideActivity.class);
            ActivityUtils.finishActivityOfMain();
        });

        mViewModel.observerRandomName().observe(this, s -> {
            if (user != null) {
                user.setNickname(s);
            }
        });
    }

    private void initListener() {
        mViewBinding.registerRlAge.setOnClickListener(this::onClick);
        mViewBinding.registerIvAvatar.setOnClickListener(this::onClick);
        mViewBinding.registerBtnConfirm.setOnClickListener(this::onClick);
        mViewBinding.registerIvNick.setOnClickListener(this::onClick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        if (requestCode == UploadType.ALBUM.getValue()) {
            onActivityResultAlbum(data);
        } else if (requestCode == UploadType.CAMERA_PIC.getValue()) {
            onActivityResultAlbum(data);
        } else if (requestCode == UploadType.CROP.getValue()) {
            uploadAvatar();
        }
    }

    private void uploadAvatar() {
        if (cropFile != null) {
            ImageLoader.get().loadCropCircle(mViewBinding.registerIvAvatar, Uri.fromFile(cropFile));
            List<String> paths = new ArrayList<>();
            paths.add(cropFile.getAbsolutePath());
            //mViewBinding.registerBtnConfirm.setEnabled(false);
            uploadViewModel.uploads(paths);
        }
    }


    @Override
    protected void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.register_rl_age) {
            UserInfoItemFragment fragment = UserInfoItemFragment.newInstance(
                    UserInfoItemFragment.AGE, String.valueOf(user.getAge()));
            fragment.show(getSupportFragmentManager(), "age");
            fragment.setUserInfoItemClickListener(itemModel ->
                    user.setAge(Integer.parseInt(itemModel.getContent())));
        } else if (view.getId() == R.id.register_iv_avatar) {
            PermissionsManager.getInstance().permissions(this, () -> {
                        MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_AVATAR);
                        fragment.show(getSupportFragmentManager(), "avatar");
                        fragment.setOnItemClickListener(RegisterActivity.this::onItemClick);
                    }, Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (view.getId() == R.id.register_btn_confirm) {
            submit();
        } else if (view.getId() == R.id.register_iv_nick) {
            mViewModel.userRandomName();
        }
    }

    private void submit() {
        hideSoftInput();
        if (mViewBinding.registerRgSex.getCheckedRadioButtonId() == R.id.register_rb_man) {
            user.setSex(Sex.MAN.ordinal());
        } else {
            user.setSex(Sex.WOMAN.ordinal());
        }
        mViewModel.update(user);
    }

    private void onItemClick(MoreActionFragment.MoreType moreType) {
        switch (moreType) {
            case ALBUM:
                EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(1)
                        .start(UploadType.ALBUM.getValue());
                break;
            case PICTURE:
                EasyPhotos.createCamera(this, false)
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .start(UploadType.CAMERA_PIC.getValue());
                break;
            default:
                break;
        }
    }

    private void onActivityResultAlbum(Intent data) {
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (resultPhotos != null && !resultPhotos.isEmpty()) {
            cropFile = FileUtils.gotoCrop(this, resultPhotos.get(0).uri);
        }
    }

    @Override
    public int getTitleText() {
        return R.string.select_sex;
    }
}
