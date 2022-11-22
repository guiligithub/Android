package com.iskyun.im.ui.message.frag;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.iskyun.im.R;
import com.iskyun.im.base.BaseActivity;
import com.iskyun.im.common.Constant;
import com.iskyun.im.data.net.impl.OnResourceParseCallback;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.helper.LiveDataBus;
import com.iskyun.im.ui.frag.CommonDialogFragment;
import com.iskyun.im.ui.frag.SimpleDialogFragment;
import com.iskyun.im.ui.message.CallLogActivity;
import com.iskyun.im.ui.message.ChatActivity;
import com.iskyun.im.ui.message.OnlineListActivity;
import com.iskyun.im.ui.message.SystemMessageActivity;
import com.iskyun.im.ui.message.viewmodel.ConversationViewModel;
import com.iskyun.im.ui.message.viewmodel.MessageViewModel;
import com.iskyun.im.utils.ActivityUtils;
import com.iskyun.im.utils.ToastUtils;

import java.util.List;

public class MsgTabFragment extends EaseConversationListFragment {

    private MessageViewModel messageViewModel;
    private ConversationViewModel conversationViewModel;
    private int attentionPosition;

    public MsgTabFragment() {
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initViewModel();
        conversationListLayout.showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition.RIGHT);
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_message_header, null, false);
        conversationListLayout.addHeaderView(headerView);
        conversationListLayout.getListAdapter().hideEmptyView(true);
        srlRefresh.setEnabled(false);

        headerView.findViewById(R.id.msg_header_iv_call).setOnClickListener(v -> ActivityUtils.launcher(getActivity(), CallLogActivity.class));
        headerView.findViewById(R.id.msg_header_iv_sys_msg).setOnClickListener(v -> ActivityUtils.launcher(getActivity(), SystemMessageActivity.class));
        headerView.findViewById(R.id.msg_header_iv_service).setOnClickListener(v -> ActivityUtils.launcher(getActivity(), OnlineListActivity.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseConversationInfo info = conversationListLayout.getItem(position);
        Object object = info.getInfo();

        if (object instanceof EMConversation) {
            switch (item.getItemId()) {
                case R.id.action_con_make_top:
                    conversationListLayout.makeConversationTop(position, info);
                    return true;
                case R.id.action_con_cancel_top:
                    conversationListLayout.cancelConversationTop(position, info);
                    return true;
                case R.id.action_con_delete:
                    showDeleteDialog(position, info);
                    return true;
            }
        }
        return super.onMenuItemClick(item, position);
    }

    private void showDeleteDialog(int position, EaseConversationInfo info) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.delete_conversation)
                .setOnConfirmClickListener(R.string.delete, new CommonDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        conversationListLayout.deleteConversation(position, info);
                        LiveDataBus.get().with(Constant.CONVERSATION_DELETE).postValue(new EaseEvent(Constant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE));
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void initData() {
        //需要两个条件，判断是否触发从服务器拉取会话列表的时机，一是第一次安装，二则本地数据库没有会话列表数据
        if (ImHelper.getInstance().isFirstInstall() &&
                EMClient.getInstance().chatManager().getAllConversations().isEmpty()) {
            conversationViewModel.fetchConversationsFromServer();
        } else {
            super.initData();
        }

    }

    private void initViewModel() {
        conversationViewModel = new ViewModelProvider(this).get(ConversationViewModel.class);
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        conversationViewModel.getDeleteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    //mViewModel.loadConversationList();
                    conversationListLayout.loadDefaultData();
                }
            });
        });

        conversationViewModel.getReadObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    conversationListLayout.loadDefaultData();
                }
            });
        });

        conversationViewModel.getConversationInfoObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseConversationInfo>>(true) {
                @Override
                public void onSuccess(@Nullable List<EaseConversationInfo> data) {
                    conversationListLayout.setData(data);
                }
            });
        });

        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(Constant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        //messageChange.with(Constant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        //messageChange.with(Constant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(Constant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(Constant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        //messageChange.with(Constant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        //messageChange.with(Constant.CONTACT_ADD, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(Constant.CONTACT_UPDATE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(Constant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);
        messageChange.with(Constant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);

    }

    private void refreshList(Boolean event) {
        if (event == null) {
            return;
        }
        if (event) {
            conversationListLayout.loadDefaultData();
        }
    }

    private void loadList(EaseEvent change) {
        if (change == null) {
            return;
        }
        if (change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave() || change.isChatRoomLeave()
                || change.isContactChange()
                || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange()) {
            conversationListLayout.loadDefaultData();
        }
    }

    /**
     * 解析Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (mContext instanceof BaseActivity) {
            // ((BaseActivity) mContext).parseResource(response, callback);
        }
    }

    /**
     * toast by string
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        Object item = conversationListLayout.getItem(position).getInfo();
   /*     if(item instanceof EMConversation) {
            if(EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {
                SystemMsgsActivity.actionStart(mContext);
            }else {
            }
        }*/
        String id = ((EMConversation) item).conversationId();
        ChatActivity.actionStart(mContext, id, EaseCommonUtils.getChatType((EMConversation) item));
    }

    @Override
    public void notifyItemChange(int position) {
        super.notifyItemChange(position);
        LiveDataBus.get().with(Constant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(Constant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
    }

    @Override
    public void notifyAllChange() {
        super.notifyAllChange();
    }

}
