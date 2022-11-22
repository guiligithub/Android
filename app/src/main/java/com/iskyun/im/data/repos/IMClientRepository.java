package com.iskyun.im.data.repos;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.iskyun.im.data.bean.User;
import com.iskyun.im.data.local.DbHelper;
import com.iskyun.im.data.net.impl.AppEmCallBack;
import com.iskyun.im.data.net.impl.ResultCallBack;
import com.iskyun.im.data.resp.Resource;
import com.iskyun.im.helper.ImHelper;
import com.iskyun.im.utils.manager.SpManager;

public class IMClientRepository extends BaseRepository{

    /**
     * 登录到服务器，可选择密码登录或者token登录
     * 登录之前先初始化数据库，如果登录失败，再关闭数据库;如果登录成功，则再次检查是否初始化数据库
     * @param userName
     * @param pwd
     * @param isTokenFlag
     * @return
     */
    public LiveData<Resource<EaseUser>> loginToServer(String userName, String pwd, boolean isTokenFlag) {
        return new NetworkOnlyResource<EaseUser>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
                //ImHelper.getInstance().init(ImLiveApp.get());
                ImHelper.getInstance().getModel().setCurrentUserName(userName);
                if(isTokenFlag) {
                    EMClient.getInstance().loginWithToken(userName, pwd, new AppEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                            //closeDb();
                        }
                    });
                }else {
                    EMClient.getInstance().login(userName, pwd, new AppEmCallBack() {
                        @Override
                        public void onSuccess() {
                            successForCallBack(callBack);
                        }

                        @Override
                        public void onError(int code, String error) {
                            callBack.onError(code, error);
                            //closeDb();
                        }
                    });
                }

            }

        }.asLiveData();
    }

    private void successForCallBack(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
        // ** manually load all local groups and conversation
        loadAllConversationsAndGroups();
        //从服务器拉取加入的群，防止进入会话页面只显示id
        //getAllJoinGroup();
        // get contacts from server
        //getContactsFromServer();
        // get current user id
        String currentUser = EMClient.getInstance().getCurrentUser();
        User myUser= SpManager.getInstance().getCurrentUser();
        EaseUser user = new EaseUser();
        if(myUser != null){
            user.setNickname(myUser.getNickname());
            user.setAvatar(myUser.getHeadUrl());
            user.setGender(myUser.getSex());
        }
        user.setUsername(currentUser);
        SpManager.getInstance().setCurrentEaseUser(user);
        callBack.onSuccess(new MutableLiveData<>(user));
    }

    public LiveData<Resource<Boolean>> loadAllInfoFromHX() {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(ResultCallBack<LiveData<Boolean>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(() -> {
                    loadAllConversationsAndGroups();
                    callBack.onSuccess(createLiveData(true));
                });

            }
        }.asLiveData();
    }

    /**
     * 退出登录
     * @param unbindDeviceToken
     * @return
     */
    public LiveData<Resource<Boolean>> logout(boolean unbindDeviceToken) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        ImHelper.getInstance().logoutSuccess();
                        //reset();
                        if (callBack != null) {
                            callBack.onSuccess(createLiveData(true));
                        }

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String error) {
                        //reset();
                        if (callBack != null) {
                            callBack.onError(code, error);
                        }
                    }
                });
            }
        }.asLiveData();
    }

    /**
     * 从本地数据库加载所有的对话及群组
     */
    private void loadAllConversationsAndGroups() {
        // 初始化数据库
        initDb();
        // 从本地数据库加载所有的对话及群组
        getChatManager().loadAllConversations();
        //getGroupManager().loadAllGroups();
    }

    private void closeDb() {
        DbHelper.getInstance().closeDb();
    }
}
