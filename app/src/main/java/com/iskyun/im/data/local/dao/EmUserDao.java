package com.iskyun.im.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hyphenate.easeui.domain.EaseUser;
import com.iskyun.im.data.local.model.EmUserEntity;

import java.util.List;

@Dao
public interface EmUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(EmUserEntity... users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<EmUserEntity> users);

    @Query("select * from em_users")
    List<EaseUser> loadAllEaseUsers();

    @Query("select username from em_users where lastModifyTimestamp + :arg0  <= :arg1")
    List<String> loadTimeOutEaseUsers(long arg0,long arg1);

}
