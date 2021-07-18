package com.example.satunetra.local.table;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "consul_entity")
public class ConsulEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "instruction")
    private String instruction;

    @ColumnInfo(name = "feel")
    private String feel;

    @ColumnInfo(name = "date")
    private String date;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getFeel() {
        return feel;
    }

    public void setFeel(String feel) {
        this.feel = feel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ConsulEntity(String instruction, String feel, String date) {
        this.instruction = instruction;
        this.date = date;
        this.feel = feel;
    }
}
