package com.iskyun.im.ui.message.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.iskyun.im.data.repos.ChatManagerRepository;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.helper.SingleLiveData;

import java.util.List;

public class ConversationViewModel extends ViewModel {

    private SingleLiveData<Resource<List<Object>>> conversationObservable;
    private SingleLiveData<Resource<List<EaseConversationInfo>>> conversationInfoObservable;
    private SingleLiveData<Resource<Boolean>> deleteConversationObservable;
    private SingleLiveData<Resource<Boolean>> readConversationObservable;
    private ChatManagerRepository mRepository;


    public ConversationViewModel() {
        conversationObservable = new SingleLiveData<>();
        conversationInfoObservable = new SingleLiveData<>();
        deleteConversationObservable = new SingleLiveData<>();
        readConversationObservable = new SingleLiveData<>();
        mRepository = new ChatManagerRepository();
    }



    /**
     * 获取聊天列表
     */
    public void loadConversationList() {
        conversationObservable.setSource(mRepository.loadConversationList());
    }

    public LiveData<Resource<List<Object>>> getConversationObservable() {
        return conversationObservable;
    }

    /**
     * 从服务器获取聊天列表
     */
    public void fetchConversationsFromServer() {
        conversationInfoObservable.setSource(mRepository.fetchConversationsFromServer());
    }

    public LiveData<Resource<List<EaseConversationInfo>>> getConversationInfoObservable() {
        return conversationInfoObservable;
    }

    /**
     * 删除对话
     * @param conversationId
     */
    public void deleteConversationById(String conversationId) {
//        deleteConversationObservable.setSource(mRepository.deleteConversationById(conversationId));
    }

    public LiveData<Resource<Boolean>> getDeleteObservable() {
        return deleteConversationObservable;
    }

    /**
     * 将会话置为已读
     * @param conversationId
     */
    public void makeConversationRead(String conversationId) {
//        readConversationObservable.setSource(mRepository.makeConversationRead(conversationId));
    }

    public LiveData<Resource<Boolean>> getReadObservable() {
        return readConversationObservable;
    }

    /**
     * 删除系统消息
     * @param msg
     */
//    public void deleteSystemMsg(MsgType msg) {
//        try {
//            DbHelper dbHelper = DbHelper.getInstance(ImLiveApp.get());
////            if(dbHelper.getInviteMessageDao() != null) {
////                dbHelper.getInviteMessageDao().delete("type", msg.getType());
////            }
//            if(dbHelper.getMsgTypeDao() != null) {
//                dbHelper.getMsgTypeDao().delete(msg);
//            }
//            deleteConversationObservable.postValue(Resource.success(true));
//        } catch (Exception e) {
//            e.printStackTrace();
//            deleteConversationObservable.postValue(Resource.error(ErrorCode.EM_DELETE_SYS_MSG_ERROR, e.getMessage(), null));
//        }
//    }

}