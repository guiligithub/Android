package com.iskyun.im.ui.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseIM;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.provider.EaseUserProfileProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.bean.Anchor;
import com.iskyun.im.data.bean.RelationType;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.req.RelationBody;
import com.iskyun.im.databinding.ActivityChatBinding;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.MainActivity;
import com.iskyun.im.ui.frag.MoreActionFragment;
import com.iskyun.im.ui.message.frag.ChatFragment;
import com.iskyun.im.ui.message.frag.ChatTopFragment;
import com.iskyun.im.ui.message.viewmodel.ChatViewModel;
import com.iskyun.im.ui.message.viewmodel.MessageViewModel;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.manager.SpManager;
import com.iskyun.im.viewmodel.AnchorViewModel;
import com.iskyun.im.viewmodel.RelationViewModel;

public class ChatActivity extends BaseActivity<ActivityChatBinding, ChatViewModel> {

    private static final String FROM_MAIN = "MAIN";
    private String conversationId;
    private String userId;
    private int chatType;
    private ChatFragment fragment;
    private String historyMsgId;
    private Anchor mAnchor;
    private RelationViewModel relationViewModel;
    private AnchorViewModel anchorViewModel;
    private RelationType relationType;
    private boolean fromNotifier = false;//是否从通知进来


    public static void actionStart(Context context, String conversationId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        if (!conversationId.contains(Constant.HX_ID)) {
            conversationId = Constant.HX_ID + conversationId;
        }
        intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        intent.putExtra(FROM_MAIN, false);
        context.startActivity(intent);
    }

    @Override
    public ChatViewModel onCreateViewModel(ViewModelProvider provider) {
        return provider.get(ChatViewModel.class);
    }

    @Override
    public ActivityChatBinding onCreateViewBinding(LayoutInflater inflater) {
        return ActivityChatBinding.inflate(inflater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void initBase() {
        initIntent(getIntent());
        initChatFragment();
        initData();
        setActionImage(R.mipmap.icon_chat_more);
        getAnchorInfo();
        observerRelation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            initIntent(intent);
            initChatFragment();
            initData();
            getAnchorInfo();
        }
    }

    @Override
    public int getTitleText() {
        return 0;
    }

    @Override
    protected void onClick(View view) {
        super.onClick(view);
        if (mAnchor != null) {
            MoreActionFragment dialogChatMore = MoreActionFragment
                    .newInstance(MoreActionFragment.FROM_CHAT, mAnchor);
            dialogChatMore.setOnItemClickListener(this::onItemClick);
            dialogChatMore.showNow(getSupportFragmentManager(), "more");
        }
    }

    private void initIntent(Intent intent) {
        conversationId = intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID);
        if (conversationId.contains(Constant.HX_ID)) {
            userId = conversationId.replace(Constant.HX_ID, "");
        }
        chatType = intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        historyMsgId = intent.getStringExtra(Constant.HISTORY_MSG_ID);
        fromNotifier = intent.getBooleanExtra(FROM_MAIN, true);
    }

    private void initChatFragment() {
        fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        bundle.putString(Constant.HISTORY_MSG_ID, historyMsgId);
        bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, ImHelper.getInstance().getModel().isMsgRoaming());
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.chat_fl_content, fragment, "chat_content");

        ChatTopFragment chatTopFragment = new ChatTopFragment();
        ft.replace(R.id.chat_fl_header, chatTopFragment, "chat_top");
        ft.commit();
    }

    private void initData() {
        anchorViewModel = onCreateViewModel(AnchorViewModel.class);
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        mViewModel.getDeleteObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    finish();
                    EaseEvent event = EaseEvent.create(Constant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE);
                    messageViewModel.setMessageChange(event);
                }
            });
        });
        messageViewModel.getMessageChange().with(Constant.MESSAGE_FORWARD, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (event.isMessageChange()) {
                showToast(event.event);
            }
        });

        setDefaultTitle();
    }

    /**
     * 获取对方信息
     */
    public void getAnchorInfo() {
        anchorViewModel.setShowDialog(false);
        try {
            anchorViewModel.findAnchorDetailById(Integer.parseInt(userId))
                    .observe(this, anchor -> {
                        mAnchor = anchor;
                        mTvTitle.setText(mAnchor.getNickname());
                        LiveDataBus.get().with(Constant.CHAT_ANCHOR).postValue(anchor);
                    });
        } catch (NumberFormatException e) {
            LogUtils.e(e.getMessage());
        }
    }

    /**
     * @param moreType
     */
    private void onItemClick(MoreActionFragment.MoreType moreType) {
        switch (moreType) {
            case CLEAN_CHAT_RECORD:
                clearChatRecord();
                break;
            case ATTENTION:
            case UN_ATTENTION:
                attention(RelationType.ATTENTION);
                break;
            case BLOCKLIST:
            case UN_BLOCKLIST:
                attention(RelationType.BLOCK);
                break;
        }
    }

    @Override
    public void finish() {
        if(fromNotifier && !ActivityUtils.taskHasMainActivity()){
            ActivityUtils.launcher(this, MainActivity.class);
        }
        super.finish();
    }

    /**
     * 关注  拉黑
     *
     * @param relationType
     */
    private void attention(RelationType relationType) {
        if (this.mAnchor == null)
            return;
        User user = SpManager.getInstance().getCurrentUser();
        this.relationType = relationType;
        RelationBody body = new RelationBody(relationType.getRelationType(),
                user.getId(), this.mAnchor.getId());
        switch (relationType) {
            case BLOCK:
                try {
                    if (this.mAnchor.isBlack() == 1) {
                        relationViewModel.delAttention(body);
                        EMClient.getInstance().contactManager()
                                .removeUserFromBlackList(Constant.HX_ID + this.mAnchor.getId());
                    } else {
                        EMClient.getInstance().contactManager().addUserToBlackList(
                                Constant.HX_ID + this.mAnchor.getId(), true);
                        relationViewModel.addUserRelation(body);
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                break;
            case ATTENTION:
                if (this.mAnchor.isFocus() == 1) {
                    relationViewModel.delAttention(body);
                } else {
                    relationViewModel.addUserRelation(body);
                }
                break;
        }
    }

    /**
     * 清除聊天记录
     */
    private void clearChatRecord() {
        EMClient.getInstance().chatManager().deleteConversation(conversationId, true);
        LiveDataBus.get().with(Constant.CONVERSATION_DELETE)
                .postValue(new EaseEvent(Constant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE));
    }

    private void observerRelation() {
        relationViewModel = onCreateViewModel(RelationViewModel.class);
        relationViewModel.observerRelation().observe(this, s -> {
            switch (relationType) {
                case BLOCK:
                    if (mAnchor != null) {
                        if (mAnchor.isBlack() == 1) {
                            mAnchor.setBlack(0);
                        } else {
                            mAnchor.setBlack(1);
                        }
                    }
                    break;
                case ATTENTION:
                    if (mAnchor != null) {
                        if (mAnchor.isFocus() == 1) {
                            mAnchor.setFocus(0);
                        } else {
                            mAnchor.setFocus(1);
                        }
                    }
                    break;
            }
        });
    }

    /**
     * title 设置
     */
    private void setDefaultTitle() {
        String title;
        EaseUserProfileProvider userProvider = EaseIM.getInstance().getUserProvider();
        if (userProvider != null) {
            EaseUser user = userProvider.getUser(conversationId);
            if (user != null) {
                title = user.getNickname();
            } else {
                title = conversationId;
            }
        } else {
            title = conversationId;
        }
        mTvTitle.setText(title);
    }
}
