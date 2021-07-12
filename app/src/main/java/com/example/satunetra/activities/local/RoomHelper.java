package com.example.satunetra.activities.local;

import android.content.Context;

import com.example.satunetra.activities.local.table.ConsulEntity;
import com.example.satunetra.activities.local.table.UserEntity;
import com.example.satunetra.activities.model.Consul;
import com.example.satunetra.activities.model.User;

import java.util.List;

public class RoomHelper {
    private final AppDatabase roomDb;
    private boolean status;

    public RoomHelper(Context context){
        roomDb = AppDatabase.getInstance(context);
    }

    //read user
    public User readUser(){
        return roomDb.userDao().getUser();
    }

    public List<Consul> readHistory(){
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
