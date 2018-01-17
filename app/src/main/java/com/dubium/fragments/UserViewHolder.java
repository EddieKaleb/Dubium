package com.dubium.fragments;

/**
 * Created by marcus-vinicius on 15/01/18.
 */

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserViewHolder {

    private String uId;
    private String name;
    private String photoUrl;
    private int aptidoesComuns;
    private int dificuldadesComuns;
    private double distancia;

    public UserViewHolder(String nome, String email, String photoUrl, int aptidoesComuns, int dificuldadesComuns, int distancia) {
        this.name = nome;
        this.photoUrl = photoUrl;
        this.aptidoesComuns = aptidoesComuns;
        this.dificuldadesComuns = dificuldadesComuns;
        this.distancia = distancia;
    }

    public UserViewHolder(){};

    public String getuId(){
        return this.uId;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAptidoesComuns() {
        if (aptidoesComuns == 1) return "• 1 aptidão em comum";
        if (aptidoesComuns > 1) return "• " + aptidoesComuns + " aptidões em comum";
        return "";
    }

    public void setAptidoesComuns(int aptidoesComuns) {
        this.aptidoesComuns = aptidoesComuns;
    }

    public String getDificuldadesComuns() {
        if (dificuldadesComuns == 1) return "• 1 dificuldade em comum";
        if (dificuldadesComuns > 1) return "• " + dificuldadesComuns + " dificuldades em comum";
        return "";
    }

    public void setDificuldadesComuns(int dificuldadesComuns) {
        this.dificuldadesComuns = dificuldadesComuns;
    }

    public String getDistancia() {
        if (distancia == 1) return "aprox. 1 km";
        return "aprox. " + (distancia/1000) + " kms";
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

}

