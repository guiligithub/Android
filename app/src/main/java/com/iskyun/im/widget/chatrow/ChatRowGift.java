package com.iskyun.im.widget.chatrow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.iskyun.im.R;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Gift;
import com.iskyun.im.utils.GsonUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.glide.ImageLoader;

import java.util.Map;

@SuppressLint("ViewConstructor")
public class ChatRowGift extends EaseChatRow {
    private TextView nickNameView;
    private TextView tvTips;
    private ImageView headImageView;

    public ChatRowGift(Context context, boolean isSender) {
        super(context, isSender);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(showSenderType ? R.layout.ease_row_sent_gift
                : R.layout.ease_row_received_gift, this);
    }

    @Override
    protected void onFindViewById() {
        nickNameView = findViewById(R.id.user_nick_name);
        tvTips = findViewById(R.id.user_card);
        /* userIdView = (TextView) findViewById(R.id.user_id); */
        headImageView = findViewById(R.id.head_Image_view);
    }

    @Override
    protected void onSetUpView() {
        EMCustomMessageBody messageBody = (EMCustomMessageBody) message.getBody();
        Map<String, String> params = messageBody.getParams();
        try{
            Gift gift = GsonUtils.fromJson(params.get(Constant.GIVE_GIFT), Gift.class);
            String name = getContext().getString(R.string.gift) + "[" + gift.getGiftName() + "]"
                    + " x " + gift.getGiftNum();
            tvTips.setText(name);
            ImageLoader.get().load(headImageView, gift.getGiftPic());
        }catch (JsonParseException e){
            LogUtils.e(e.getMessage());
        }
    }
}
