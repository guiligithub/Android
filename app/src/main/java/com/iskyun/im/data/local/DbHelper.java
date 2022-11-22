package com.iskyun.im.data.local;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;
import com.iskyun.im.ImLiveApp;
import com.iskyun.im.data.local.dao.EmUserDao;
import com.iskyun.im.data.local.dao.MsgTypeDao;
import com.iskyun.im.utils.LogUtils;
import com.iskyun.im.utils.MD5;

public class DbHelper {
    private static final String TAG = "DbHelper";
    private static volatile DbHelper instance;
    private String currentUser;
    private AppDatabase mDatabase;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();


    private DbHelper() {
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化数据库
     *
     * @param user
     */
    public void initDb(String user) {
        if (currentUser != null) {
            if (TextUtils.equals(currentUser, user)) {
                LogUtils.i("you have opened the db");
                return;
            }
            closeDb();
        }
        this.currentUser = user;
        String userMd5 = MD5.encrypt2MD5(user);
        // 以下数据库升级设置，为升级数据库将清掉之前的数据，如果要保留数据，慎重采用此种方式
        // 可以采用addMigrations()的方式，进行数据库的升级
        String dbName = String.format("em_%1$s.db", userMd5);
        LogUtils.i("db name = " + dbName);
        mDatabase = Room.databaseBuilder(ImLiveApp.get(), AppDatabase.class, dbName)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        mIsDatabaseCreated.postValue(true);
    }

    public String getCurrentUser() {
        return getEMClient().getCurrentUser();
    }

    /**
     * 获取IM SDK的入口类
     * @return
     */
    public EMClient getEMClient() {
        return EMClient.getInstance();
    }

    /**
     * 关闭数据库
     */
    public void closeDb() {
        if(mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        currentUser = null;
    }

    public MsgTypeDao getMsgTypeDao() {
        if(mDatabase != null) {
            return mDatabase.getMsgTypeDao();
        }
        EMLog.i(TAG, "get msgTypeManageDao failed, should init db first");
        return null;
    }

    public EmUserDao getUserDao() {
        if(mDatabase != null) {
            return mDatabase.userDao();
        }
        EMLog.i(TAG, "get userDao failed, should init db first");
        return null;
    }

}
