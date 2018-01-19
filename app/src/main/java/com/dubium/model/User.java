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
    private String city;



    private double latitude;
    private double longitude;
    private String state;
    private String photoUrl;

    public User(String uId, String nome, String email, String photoUrl) {
        this.uId = uId;
        this.name = nome;
        this.photoUrl = photoUrl;
        this.email = email;

    }

    public User(String uId, String nome, String email) {
        this.uId = uId;
        this.name = nome;
        this.email = email;
    }

    public User(){}

    public void findAdress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        //PASSANDO A LATITUDE E LONGITUDE QUE TEMOS, E QUERENDO APENAS 1 RESULTADO
        addresses = geocoder.getFromLocation(latitude, longitude,1);

        if (addresses.size() > 0)
            address = addresses.get(0);

        this.state = address.getAdminArea();
        this.city = address.getLocality();
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

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
