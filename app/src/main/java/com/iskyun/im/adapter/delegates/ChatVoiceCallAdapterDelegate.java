package com.iskyun.im.adapter.delegates;

import static com.hyphenate.chat.EMMessage.Type.TXT;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.utils.EaseMsgUtils;
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.iskyun.im.adapter.viewholder.ChatVoiceCallViewHolder;
import com.iskyun.im.common.Constant;
import com.iskyun.im.widget.chatrow.ChatRowVoiceCall;

import java.util.Map;

public class ChatVoiceCallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        if (item.getType() == EMMessage.Type.CUSTOM) {
            EMCustomMessageBody messageBody = (EMCustomMessageBody) item.getBody();
            String event = messageBody.event();
            Map<String, String> params = messageBody.getParams();
            boolean isChatVideo = false;
            if(params!= null){
                String callType = params.get(Constant.CALL_TYPE);
                if(!TextUtils.isEmpty(callType)
                        && String.valueOf(EaseCallType.SINGLE_VOICE_CALL.code).equals(callType)){
                    isChatVideo = true;
                }
            }
            return event.equals(Constant.CALL_END_MESSAGE_EVENT) && isChatVideo;
        }else {
            boolean isRtcCall = item.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE, "").equals(EaseMsgUtils.CALL_MSG_INFO);
            boolean isVoiceCall = item.getIntAttribute(EaseMsgUtils.CALL_TYPE, 0) == EaseCallType.SINGLE_VOICE_CALL.code;
            return item.getType() == TXT && isRtcCall && isVoiceCall;
        }
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowVoiceCall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatVoiceCallViewHolder(view, itemClickListener);
    }
}
