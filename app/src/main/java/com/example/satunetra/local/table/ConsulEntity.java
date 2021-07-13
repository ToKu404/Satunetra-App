package com.example.satunetra.local.table;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "consul_entity")
public class ConsulEntity implements Serializable {
    @PrimaryKey
    private int id;

    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "date")
    private String data;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ConsulEntity(int id, String tag, String data) {
        this.id = id;
        this.tag = tag;
        this.data = data;
    }
}
