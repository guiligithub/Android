package com.iskyun.im.widget.chatrow;

import android.content.Context;
import android.widget.TextView;

import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.iskyun.im.R;
import com.iskyun.im.utils.MessageUtils;

public class ChatRowVoiceCall extends EaseChatRow {
    private TextView contentView;

    public ChatRowVoiceCall(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_voice_call : R.layout.ease_row_received_voice_call, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = findViewById(R.id.tv_chatcontent);
    }

    @Override
    protected void onSetUpView() {
        if (message.getBody() instanceof EMCustomMessageBody) {
            contentView.setText(MessageUtils.getHandleCallMessage(context, message));
        } else {
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            contentView.setText(txtBody.getMessage());
        }
    }
}
