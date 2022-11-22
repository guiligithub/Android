package com.iskyun.im.utils.manager;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hyphenate.easeui.domain.EaseUser;
import com.iskyun.im.data.bean.AnchorAuth;
import com.iskyun.im.data.bean.SVipCallCount;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.req.AnchorPriceSetting;
import com.iskyun.im.utils.AESUtils;
import com.iskyun.im.utils.GsonUtils;

public class SpManager {
    /**
     * name of preference
     */
    public static final String PREFERENCE_NAME = "saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static SpManager mSpManager;
    private static SharedPreferences.Editor editor;

    private static final String UMENG_INIT = "UMENG_INIT";
    private static final String UUID = "UUID";
    private static final String TOKEN = "TOKEN";

    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    private static final String CURRENT_USER = "CURRENT_USER";
    private static final String CURRENT_EASE_USER = "CURRENT_EASE_USER";
    private static final String ANCHOR_AUTH = "ANCHOR_AUTH";
    private static final String CURRENT_USER_PASSWORD = "CURRENT_USER_PASSWORD";
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";

    private static final String SHARED_KEY_MSG_ROAMING = "SHARED_KEY_MSG_ROAMING";
    private static final String SHARED_KEY_SHOW_MSG_TYPING = "SHARED_KEY_SHOW_MSG_TYPING";


    private static final String PLAY_VIDEO_VOICE = "PLAY_VIDEO_VOICE";
    private static final String AES_KEY = "AES_KEY";//加密key

    private static final String INIT_BLACKLIST = "INIT_BLACKLIST";// 黑名单
    private static final String SETTING_PRICE = "SETTING_PRICE";// 黑名单
    private static final String INIT_ONLINE_SERVICE = "INIT_ONLINE_SERVICE";// 在线客服
    private static final String SHARE_TRACE = "SHARE_TRACE";//
    private static final String INIT_SHARE_TRACE = "INIT_SHARE_TRACE";//


    @SuppressLint("CommitPrefEdits")
    private SpManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mSpManager == null) {
            mSpManager = new SpManager(cxt);
        }
    }

    public synchronized static SpManager getInstance() {
        if (mSpManager == null) {
            throw new RuntimeException("please init first!");
        }

        return mSpManager;
    }

    /**
     * 友盟初始化
     */
    public boolean getUmengInit() {
        return mSharedPreferences.getBoolean(UMENG_INIT, false);
    }

    public void setUmengInit(Boolean isInit) {
        editor.putBoolean(UMENG_INIT, isInit);
        editor.apply();
    }

    public int getCurrentUserId() {
        return mSharedPreferences.getInt(CURRENT_USER_ID, 0);
    }

    public void setCurrentUserId(int userId) {
        editor.putInt(CURRENT_USER_ID, userId);
        editor.apply();
    }

    public void setCurrentUser(User user) {
        if (user == null) {
            editor.putString(CURRENT_USER, "");
        } else {
            String aesKey = getAesKey();
            if(TextUtils.isEmpty(aesKey)){
                aesKey = AESUtils.generateKey();
                setAesKey(aesKey);
            }
            String encryptContent = AESUtils.encrypt(GsonUtils.toJson(user));
            editor.putString(CURRENT_USER, encryptContent);
        }
        editor.apply();
    }

    public User getCurrentUser() {
        if(TextUtils.isEmpty(getAesKey())){
            clearUserInfo();
            //ToastUtils.showToast(R.string.login_again);
            return null;
        }
        String current = mSharedPreferences.getString(CURRENT_USER, "");
        String decryptContent = AESUtils.decrypt(current);
        return GsonUtils.fromJson(decryptContent, User.class);
    }

    public void setCurrentEaseUser(EaseUser easeUser) {
        if (easeUser == null) {
            editor.putString(CURRENT_EASE_USER, "");
        } else {
            editor.putString(CURRENT_EASE_USER, GsonUtils.toJson(easeUser));
        }
        editor.apply();
    }

    public EaseUser getCurrentEaseUser() {
        String current = mSharedPreferences.getString(CURRENT_EASE_USER, "");
        return GsonUtils.fromJson(current, EaseUser.class);
    }

    public String getCurrentUserPwd() {
        return mSharedPreferences.getString(CURRENT_USER_PASSWORD, null);
    }

    public AnchorAuth getAnchorAuth() {
        String current = mSharedPreferences.getString(ANCHOR_AUTH, "");
        return GsonUtils.fromJson(current, AnchorAuth.class);
    }

    public void setAnchorAuth(AnchorAuth auth) {
        editor.putString(ANCHOR_AUTH, GsonUtils.toJson(auth));
        editor.apply();
    }

    public void setCurrentUserPwd(String pwd) {
        editor.putString(CURRENT_USER_PASSWORD, pwd);
        editor.apply();
    }

    public void setCurrentUserName(String username) {
        editor.putString(CURRENT_USER_NAME, username);
        editor.apply();
    }

    public String getCurrentUsername() {
        return mSharedPreferences.getString(CURRENT_USER_NAME, null);
    }

    public boolean isMsgRoaming() {
        return mSharedPreferences.getBoolean(SHARED_KEY_MSG_ROAMING, false);
    }

    public boolean getVoiceOpened() {
        return mSharedPreferences.getBoolean(PLAY_VIDEO_VOICE, true);
    }

    public void setVoiceOpened(boolean opened) {
        editor.putBoolean(PLAY_VIDEO_VOICE, opened);
        editor.apply();
    }

    public void setMsgRoaming(boolean isRoaming) {
        editor.putBoolean(SHARED_KEY_MSG_ROAMING, isRoaming);
        editor.apply();
    }

    public boolean isShowMsgTyping() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SHOW_MSG_TYPING, false);
    }

    public void showMsgTyping(boolean show) {
        editor.putBoolean(SHARED_KEY_SHOW_MSG_TYPING, show);
        editor.apply();
    }

    public void setUuid(String uuid) {
        editor.putString(UUID, uuid);
        editor.apply();
    }

    public String getUuid() {
        return mSharedPreferences.getString(UUID, "");
    }

    /**
     * 加密key
     * @param aesKey
     */
    public void setAesKey(String aesKey) {
        editor.putString(AES_KEY, aesKey);
        editor.apply();
    }

    public String getAesKey() {
        return mSharedPreferences.getString(AES_KEY, "");
    }

    public void setToken(String token) {
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return mSharedPreferences.getString(TOKEN, "");
    }

    /**
     * 设置钻石余额
     * <p>
     * consumeDiamond  消费的钻石
     */
    public static void setDiamondBalance(int consumeDiamond) {
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            int balance = user.getUserDiamond() - consumeDiamond;
            user.setUserDiamond(balance);
            SpManager.getInstance().setCurrentUser(user);
        }
    }

    public static int getDiamondBalance() {
        User user = SpManager.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUserDiamond();
        }
        return 0;
    }


    public void setSVipCallCount(SVipCallCount sVipCallCount) {
        User user = getCurrentUser();
        if (user != null) {
            editor.putString(String.valueOf(user.getId()), GsonUtils.toJson(sVipCallCount));
            editor.apply();
        }
    }

    public SVipCallCount getSVipCallCount() {
        User user = getCurrentUser();
        if (user != null) {
            String sVipCallCount = mSharedPreferences.getString(String.valueOf(user.getId()),
                    "");
            return GsonUtils.fromJson(sVipCallCount, SVipCallCount.class);
        }
        return null;
    }

    /**
     * 黑名单初始化
     *
     * @return
     */
    public boolean isInitBlacklist() {
        return mSharedPreferences.getBoolean(INIT_BLACKLIST, false);
    }

    public void setInitBlacklist(boolean isInitBlacklist) {
        editor.putBoolean(INIT_BLACKLIST, isInitBlacklist);
        editor.apply();
    }

    /**
     * 在线客服初始化
     *
     * @return
     */
    public boolean isInitOnlineService() {
        return mSharedPreferences.getBoolean(INIT_ONLINE_SERVICE, false);
    }

    public void setInitOnlineService(boolean isInit) {
        editor.putBoolean(INIT_ONLINE_SERVICE, isInit);
        editor.apply();
    }

    /**
     * 设置 ShareTrace channel
     */
    public void setShareTraceChannel(String traceChannel){
        editor.putString(SHARE_TRACE, traceChannel);
        editor.apply();
    }

    public String getShareTraceChannel() {
        return mSharedPreferences.getString(SHARE_TRACE, "");
    }

    /**
     * 设置 ShareTrace 是否初始化
     * @param initShareTrace
     */
    public void setInitShareTrace(boolean initShareTrace){
        editor.putBoolean(INIT_SHARE_TRACE, initShareTrace);
        editor.apply();
    }

    public boolean isInitShareTrace() {
        return mSharedPreferences.getBoolean(INIT_SHARE_TRACE, false);
    }

    /**
     * 设置价格
     *
     * @param anchorPriceSetting
     */
    public void setPriceSetting(@NonNull AnchorPriceSetting anchorPriceSetting) {
        editor.putString(SETTING_PRICE, GsonUtils.toJson(anchorPriceSetting));
        editor.apply();
    }

    public AnchorPriceSetting getPriceSetting() {
        String priceSetting = mSharedPreferences.getString(SETTING_PRICE, "");
        if(TextUtils.isEmpty(priceSetting)){
            return new AnchorPriceSetting();
        }
        return GsonUtils.fromJson(priceSetting, AnchorPriceSetting.class);
    }

    /**
     * 清除保存的用户信息
     */
    public void clearUserInfo() {
        setCurrentUser(null);
        setCurrentUserId(0);
        setCurrentUserName(null);
        setCurrentUserPwd(null);
        setInitBlacklist(false);
        setToken("");
    }
}

