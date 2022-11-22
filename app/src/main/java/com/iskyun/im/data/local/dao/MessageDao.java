package com.iskyun.im.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.iskyun.im.data.local.model.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insert(Message message);

    @Delete
    void delete(Message message);

    @Update
    void update(Message message);

    @Query("SELECT * FROM message")
    List<Message> getMessageList();

    @Query("SELECT * FROM message")
    List<Message> getMessages();

    @Query("SELECT * FROM message WHERE anchorId = :anchorId")
    Message getMessageByAnchorId(int anchorId);

}
