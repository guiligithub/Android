package com.iskyun.im.ui.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.common.UploadType;
import com.iskyun.im.data.bean.IdCard;
import com.iskyun.im.databinding.ActivityVerifiedBinding;
import com.iskyun.im.utils.glide.GlideEngine;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.viewmodel.FileUploadViewModel;

import java.util.ArrayList;

/**
 *实名认证
 */
public class VerifiedActivity extends BaseActivity<ActivityVerifiedBinding, FileUploadViewModel> {
    private final ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private int mVerifiedIvIdentity = 0;

    private IdCard idCard ;

    private
    MoreActionFragment fragment = MoreActionFragment.newInstance(MoreActionFragment.FROM_USERINFO_ALBUM);

    @Override
    public FileUploadViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(FileUploadViewModel.class);
    }

    @Override
    public ActivityVerifiedBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityVerifiedBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        mViewBinding.verifiedIvIdentityFrontUpload.setOnClickListener(this::onClick);
        mViewBinding.verifiedIvIdentityBackUpload.setOnClickListener(this::onClick);
        mViewBinding.verifiedTvSubmit.setOnClickListener(this::onClick);

        if(getIntent() != null && getIntent().getExtras()!= null){
            IdCard mIdCard = (IdCard) getIntent().getExtras().getSerializable(AuthActivity.AUTH_TYPE);
            if(mIdCard != null){
                idCard = mIdCard;
                ImageLoader.get().load(mViewBinding.verifiedIvIdentityBack,idCard.getIdCardBackUrl());
                ImageLoader.get().load(mViewBinding.verifiedIvIdentityFront,idCard.getIdCardFaceUrl());
            }else {
                idCard = new IdCard();
            }
        }else {
            idCard = new IdCard();
        }
        mViewBinding.setIdCard(idCard);

        mViewModel.observerIdCard().observe(this, idCard -> {
            //ToastUtils.showToast(R.string.upload_success);
            //LiveDataBus.get().with(Constant.ID_CARD_SUCCESS).postValue(idCard);
            Intent data = new Intent();
            data.putExtra(AuthActivity.AUTH_TYPE, idCard);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verified_iv_identity_front_upload:
                fragment.setOnItemClickListener(this::onItemClick);
                fragment.show(getSupportFragmentManager(), "album");
                mVerifiedIvIdentity = 1;
                break;

            case R.id.verified_iv_identity_back_upload:
                fragment.setOnItemClickListener(this::onItemClick);
                fragment.show(getSupportFragmentManager(), "album");
                mVerifiedIvIdentity = 2;
                break;
            case R.id.verified_tv_submit:
                submit();
                break;
        }
    }

    private void submit() {
        mViewModel.uploadIdCart(idCard);
    }

    private void onItemClick(MoreActionFragment.MoreType type) {
        switch (type) {
            case ALBUM:
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority(Constant.AUTHORITY)
                        .setCount(1)
                        .setSelectedPhotos(selectedPhotoList)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null)
            return;
        ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
        if (requestCode == UploadType.ALBUM.getValue()) {
            if (resultPhotos != null && !resultPhotos.isEmpty()) {
                setImages(resultPhotos.get(0));
            }
        } else if (requestCode == UploadType.CAMERA_PIC.getValue()) {
            if (resultPhotos != null && !resultPhotos.isEmpty()) {
                setImages(resultPhotos.get(0));
            }
        }
    }

    @Override
    public int getTitleText() {
        return R.string.verified;
    }

    private void setImages(Photo photo) {
        if (photo == null)
            return;
        if (mVerifiedIvIdentity == 1) {
            mViewBinding.verifiedIvIdentityFront.setImageURI(photo.uri);
            idCard.setIdCardFaceUrl(photo.path);
        } else if (mVerifiedIvIdentity == 2) {
            mViewBinding.verifiedIvIdentityBack.setImageURI(photo.uri);
            idCard.setIdCardBackUrl(photo.path);
        }
    }

}