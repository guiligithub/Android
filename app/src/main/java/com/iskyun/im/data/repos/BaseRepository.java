package com.iskyun.im.data.repos;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMChatManager;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.data.local.DbHelper;

public class BaseRepository {


    public Context getContext() {
        return ImLiveApp.get().getApplicationContext();
    }

    /**
     * return a new liveData
     * @param item
     * @param <T>
     * @return
     */
    public <T> LiveData<T> createLiveData(T item) {
        return new MutableLiveData<>(item);
    }

    /**
     * init room
     */
    public void initDb() {
        DbHelper.getInstance().initDb(getCurrentUser());
    }

    /**
     * 获取当前用户
     * @return
     */
    public String getCurrentUser() {
        return DbHelper.getInstance().getCurrentUser();
    }

    public EMChatManager getChatManager() {
        return DbHelper.getInstance().getEMClient().chatManager();
    }
}
