package com.iskyun.im.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.iskyun.im.data.converter.DateConverter;
import com.iskyun.im.data.local.dao.EmUserDao;
import com.iskyun.im.data.local.dao.MsgTypeDao;
import com.iskyun.im.data.local.model.EmUserEntity;
import com.iskyun.im.data.local.model.MsgType;

@Database(entities = {
        MsgType.class,
        EmUserEntity.class,
}, version = 3)

@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract MsgTypeDao getMsgTypeDao();

    public abstract EmUserDao userDao();


}
