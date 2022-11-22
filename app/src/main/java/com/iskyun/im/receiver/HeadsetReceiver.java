package com.iskyun.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iskyun.im.R;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.ToastUtils;

public class HeadsetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 耳机插入状态 0 拔出，1 插入
        boolean state = intent.getIntExtra("state", 0) != 0;
        // 耳机类型
        String name = intent.getStringExtra("name");
        // 耳机是否带有麦克风 0 没有，1 有
        boolean mic = intent.getIntExtra("microphone", 0) != 0;
        String headsetChange = String.format(context.getString(R.string.headset_insert), state, mic);
        LogUtils.d(headsetChange);
        ToastUtils.showToast(headsetChange);
    }
}
