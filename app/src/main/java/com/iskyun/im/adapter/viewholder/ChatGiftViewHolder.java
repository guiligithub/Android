package com.iskyun.im.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.iskyun.im.common.Constant;
import com.iskyun.im.widget.chatrow.ChatRowGift;

import java.util.Map;

public class ChatGiftViewHolder extends EaseChatRowViewHolder {

    public ChatGiftViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatGiftViewHolder create(ViewGroup parent, boolean isSender,
                                                MessageListItemClickListener itemClickListener) {
        return new ChatGiftViewHolder(new ChatRowGift(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.getType() == EMMessage.Type.CUSTOM){
            EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
            String event = messageBody.event();
            if(event.equals(Constant.GIVE_GIFT_EVENT)){
                Map<String,String> params = messageBody.getParams();
            }
        }
    }
}

