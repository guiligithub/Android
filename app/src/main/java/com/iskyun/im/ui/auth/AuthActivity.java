package com.iskyun.im.ui.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.AnchorAuth;
import com.iskyun.im.data.bean.AuthStatus;
import com.iskyun.im.data.bean.IdCard;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.UserAlbum;
import com.iskyun.im.data.req.AlbumBody;
import com.iskyun.im.data.req.AlbumTag;
import com.iskyun.im.data.req.AnchorPriceSetting;
import com.iskyun.im.databinding.ActivityAuthBinding;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.login.AppWebActivity;
import com.iskyun.im.ui.login.SmsLoginActivity;
import com.iskyun.im.ui.mine.MineAlbumActivity;
import com.iskyun.im.ui.mine.UserInfoActivity;
import com.iskyun.im.ui.setting.PriceSettingActivity;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.ui.auth.viewomodel.AuthViewModel;
import com.iskyun.im.viewmodel.UpdateUserViewModel;
import com.iskyun.im.viewmodel.UserAlbumViewModel;
import com.iskyun.im.widget.ListItemView;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends BaseActivity<ActivityAuthBinding, AuthViewModel> {

    public static final String AUTH_TYPE = "AUTH_TYPE";
    public static final String AUTH_STATUS = "STATUS";
    public static final int REQUEST_USER_INFO = 0x010;
    public static final int REQUEST_PRICE = 0x011;
    public static final int REQUEST_CALL_SHOW = 0x012;
    public static final int REQUEST_VERIFIED = 0x013;
    public static final int REQUEST_CODE = 0x014;
    public static final int REQUEST_ALBUM = 0x015;
    public static final int REQUEST_PHONE = 0x016;
    public static final int ALBUM_LIMIT = 4;

    private UserAlbumViewModel userAlbumViewModel;
    private UpdateUserViewModel updateUserViewModel;
    private AnchorAuth authBody;
    private boolean anchorInfoIsFinished = false;
    private boolean anchorAlbumFinished = false;
    //审核通过的图片集
    private final List<UserAlbum> mPassUserAlbums = new ArrayList<>();
    //审核通过中的图片
    private final List<UserAlbum> mApprovalUserAlbums = new ArrayList<>();

    @Override
    public AuthViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(AuthViewModel.class);
    }

    @Override
    public ActivityAuthBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityAuthBinding.inflate(inflater);
    }

    @Override
    public void initBase() {
        userAlbumViewModel = new ViewModelProvider(this).get(UserAlbumViewModel.class);
        updateUserViewModel = new ViewModelProvider(this).get(UpdateUserViewModel.class);
        if (SpManager.getInstance().getAnchorAuth() == null) {
            authBody = new AnchorAuth();
        } else {
            //临时保存
            authBody = SpManager.getInstance().getAnchorAuth();
        }
        mViewModel.getMyAuthDetails().observe(this, anchorAuth -> {
            authBody = anchorAuth;
            setAuthInfo();

            if(anchorAuth.getAppUserSetting() != null){
                AnchorPriceSetting priceSetting = anchorAuth.getAppUserSetting();
                SpManager.getInstance().setPriceSetting(priceSetting);
            }
            setPriceSetting();
        });
        setAuthInfo();

        mViewModel.observerAnchorAuth().observe(this, s -> finish());
        userPhotos();
        initTipContent();

        LiveDataBus.get().with(Constant.UPDATE_USER_SUCCESS, User.class).observe(this, s -> {
            setPerfectUserInfo();
        });

        userAlbumViewModel.observerUserPhotos().observe(this, userAlbums -> {
            for (UserAlbum album : userAlbums) {
                if (album.getAuthStatus() == AuthStatus.ON_PASS.getAuthStatus()) {
                    mPassUserAlbums.add(album);
                } else if (album.getAuthStatus() == AuthStatus.NO_APPROVAL.getAuthStatus()) {
                    mApprovalUserAlbums.add(album);
                }
            }
            setAlbumInfo(mPassUserAlbums.size());
        });

    }


    @Override
    public int getTitleText() {
        return R.string.anchor_auth;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        Bundle args = data.getExtras();
        if (requestCode == REQUEST_CODE) {
            setLaborCode(args.getString(AUTH_TYPE));
        } else if (requestCode == REQUEST_ALBUM) {
            userPhotos();
        } else if (requestCode == REQUEST_VERIFIED) {
            setVerifiedInfo((IdCard) args.getSerializable(AUTH_TYPE));
        } else if (requestCode == REQUEST_CALL_SHOW) {
            setVideoInfo(args.getInt(UploadVideoActivity.UPLOAD_TYPE), args.getString(AUTH_TYPE));
        } else if (requestCode == REQUEST_PRICE) {
            setPriceSetting();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.auth_view_info) {
            if (!anchorInfoIsFinished)
                ActivityUtils.launcher(this, UserInfoActivity.class);
        } else if (v.getId() == R.id.auth_view_mobile) {
            User user = SpManager.getInstance().getCurrentUser();
            if (TextUtils.isEmpty(user.getMobile())) {
                Bundle args = new Bundle();
                args.putInt(SmsLoginActivity.MSG_TYPE, SmsLoginActivity.AUTH);
                ActivityUtils.launcherForResult(this, REQUEST_VERIFIED, args, SmsLoginActivity.class);
            }
        } else if (v.getId() == R.id.auth_view_album) {
            if (mPassUserAlbums.size() < ALBUM_LIMIT) {
                ActivityUtils.launcherForResult(this, REQUEST_ALBUM, MineAlbumActivity.class);
            }
        } else if (v.getId() == R.id.auth_view_identity) {
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(authBody.getIdCardBackUrl())
                    && !TextUtils.isEmpty(authBody.getIdCardBackUrl())) {
                IdCard idCard = new IdCard();
                idCard.setIdCardBackUrl(authBody.getIdCardBackUrl());
                idCard.setIdCardFaceUrl(authBody.getIdCardFaceUrl());
                idCard.setAuthStatus(authBody.getAuthStatus());
                args.putSerializable(AUTH_TYPE, idCard);
            }
            ActivityUtils.launcherForResult(this, REQUEST_VERIFIED, args, VerifiedActivity.class);
        } else if (v.getId() == R.id.auth_view_video) {
            Bundle args = new Bundle();
            args.putInt(UploadVideoActivity.UPLOAD_TYPE, UploadVideoActivity.VIDEO_AUTH);
            if (!TextUtils.isEmpty(authBody.getVideoAuthUrl())) {
                args.putString(AUTH_TYPE, authBody.getVideoAuthUrl());
                args.putInt(AUTH_STATUS, authBody.getAuthStatus());
            }
            ActivityUtils.launcherForResult(this, REQUEST_CALL_SHOW, args, UploadVideoActivity.class);
        } else if (v.getId() == R.id.auth_view_call_show) {
            Bundle args = new Bundle();
            args.putInt(UploadVideoActivity.UPLOAD_TYPE, UploadVideoActivity.VIDEO_CALL_SHOW);
            User user = SpManager.getInstance().getCurrentUser();
            if (!TextUtils.isEmpty(user.getCoverVideoUrl())) {
                args.putString(AUTH_TYPE, user.getCoverVideoUrl());
                args.putInt(AUTH_STATUS, authBody.getAuthStatus());
            }
            ActivityUtils.launcherForResult(this, REQUEST_CALL_SHOW, args,
                    UploadVideoActivity.class);
        } else if (v.getId() == R.id.auth_view_price) {
            ActivityUtils.launcherForResult(this, REQUEST_PRICE, PriceSettingActivity.class);
        } else if (v.getId() == R.id.auth_view_code) {
            Bundle args = new Bundle();
            if (authBody.getLaborCode() != 0) {
                args.putInt(AUTH_TYPE, authBody.getLaborCode());
            }
            ActivityUtils.launcherForResult(this, REQUEST_CODE, args, InvitationCodeActivity.class);
        } else if (v.getId() == R.id.auth_btn_submit) {
            submit();
        }
    }

    /**
     *
     */
    private void userPhotos() {
        AlbumBody body = new AlbumBody();
        body.setTag(AlbumTag.MY_ALBUM.getTag());
        userAlbumViewModel.userPhotos(body, false);
    }

    private void submit() {
        if (authBody == null)
            return;
        if (!mViewBinding.authCbStandard.isChecked()) {
            ToastUtils.showToast(R.string.agree_agreement);
            return;
        }
        User user = SpManager.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(user.getCoverVideoUrl())
                || !anchorInfoIsFinished || TextUtils.isEmpty(user.getMobile())
                || !anchorAlbumFinished) {
            ToastUtils.showToast(R.string.complete_auth_information);
            return;
        }
        mViewModel.anchorAuth(authBody);
    }

    private void setPriceSetting(){
        AnchorPriceSetting anchorPriceSetting = SpManager.getInstance().getPriceSetting();
        setUserInfoStatus(mViewBinding.authViewPrice, anchorPriceSetting.getVideoMinute() != null && anchorPriceSetting.getVoiceMinute() != null
                && anchorPriceSetting.getTextPrice() != null && anchorPriceSetting.getUnlockWechat() != null);
    }

    private void setUserInfoStatus(ListItemView itemView, boolean isSelectedFinish) {
        String content;
        @ColorInt int color;
        if (isSelectedFinish) {
            content = getString(R.string.finished);
            color = getResources().getColor(R.color.colorContent);
        } else {
            content = getString(R.string.unfinished);
            color = getResources().getColor(R.color.colorAccent);
        }
        itemView.setContentColor(color);
        itemView.setContent(content);
    }

    private void setAuthInfo() {
        if (authBody != null) {
            setAuthStatus(mViewBinding.authViewCode, authBody.getLaborCode() != 0);
            setAuthStatus(mViewBinding.authViewIdentity, !TextUtils.isEmpty(authBody.getIdCardBackUrl())
                    && !TextUtils.isEmpty(authBody.getIdCardFaceUrl()));
            setAuthStatus(mViewBinding.authViewVideo, !TextUtils.isEmpty(authBody.getVideoAuthUrl()));
        }
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            setUserInfoStatus(mViewBinding.authViewMobile, !TextUtils.isEmpty(user.getMobile()));
            setUserInfoStatus(mViewBinding.authViewCallShow, !TextUtils.isEmpty(user.getCoverVideoUrl()));
            setPerfectUserInfo();
        }
    }

    /**
     * 邀请码
     * 7888
     *
     * @param laborCode
     */
    private void setLaborCode(String laborCode) {
        try {
            authBody.setLaborCode(Integer.parseInt(laborCode));
            SpManager.getInstance().setAnchorAuth(authBody);
            setAuthStatus(mViewBinding.authViewCode, true);
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * 相册
     *
     * @param albumSize
     */
    private void setAlbumInfo(int albumSize) {
        if (mPassUserAlbums.size() < ALBUM_LIMIT && !mApprovalUserAlbums.isEmpty()) {
            anchorAlbumFinished = true;
            mViewBinding.authViewAlbum.setContentColor(getResources().getColor(R.color.colorContent));
            mViewBinding.authViewAlbum.setContent(getString(R.string.under_review));
        } else if (albumSize < ALBUM_LIMIT) {
            anchorAlbumFinished = false;
            setUserInfoStatus(mViewBinding.authViewAlbum, false);
        } else {
            anchorAlbumFinished = true;
            setUserInfoStatus(mViewBinding.authViewAlbum, true);
        }
    }

    private void setVideoInfo(int type, String urlPath) {
        if (type == UploadVideoActivity.VIDEO_AUTH) {
            authBody.setVideoAuthUrl(urlPath);
            SpManager.getInstance().setAnchorAuth(authBody);
            setAuthStatus(mViewBinding.authViewVideo, true);
        } else {
            User user = SpManager.getInstance().getCurrentUser();
            user.setCoverVideoUrl(urlPath);
            SpManager.getInstance().setCurrentUser(user);
            setUserInfoStatus(mViewBinding.authViewCallShow, true);
            updateUserViewModel.update(user);
        }
    }

    private void setPerfectUserInfo() {
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            anchorInfoIsFinished = !TextUtils.isEmpty(user.getNickname())
                    && !TextUtils.isEmpty(user.getWxNumber())
                    && !TextUtils.isEmpty(user.getProfession())
                    && !TextUtils.isEmpty(user.getCity())
                    && user.getHeight() != 0
                    && user.getAffectiveState() != 0
                    && !TextUtils.isEmpty(user.getTag());
            setUserInfoStatus(mViewBinding.authViewInfo, anchorInfoIsFinished);
        }
    }

    private void setVerifiedInfo(IdCard idCard) {
        authBody.setIdCardFaceUrl(idCard.getIdCardFaceUrl());
        authBody.setIdCardBackUrl(idCard.getIdCardBackUrl());
        SpManager.getInstance().setAnchorAuth(authBody);
        setAuthStatus(mViewBinding.authViewIdentity, true);
    }

    /**
     * @param isSelectedFinish 选择完成
     */
    private void setAuthStatus(ListItemView itemView, boolean isSelectedFinish) {
        String content;
        @ColorInt int color;
        if (authBody.getAuthStatus() == AuthStatus.ON_PASS.getAuthStatus()) {
            content = getString(R.string.finished);
            color = getResources().getColor(R.color.colorContent);
        } else if (authBody.getAuthStatus() == AuthStatus.NO_APPROVAL.getAuthStatus()) {
            content = getString(R.string.under_review);
            color = getResources().getColor(R.color.colorAccent);
        } else if (authBody.getAuthStatus() == AuthStatus.UN_PASS.getAuthStatus()) {
            content = getString(R.string.un_pass);
            color = getResources().getColor(R.color.colorAccent);
        } else {
            if (isSelectedFinish) {
                content = getString(R.string.finished);
                color = getResources().getColor(R.color.colorContent);
            } else {
                content = getString(R.string.unfinished);
                color = getResources().getColor(R.color.colorAccent);
            }
        }
        itemView.setContentColor(color);
        itemView.setContent(content);
    }


    private void initTipContent() {
        setSpannableContent(DisplayUtils.getString(R.string.invitation_code_tips),
                DisplayUtils.getString(R.string.online), mViewBinding.authTvInvitationTips, true);

        setSpannableContent(DisplayUtils.getString(R.string.auth_promise),
                "《" + DisplayUtils.getString(R.string.auth_standard) + "》",
                mViewBinding.authTvStandard, false);
    }


    /**
     *
     */
    private void setSpannableContent(String tipsContent, String spanContent,
                                     TextView tvContent, boolean underline) {
        tvContent.setText(tipsContent);
        SpannableString spannableContent = new SpannableString(spanContent);
        spannableContent.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if(underline){

                }else {
                    Intent intent = new Intent();
                    intent.setClass(ImLiveApp.get().getTopActivity(), AppWebActivity.class);
                    intent.putExtra(com.mobile.auth.gatewayauth.Constant.PROTOCOL_WEBVIEW_URL,
                            Constant.EMPLOYEE_AGREEMENT);
                    intent.putExtra(com.mobile.auth.gatewayauth.Constant.PROTOCOL_WEBVIEW_NAME,
                            DisplayUtils.getString(R.string.auth_standard));
                    startActivity(intent);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(DisplayUtils.getColor(R.color.colorAccent));
                if (underline) {
                    ds.setUnderlineText(true);
                }
            }
        }, 0, spanContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.append(spannableContent);
        tvContent.setHighlightColor(Color.TRANSPARENT);
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
    }
}