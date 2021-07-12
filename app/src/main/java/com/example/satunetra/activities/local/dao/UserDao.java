package com.example.satunetra.activities.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.satunetra.activities.local.table.ConsulEntity;
import com.example.satunetra.activities.local.table.UserEntity;
import com.example.satunetra.activities.model.Consul;
import com.example.satunetra.activities.model.User;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface UserDao {

    @Query("Select * FROM user_entity Limit 1")
    User getUser();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable createUser(UserEntity user);

    @Query("Select * FROM consul_entity")
    List<Consul> getHistory();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable addConsul(ConsulEntity consul);

    @Query("SELECT EXISTS(SELECT * FROM user_entity LIMIT 1)")
    boolean isUserExist();
}
