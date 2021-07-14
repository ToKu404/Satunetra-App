package com.example.satunetra.model;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    String id;
    String message;
    String url;
    String title;
    String description;
    LocalDateTime dateTime;

    public Message(){

    }


    public Message(String title, String description, String url, LocalDateTime dateTime){
        this.message = "";
        this.title = title;
        this.description = description;
        this.url = url;
        this.id = "2";
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
