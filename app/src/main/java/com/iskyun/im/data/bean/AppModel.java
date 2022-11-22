package com.iskyun.im.data.bean;

import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.manager.EasePreferenceManager;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.data.local.DbHelper;
import com.iskyun.im.data.local.dao.EmUserDao;
import com.iskyun.im.data.local.model.EmUserEntity;
import com.iskyun.im.utils.manager.SpManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppModel {
    protected Context context = null;
    //用户属性数据过期时间设置
    public static long userInfoTimeOut =  7 * 24 * 60 * 60 * 1000;

    public AppModel(Context ctx){
        context = ctx;
        SpManager.init(context);
    }

    /**
     * 检查是否是第一次安装登录
     * 默认值是true, 需要在用api拉取完会话列表后，就其置为false.
     * @return
     */
    public boolean isFirstInstall() {
        SharedPreferences preferences = ImLiveApp.get().getSharedPreferences("first_install", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_first_install", true);
    }

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        SpManager.getInstance().setCurrentUserName(username);
    }

    /**
     * 保存当前用户密码
     * 此处保存密码是为了查看多端设备登录是，调用接口不再输入用户名及密码，实际开发中，不可在本地保存密码！
     * 注：实际开发中不可进行此操作！！！
     * @param pwd
     */
    public void setCurrentUserPwd(String pwd) {
        SpManager.getInstance().setCurrentUserPwd(pwd);
    }

    public boolean isMsgRoaming() {
        return SpManager.getInstance().isMsgRoaming();
    }

    public boolean isShowMsgTyping() {
        return SpManager.getInstance().isShowMsgTyping();
    }

    /**
     * 保存未发送的文本消息内容
     * @param toChatUsername
     * @param content
     */
    public void saveUnSendMsg(String toChatUsername, String content) {
        EasePreferenceManager.getInstance().saveUnSendMsgInfo(toChatUsername, content);
    }

    /**
     *
     * @param toChatUsername
     * @return
     */
    public String getUnSendMsg(String toChatUsername) {
        return EasePreferenceManager.getInstance().getUnSendMsgInfo(toChatUsername);
    }

    public boolean updateContactList(List<EaseUser> contactList) {
        List<EmUserEntity> userEntities = EmUserEntity.parseList(contactList);
        EmUserDao dao = DbHelper.getInstance().getUserDao();
        if(dao != null) {
            dao.insert(userEntities);
            return true;
        }
        return false;
    }

    public Map<String, EaseUser> getAllUserList() {
        EmUserDao dao = DbHelper.getInstance().getUserDao();
        if(dao == null) {
            return new HashMap<>();
        }
        Map<String, EaseUser> map = new HashMap<>();
        List<EaseUser> users = dao.loadAllEaseUsers();
        if(users != null && !users.isEmpty()) {
            for (EaseUser user : users) {
                map.put(user.getUsername(), user);
            }
        }
        return map;
    }

    /**
     * 查找有关用户用户属性过期的用户ID
     *
     */
    public List<String> selectTimeOutUsers() {
        DbHelper dbHelper = DbHelper.getInstance();
        List<String> users = null;
        if(dbHelper.getUserDao() != null) {
            users = dbHelper.getUserDao().loadTimeOutEaseUsers(userInfoTimeOut,System.currentTimeMillis());
        }
        return users;
    }
}
