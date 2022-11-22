package com.iskyun.im.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.easeui.constants.EaseConstant;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.databinding.ActivityOnlineBinding;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.ui.message.frag.OnlineFragment;
import com.iskyun.im.ui.mine.viewmodel.MineModel;

public class OnlineActivity extends BaseActivity<ActivityOnlineBinding, MineModel> {

    private OnlineFragment onlineFragment;
    private String conversationId;

    public static void actionStart(Context context, String conversationId) {
        Intent intent = new Intent(context, OnlineActivity.class);
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        context.startActivity(intent);
    }

    @Override
    public MineModel onCreateViewModel(ViewModelProvider provider) {
        return null;
    }

    @Override
    public ActivityOnlineBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityOnlineBinding.inflate(inflater);
    }

    @Override
    public void initBase() {

        conversationId = getIntent().getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);

        onlineFragment = new OnlineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, ImHelper.getInstance().getModel().isMsgRoaming());
        onlineFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.online_content, onlineFragment, onlineFragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (onlineFragment != null) {
            onlineFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public int getTitleText() {
        return R.string.online;
    }
}
