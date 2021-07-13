package com.example.satunetra.helper;

import android.content.Context;

import com.example.satunetra.local.AppDatabase;
import com.example.satunetra.local.table.ConsulEntity;
import com.example.satunetra.local.table.UserEntity;

import java.util.List;

public class RoomHelper {
    private final AppDatabase roomDb;
    private boolean status;

    public RoomHelper(Context context){
        roomDb = AppDatabase.getInstance(context);
    }

    //read user
    public UserEntity readUser(){
        return roomDb.userDao().getUser();
    }

    public List<ConsulEntity> readHistory(){
        return roomDb.userDao().getHistory();
    }

    public boolean isUserExist(){
        return roomDb.userDao().isUserExist();
    }

    public boolean createUser(int id, String name) {
        UserEntity user = new UserEntity(id, name);
        roomDb.userDao().createUser(user).subscribe(() -> {
            status = true;
        }, throwable -> {
            status = false;

        });
        return status;
    }

    public boolean insertConsul(int id, String tag, String date){
        ConsulEntity consul = new ConsulEntity(id, tag,date);
        roomDb.userDao().addConsul(consul).subscribe(() -> {
            status = true;
        }, throwable -> {
            status = false;

        });
        return status;
    }
}
