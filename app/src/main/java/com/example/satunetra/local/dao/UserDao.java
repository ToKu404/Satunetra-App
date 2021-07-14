package com.example.satunetra.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.satunetra.local.table.ConsulEntity;
import com.example.satunetra.local.table.UserEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface UserDao {

    @Query("Select * FROM user_entity Limit 1")
    UserEntity getUser();

    @Query("UPDATE user_entity SET first=:value WHERE id = :id")
    Completable firstTake(boolean value, int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable createUser(UserEntity user);

    @Query("Select * FROM consul_entity")
    List<ConsulEntity> getHistory();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable addConsul(ConsulEntity consul);

    @Query("SELECT EXISTS(SELECT * FROM user_entity LIMIT 1)")
    boolean isUserExist();
}
