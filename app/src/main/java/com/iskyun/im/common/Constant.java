package com.iskyun.im.common;

import com.hyphenate.easeui.constants.EaseConstant;
import com.iskyun.im.BuildConfig;

public interface Constant {

    String AUTHORITY = BuildConfig.APPLICATION_ID + ".FileProvider";
    int VIDEO_DURATION = 15;
    int VIDEO_MAX_DURATION = (VIDEO_DURATION + 1) * 1000;
    String HX_ID = "hxid-";
    String WX_APP_ID = BuildConfig.WX_APP_ID;

    String API_URL = BuildConfig.API_URL;
    String PRIVACY_AGREEMENT = "https://www.ihuachao.com/agreement/privacy_agreement.html";//隐私
    String USER_AGREEMENT = "https://www.ihuachao.com/agreement/user_agreement.html";//用户协议
    String EMPLOYEE_AGREEMENT = "https://www.ihuachao.com/agreement/employee_agreement.html";//用户协议

    String DEFAULT_CHANNEL = "huachao";

    String ANCHOR = "ANCHOR";//主播
    String ANCHOR_ID = "ANCHOR_ID";//主播ID
    String ANCHOR_SEX = "ANCHOR_SEX";//主播性别
    String ANCHOR_NAME = "ANCHOR_NAME";//主播昵称
    String ANCHOR_HEADURL = "ANCHOR_HEADURL";//主播头像
    String DIAMOND_CHANGE = "DIAMOND_CHANGE";//钻石变化
    String VIP_CHANGE = "VIP_CHANGE";//vip变化
    String UPDATE_USER_SUCCESS = "UPDATE_USER_SUCCESS";//用户资料更新成功

    String RELATION_VISITOR = "RELATION_VISITOR";//关注，粉丝
    String RELATION_FANS = "RELATION_FANS";//关注，粉丝
    String RELATION_FOLLOW = "RELATION_FOLLOW";//关注，粉丝
    String CHANGE_MINE = "CHANGE_MINE";//切换到我的


    String PAY_SUCCESS = "PAY_SUCCESS";//支付成功

    //    public static final int HOME_TAB_SPAN_COUNT = R.integer.;
    String CONVERSATION_DELETE = "conversation_delete";

    String NOTIFY_CHANGE = "notify_change";

    String MESSAGE_CHANGE_CHANGE = "message_change";

    String CONVERSATION_READ = "conversation_read";

    String CONTACT_CHANGE = "contact_change";
    String CONTACT_ADD = "contact_add";
    String CONTACT_UPDATE = "contact_update";

    String MESSAGE_CALL_SAVE = "message_call_save";
    String MESSAGE_NOT_SEND = "message_not_send";

    String MESSAGE_CHANGE_RECEIVE = "message_receive";
    String MESSAGE_CHANGE_CMD_RECEIVE = "message_cmd_receive";
    String MESSAGE_TYPE_RECALL = "message_recall";
    String MESSAGE_TYPE_RECALLER = "message_recaller";
    String MESSAGE_CHANGE_RECALL = "message_recall";
    String MESSAGE_FORWARD = "message_forward";
    String ACCOUNT_REMOVED = "account_removed";
    String ACCOUNT_FORBIDDEN = "user_forbidden";
    String ACCOUNT_CONFLICT = "conflict";
    String ACCOUNT_CHANGE = "account_change";
    String ACCOUNT_KICKED_BY_CHANGE_PASSWORD = "kicked_by_change_password";
    String ACCOUNT_KICKED_BY_OTHER_DEVICE = "kicked_by_another_device";

    String HISTORY_MSG_ID = "history_msg_id";


    String SMS_LOGIN_SUCCESS = "SMS_LOGIN_SUCCESS";
    String HX_LOGIN_FAIL = "HX_LOGIN_FAIL";//环信登录失败


    String GIVE_GIFT_EVENT = "GIVE_GIFT_EVENT";//赠送礼物event
    String RECEIVED_GIFT = "RECEIVED_GIFT";//接收礼物
    String GIVE_GIFT = "GIVE_GIFT";//赠送礼物
    String CALL_END_MESSAGE_EVENT = EaseConstant.CALL_END_MESSAGE_EVENT;//通话结束消息
    String REASON = EaseConstant.REASON;//通话结束原因
    String CALL_TIME = EaseConstant.CALL_TIME;//通话结束 时间
    String CALL_TYPE = EaseConstant.CALL_TYPE;//通话类型
    String RECORD_URL = "RECORD_URL";//录制的url
    String CHAT_ANCHOR = "CHAT_ANCHOR";

    String END_CALL_EVENT = "END_CALL";//通话结束
    String END_MESSAGE_EVENT = "END_MESSAGE_EVENT";//聊天发送消息

    String COMPLAINT_SUCCESS="COMPLAINT_SUCCESS";//举报成功

    String ANCHOR_EVALUATE="ANCHOR_EVALUATE";//主播评分

}
