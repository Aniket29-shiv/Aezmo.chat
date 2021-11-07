package com.aniket.aezmodate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.aniket.aezmodate.model.ConversationBean;

import java.util.List;

@Dao
public interface ConversationDao {
    @Insert
    void insertAll(ConversationDao... users);

    @Delete
    void delete(ConversationBean conversation);

    @Update
    void update(ConversationBean conversation);

    @Query("SELECT * FROM ConversationBean")
    List<ConversationBean> getAll();

    @Query("DELETE FROM conversationBean")
    void nukeTable();

//    @Query("SELECT message FROM ConversationBean " + " WHERE userId LIKE :selfId AND peerId LIKE :targetId")
//    List<ConversationBean> getChatHistory(String selfId, String targetId, String message);

}
