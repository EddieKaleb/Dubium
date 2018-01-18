package com.dubium.model;

/**
 * Created by eddie on 09/01/2018.
 */

public class User {

    private String uId;
    private String name;
    private String email;
    private String photoUrl;
    private UserAddress userAddress;

    public User(String uId, String nome, String email, String photoUrl, UserAddress userAddress) {
        this.uId = uId;
        this.name = nome;
        this.photoUrl = photoUrl;
        this.email = email;
        this.userAddress = userAddress;
    }

    public User(String uId, String nome, String email) {
        this.uId = uId;
        this.name = nome;
        this.email = email;
    }

    public User(){}

    public String getUid(){
        return uId;
    }

    public void setUid(String uId){
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail(){ return email; }

    public void setEmail(){ this.email = email; }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
