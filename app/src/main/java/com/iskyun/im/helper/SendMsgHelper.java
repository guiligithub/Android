package com.iskyun.im.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.hyphenate.easecallkit.base.EaseCallType;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.SVipCallCount;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.bean.VipType;
import com.iskyun.im.ui.auth.AuthActivity;
import com.iskyun.im.ui.common.RechargeActivity;
import com.iskyun.im.ui.common.VipRechargeActivity;
import com.iskyun.im.ui.frag.ChatOtherTipDialogFragment;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.DisplayUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;

import java.util.Calendar;

public class SendMsgHelper {

    private static SendMsgHelper INSTANCE;

    private OnRechargeCallback callback;

    private SendMsgHelper() {
    }

    public static SendMsgHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (SendMsgHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SendMsgHelper();
                }
            }
        }
        return INSTANCE;
    }

    public boolean isSuperVip() {
        boolean isSuper = false;
        User user = SpManager.getInstance().getCurrentUser();
        if (user.getVipType() == VipType.VIP_OF_S.getType()) {
            isSuper = true;
        }
        return isSuper;
    }

    /**
     * 判断用户VIP过期
     *
     * @return
     */
    public boolean vipIsExpire() {
        boolean isExpire = false;
        User user = SpManager.getInstance().getCurrentUser();
        if (user.isVip() == 1 ) {
            isExpire = DateUtils.currentDateCompareTo(user.getVipExpireTime());
        }
        return isExpire;
    }

    /**
     * 判断用户 super VIP过期
     *
     * @return
     */
    public boolean superVipIsExpire() {
        boolean isExpire = false;
        User user = SpManager.getInstance().getCurrentUser();
        if (isSuperVip()) {
            isExpire = DateUtils.currentDateCompareTo(user.getVipExpireTime());
        }
        return isExpire;
    }



    /**
     * 文本 语音 图片 消息
     *
     * @return
     */
    public boolean isExpireSendMsg(@NonNull Anchor anchor) {
        User user = SpManager.getInstance().getCurrentUser();
        if (user.getSex() == Sex.MAN.ordinal()) {
            //vip 免费聊天
            if (!vipIsExpire()) {
                return isExpireOfText(user.getUserDiamond(), anchor.getTextPrice());
            }
        } else {
            if (user.isAnchor() == 0) {
                if (callback != null) {
                    callback.anchorAuth();
                }
                anchorAuth();
                return false;
            }
        }

        return true;
    }

    private boolean isExpireOfText(int userDiamond, int anchorTextPrice) {
        if (anchorTextPrice > userDiamond) {
            //弹窗
            if (callback != null)
                callback.rechargeDiamond();
            rechargeDiamond();
            return false;
        }
        return true;
    }

    /**
     * 判断是否是有效呼叫
     *
     * @param anchor
     * @param callType
     * @return
     */
    public boolean isExpireCall(@NonNull Anchor anchor, EaseCallType callType) {
        User user = SpManager.getInstance().getCurrentUser();
        if (user.getSex() == Sex.MAN.ordinal()) {
            int callPrice;
            if (callType == EaseCallType.SINGLE_VOICE_CALL) {
                callPrice = anchor.getVoiceMinute();
            } else {
                callPrice = anchor.getVideoMinute();
            }
            if (vipIsExpire()) {
                if (isSuperVip()) {
                    return true;
                }
                if (callPrice >= user.getUserDiamond()) {
                    //弹窗
                    if (callback != null)
                        callback.rechargeDiamond();
                    rechargeDiamond();
                    return false;
                }
            } else {
                if (callback != null)
                    callback.rechargeVip();
                rechargeVip();
                return false;
            }
        } else {
            if (user.isAnchor() == 0) {
                if (callback != null) {
                    callback.anchorAuth();
                }
                anchorAuth();
                return false;
            }
        }
        return true;
    }

    public void rechargeDiamond() {
        LogUtils.e("rechargeDiamond");
        String content = DisplayUtils.getString(R.string.balance_is_insufficient);
        ChatOtherTipDialogFragment.Builder builder = new ChatOtherTipDialogFragment.Builder((FragmentActivity) ImLiveApp.get().getTopActivity());
        builder.setTitle(content)
                .setOnConfirmClickListener(DisplayUtils.getString(R.string.go_recharge), view -> {
                    ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), RechargeActivity.class);
                })
                .setSubContent(DisplayUtils.getString(R.string.privileges))
                .showCancelButton(true);
        builder.show();
    }

    public void rechargeVip() {
        LogUtils.e("rechargeVip");
        //VipTipDialogFragment.show();
        String content = DisplayUtils.getString(R.string.not_vip_tips);
        ChatOtherTipDialogFragment.Builder builder = new ChatOtherTipDialogFragment.Builder((FragmentActivity) ImLiveApp.get().getTopActivity());
        builder.setTitle(content)
                .setOnConfirmClickListener(DisplayUtils.getString(R.string.become_vip), view -> {
                    ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), VipRechargeActivity.class);
                })
                .setSubContent(DisplayUtils.getString(R.string.privileges))
                .showCancelButton(true);
        builder.show();
    }

    private void anchorAuth() {
        LogUtils.e("anchorAuth");
        String content = DisplayUtils.getString(R.string.not_anchor_tips);
        ChatOtherTipDialogFragment.Builder builder = new ChatOtherTipDialogFragment.Builder((FragmentActivity) ImLiveApp.get().getTopActivity());
        builder.setTitle(content)
                .setOnConfirmClickListener(DisplayUtils.getString(R.string.go_auth), view -> {
                    ActivityUtils.launcher(ImLiveApp.get().getTopActivity(), AuthActivity.class);
                })
                .setSubContent("")
                .showCancelButton(true);
        builder.show();
    }

    public boolean isSVipFreeCount() {
        SVipCallCount sVipCallCount = SpManager.getInstance().getSVipCallCount();
        if (sVipCallCount != null) {
            return sVipCallCount.getCallCount() < 3;
        }
        return false;
    }

    public void setSVipFreeCount() {
        SVipCallCount sVipCallCount = SpManager.getInstance().getSVipCallCount();
        if (sVipCallCount != null) {
            int callCount = sVipCallCount.getCallCount();
            sVipCallCount.setCallCount(callCount + 1);
            sVipCallCount.setCallTime(Calendar.getInstance().getTime().toString());
        }
    }


    public interface OnRechargeCallback {
        void rechargeDiamond();

        void rechargeVip();

        void anchorAuth();
    }

}
