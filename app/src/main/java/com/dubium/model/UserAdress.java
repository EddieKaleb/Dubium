package com.dubium.model;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Marcus Vinicius on 15/01/18.
 */

public class UserAdress {

    private double latitude;
    private double longitude;
    private String city;
    private String state;

    public UserAdress(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserAdress(){}

    public void findAdress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        //PASSANDO A LATITUDE E LONGITUDE QUE TEMOS, E QUERENDO APENAS 1 RESULTADO
        addresses = geocoder.getFromLocation(latitude, longitude,1);

        if (addresses.size() > 0)
            address = addresses.get(0);

        //ATRIBUIÇÃO DE DADOS AO USER - Implementar os sets
        this.state = address.getAdminArea();
        this.city = address.getLocality();
        this.latitude = latitude;
        this.longitude = longitude;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
