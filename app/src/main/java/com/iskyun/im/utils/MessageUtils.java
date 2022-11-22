package com.iskyun.im.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallEndReason;
import com.iskyun.im.R;
import com.iskyun.im.common.Constant;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class MessageUtils {

    /**
     * 语音 视频 消息处理
     * @param context
     * @param message
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHandleCallMessage(Context context, EMMessage message){
        String reasonContent = null;
        EMCustomMessageBody customBody = (EMCustomMessageBody) message.getBody();
        String reason = customBody.getParams().get(Constant.REASON);
        if (!TextUtils.isEmpty(reason)) {
            try {
                String callTime = customBody.getParams().get(Constant.CALL_TIME);
                long duration = 0;
                if (!TextUtils.isEmpty(callTime)) {
                    duration = Long.parseLong(callTime);
                }
                int reasonCode = Integer.parseInt(reason);
                if (reasonCode == EaseCallEndReason.EaseCallEndReasonCancel.code
                        || reasonCode == EaseCallEndReason.EaseCallEndReasonRemoteCancel.code) {
                    reasonContent = context.getString(R.string.cancel_call);
                } else if (reasonCode == EaseCallEndReason.EaseCallEndReasonRefuse.code) {
                    reasonContent = context.getString(R.string.refuse_answer);
                } else if (reasonCode == EaseCallEndReason.EaseCallEndReasonBusy.code) {
                    reasonContent = context.getString(R.string.busy_line);
                } else if (reasonCode == EaseCallEndReason.EaseCallEndReasonNoResponse.code) {
                    reasonContent = context.getString(R.string.no_response);
                } else if (reasonCode == EaseCallEndReason.EaseCallEndReasonRemoteNoResponse.code) {
                    reasonContent = context.getString(R.string.other_no_response);
                } else if (reasonCode == EaseCallEndReason.EaseCallEndReasonHandleOnOtherDevice.code) {
                    reasonContent = context.getString(R.string.other_equipment);
                } else {
                    reasonContent = context.getString(R.string.call_duration)+"\u0020";
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatter= null;
                    if(duration > 60*60){
                        formatter = new SimpleDateFormat("hh时:mm分");
                    } else {
                        formatter = new SimpleDateFormat("mm:ss");
                    }
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    reasonContent += formatter.format(duration*1000);
                }
            } catch (NumberFormatException e) {
                LogUtils.e(e.getMessage());
            }
        }
        return reasonContent;
    }

}
