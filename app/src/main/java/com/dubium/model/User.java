package com.dubium.model;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by eddie on 09/01/2018.
 */

public class User {

    private String uId;
    private String name;
    private String email;
    private String photoUrl;
    private UserAdress mUserAdress;

    public User(String uId, String nome, String email, String photoUrl, UserAdress mUserAdress) {
        this.uId = uId;
        this.name = nome;
        this.photoUrl = photoUrl;
        this.email = email;
        this.mUserAdress = mUserAdress;
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

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
