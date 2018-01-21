package com.dubium.model;

/**
 * Created by marcus-vinicius on 18/01/18.
 */

public class Message {

    private String text;
    private String name;
    private String uId;
    private String photoUrl;
    private String time;

    public Message() {
    }

    public Message(String text, String name, String uId, String time, String photoUrl) {
        this.text = text;
        this.name = name;
        this.uId = uId;
        this.time = time;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getuId(){ return uId; }

    public void setuId(String uId){
        this.uId = uId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setTime (String time) {this.time = time;}

    public String getTime() {
        return time;
    }
}
