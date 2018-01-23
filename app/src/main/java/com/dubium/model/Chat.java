package com.dubium.model;

/**
 * Created by eddie on 22/01/2018.
 */

public class Chat {

    private String name;
    private String menssage;
    private String time;
    private String photoUrl;

    public Chat(String name, String menssage, String time, String photoUlr) {
        this.name = name;
        this.menssage = menssage;
        this.time = time;
        this.photoUrl = photoUlr;
    }

    public String getName() { return name; }
    public String getMenssage() { return menssage; }
    public String getTime() { return time; }
    public String getPhotoUrl() { return photoUrl; }
}
