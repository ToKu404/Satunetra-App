package com.example.satunetra.activities.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.satunetra.activities.local.dao.UserDao;
import com.example.satunetra.activities.local.table.ConsulEntity;
import com.example.satunetra.activities.local.table.UserEntity;


@Database(entities = {UserEntity.class, ConsulEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static AppDatabase instance;

    public synchronized static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Satunetra Db").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }


}
