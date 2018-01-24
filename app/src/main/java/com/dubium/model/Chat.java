package com.dubium.model;

/**
 * Created by eddie on 22/01/2018.
 */

public class Chat {

    private String name;
    private String message;
    private String time;
    private String photoUrl;

    public Chat(String name, String message, String time, String photoUlr) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.photoUrl = photoUlr;
    }

    public Chat(){

    }

    public String getName() { return name; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getPhotoUrl() { return photoUrl; }

    public void setName(String name) {
        this.name = name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
