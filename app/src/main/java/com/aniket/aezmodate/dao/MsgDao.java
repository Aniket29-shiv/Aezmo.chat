package com.aniket.aezmodate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.aniket.aezmodate.model.MessageBean;

@Dao
public interface MsgDao {

    @Query("SELECT * FROM messageBean")
    List<MessageBean> getAll();

    @Insert
    void insert(MessageBean task);

    @Delete
    void delete(MessageBean task);

    @Update
    void update(MessageBean task);

    @Query("DELETE FROM messageBean")
    void nukeTable();

//    @Query("SELECT message FROM msgDao " + " WHERE userId LIKE :selfId AND peerId LIKE :targetId")
//    List<MsgDao> getChatHistory(String selfId, String targetId, String message);
}
