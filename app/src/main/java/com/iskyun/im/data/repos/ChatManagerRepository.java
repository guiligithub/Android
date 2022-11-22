package com.iskyun.im.data.repos;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.iskyun.im.data.local.DbHelper;
import com.iskyun.im.data.local.dao.MsgTypeDao;
import com.iskyun.im.data.local.model.MsgType;
import com.iskyun.im.data.net.impl.ResultCallBack;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.helper.ImHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ChatManagerRepository extends BaseRepository {

    /**
     * 获取会话列表
     *
     * @return
     */
    public LiveData<Resource<List<EaseConversationInfo>>> fetchConversationsFromServer() {
        return new NetworkOnlyResource<List<EaseConversationInfo>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseConversationInfo>>> callBack) {
                EMClient.getInstance().chatManager().asyncFetchConversationsFromServer(new EMValueCallBack<Map<String, EMConversation>>() {
                    @Override
                    public void onSuccess(Map<String, EMConversation> value) {
                        List<EMConversation> conversations = new ArrayList<EMConversation>(value.values());
                        List<EaseConversationInfo> infoList = new ArrayList<>();
                        if (!conversations.isEmpty()) {
                            EaseConversationInfo info = null;
                            for (EMConversation conversation : conversations) {
                                info = new EaseConversationInfo();
                                info.setInfo(conversation);
                                info.setTimestamp(conversation.getLastMessage().getMsgTime());
                                infoList.add(info);
                            }
                        }
                        callBack.onSuccess(createLiveData(infoList));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * 获取会话列表
     *
     * @return
     */
    public LiveData<Resource<List<Object>>> loadConversationList() {
        return new NetworkOnlyResource<List<Object>>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<Object>>> callBack) {
                List<Object> emConversations = loadConversationListFromCache();
                callBack.onSuccess(new MutableLiveData<>(emConversations));
            }

        }.asLiveData();
    }

    protected List<Object> loadConversationListFromCache() {
        // get all conversations
        Map<String, EMConversation> conversations = getChatManager().getAllConversations();
        List<Pair<Long, Object>> sortList = new ArrayList<Pair<Long, Object>>();
        List<Pair<Long, Object>> topSortList = new ArrayList<Pair<Long, Object>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    String extField = conversation.getExtField();
                    if (!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                        topSortList.add(new Pair<>(Long.valueOf(extField), conversation));
                    } else {
                        sortList.add(new Pair<Long, Object>(conversation.getLastMessage().getMsgTime(), conversation));
                    }
                }
            }
        }
        List<MsgType> msgTypes = null;
        if (getMsgTypeDao() != null) {
            msgTypes = getMsgTypeDao().loadAllMsgType();
        }
        if (msgTypes != null && !msgTypes.isEmpty()) {
            synchronized (ChatManagerRepository.class) {
                for (MsgType msgType : msgTypes) {
                    String extField = msgType.getExtField();
                    if (!TextUtils.isEmpty(extField) && EaseCommonUtils.isTimestamp(extField)) {
                        topSortList.add(new Pair<>(Long.valueOf(extField), msgType));
                    }
//                    else {
//                        Object lastMsg = manage.getLastMsg();
//                        if(lastMsg instanceof InviteMessage) {
//                            long time = ((InviteMessage) lastMsg).getTime();
//                            sortList.add(new Pair<>(time, manage));
//                        }
//                    }
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            if (topSortList.size() > 0) {
                sortConversationByLastChatTime(topSortList);
            }
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sortList.addAll(0, topSortList);
        List<Object> list = new ArrayList<Object>();
        for (Pair<Long, Object> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * get MsgTypeManageDao
     *
     * @return
     */
    public MsgTypeDao getMsgTypeDao() {
        return DbHelper.getInstance().getMsgTypeDao();
    }

    /**
     * EMChatManager
     *
     * @return
     */
    public EMChatManager getChatManager() {
        return ImHelper.getInstance().getEMClient().chatManager();
    }

    private void sortConversationByLastChatTime(List<Pair<Long, Object>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, Object>>() {
            @Override
            public int compare(final Pair<Long, Object> con1, final Pair<Long, Object> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
}
