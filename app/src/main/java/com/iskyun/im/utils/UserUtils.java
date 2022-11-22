package com.iskyun.im.utils;

import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iskyun.im.R;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.utils.glide.ImageLoader;
import com.iskyun.im.utils.manager.SpManager;

public class UserUtils {

    /**
     * 设置用户头像
     *
     * @param ivAvatar
     */
    public static void setHeaderUrl(ImageView ivAvatar) {
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            if (!TextUtils.isEmpty(user.getHeadUrl())) {
                ImageLoader.get().loadCropCircle(ivAvatar, user.getHeadUrl());
            } else {
                setDefaultHeaderUrl(ivAvatar, user.getSex());
            }
        }
    }

    /**
     * 设置用户头像
     * @param ivAvatar
     * @param sex
     * @param headUrl
     */
    public static void setHeaderUrl(ImageView ivAvatar, int sex, String headUrl) {
        if (!TextUtils.isEmpty(headUrl)) {
            ImageLoader.get().load(ivAvatar, headUrl);
        } else {
            if (sex == Sex.WOMAN.ordinal()) {
                ImageLoader.get().loadCropCircle(ivAvatar, R.mipmap.icon_recommend_woman);
            } else {
                ImageLoader.get().loadCropCircle(ivAvatar, R.mipmap.icon_recommend_man);
            }
        }
    }

    /**
     * 设置默认头像
     *
     * @param ivAvatar
     * @param sex      性别
     */
    public static void setDefaultHeaderUrl(ImageView ivAvatar, int sex) {
        if (sex == Sex.WOMAN.ordinal()) {
            ImageLoader.get().loadCropCircle(ivAvatar, R.mipmap.icon_recommend_woman);
        } else {
            ImageLoader.get().loadCropCircle(ivAvatar, R.mipmap.icon_recommend_man);
        }
    }

    /**
     * @param tvAgeAndSex
     * @param sex
     * @param age
     */
    public static void setUserSexAndAge(TextView tvAgeAndSex, int sex, int age) {
        int iconSexResId, bgSexResId;
        if (sex == Sex.WOMAN.ordinal()) {
            iconSexResId = R.mipmap.icon_sex_woman;
            bgSexResId = R.mipmap.sex_bg_woman;
        } else {
            iconSexResId = R.mipmap.icon_sex_man;
            bgSexResId = R.mipmap.sex_bg_man;
        }
        tvAgeAndSex.setCompoundDrawablesWithIntrinsicBounds(
                DisplayUtils.getDrawable(iconSexResId), null, null, null);
        tvAgeAndSex.setBackground(DisplayUtils.getDrawable(bgSexResId));
        tvAgeAndSex.setText(String.valueOf(age));
    }

    public static void setNickname(TextView tv) {
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            tv.setText(user.getNickname());
        }
    }
}
