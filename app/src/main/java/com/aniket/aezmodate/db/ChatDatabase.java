package com.aniket.aezmodate.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aniket.aezmodate.dao.MsgDao;
import com.aniket.aezmodate.model.MessageBean;

@Database(entities = {MessageBean.class}, version = 1, exportSchema = true)
public abstract class ChatDatabase extends RoomDatabase {
    public abstract MsgDao msgDao();

    private static ChatDatabase INSTANCE;

    public static ChatDatabase getDbInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ChatDatabase.class, "DB_NAME")
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }
}
