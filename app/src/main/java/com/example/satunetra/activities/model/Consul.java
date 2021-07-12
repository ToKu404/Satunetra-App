package com.example.satunetra.activities.model;

public class Consul {
    private int id;
    private String tag;
    private String date;

    public Consul(int id, String tag, String date) {
        this.id = id;
        this.tag = tag;
        this.date = date;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
