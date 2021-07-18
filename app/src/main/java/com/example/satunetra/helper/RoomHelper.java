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

    public boolean firstTake(int id){
        roomDb.userDao().firstTake(true, id).subscribe(()->{
            status = true;
        }, throwable -> {
            status = false;
        });
        return status;
    }

    public String readHistory(){
        String temp = "Berikut Riwayat Konsultasi Anda : \n";
        for(ConsulEntity riwayat : roomDb.userDao().getHistory()){
            temp += riwayat.getDate() + " ";
            temp += "Anda Merasa " + riwayat.getFeel();
            temp += " Dan Kami memutarkan Anda " + riwayat.getInstruction() + " .\n";
        }
        return temp;
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

    public boolean insertConsul(String instruction, String feel, String date){
        ConsulEntity consul = new ConsulEntity(instruction,feel, date);
        roomDb.userDao().addConsul(consul).subscribe(() -> {
            status = true;
        }, throwable -> {
            status = false;

        });
        return status;
    }
}
