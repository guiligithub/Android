package com.iskyun.im.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.iskyun.im.R;
import com.iskyun.im.data.bean.CallLog;
import com.iskyun.im.data.bean.Sex;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.databinding.ViewItemCallLogBinding;
import com.iskyun.im.utils.DateUtils;
import com.iskyun.im.utils.UserUtils;
import com.iskyun.im.utils.manager.SpManager;

public class CallLogAdapter extends BaseQuickAdapter<CallLog, BaseDataBindingHolder<ViewItemCallLogBinding>> implements LoadMoreModule {
    public CallLogAdapter(int layoutResId) {
        super(layoutResId);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull BaseDataBindingHolder<ViewItemCallLogBinding> holder, CallLog callLog) {
        ViewItemCallLogBinding tabBinding = holder.getDataBinding();
        if (tabBinding == null)
            return;
        User user = SpManager.getInstance().getCurrentUser();
        UserUtils.setHeaderUrl(tabBinding.tabIvAvatar,callLog.getSex(),callLog.getHeadUrl());
        UserUtils.setUserSexAndAge(tabBinding.tabTvAge,callLog.getSex(),callLog.getAge());

        if (user.getSex() == Sex.WOMAN.ordinal()) {
            tabBinding.tabTvYuanbao.setText("+" + callLog.getEarnings() + "元宝");
        } else {
            tabBinding.tabTvYuanbao.setText("-" + callLog.getVirtualCurrency() + "钻石");
        }

        if (callLog.getCallDurationType() == 2) {
            tabBinding.tabIvCallType.setImageResource(R.mipmap.ease_chat_video_call_receive);
        } else {
            tabBinding.tabIvCallType.setImageResource(R.mipmap.ease_chat_voice_call_receive);
        }

        if(callLog.getCallState() == 1){
            tabBinding.tabIvCall.setImageResource(R.mipmap.icon_call_in);
        } else {
            tabBinding.tabIvCall.setImageResource(R.mipmap.icon_call_out);
        }

        tabBinding.tabTvName.setText(callLog.getNickname());
        tabBinding.tabIvTime.setText(DateUtils.getDateNoYear(callLog.getCreateTime()));
        tabBinding.tabTvAge.setText(String.valueOf(callLog.getAge()));
        tabBinding.tabTvContent.setText("通话时长：" + callLog.getCallDuration());

    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
