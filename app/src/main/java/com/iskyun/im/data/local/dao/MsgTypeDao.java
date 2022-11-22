package com.iskyun.im.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.iskyun.im.data.local.model.MsgType;

import java.util.List;

@Dao
public interface MsgTypeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(MsgType... msgTypes);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(MsgType... msgTypes);

    @Query("select * from msg_type")
    List<MsgType> loadAllMsgType();

    @Query("select * from msg_type where type = :type")
    MsgType loadMsgType(String type);

    @Delete
    void delete(MsgType... msgTypes);
}
