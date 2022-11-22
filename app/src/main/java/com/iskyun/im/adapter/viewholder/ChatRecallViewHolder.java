package com.iskyun.im.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hyphenate.easeui.interfaces.MessageListItemClickListener;
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder;
import com.iskyun.im.widget.chatrow.ChatRowRecall;

public class ChatRecallViewHolder extends EaseChatRowViewHolder {

    public ChatRecallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatRecallViewHolder create(ViewGroup parent, boolean isSender,
                                              MessageListItemClickListener listener) {
        return new ChatRecallViewHolder(new ChatRowRecall(parent.getContext(), isSender), listener);
    }


}