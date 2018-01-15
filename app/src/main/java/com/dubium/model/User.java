package com.dubium.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.dubium.Manifest;

import java.io.IOException;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by eddie on 09/01/2018.
 */

public class User {

    private String nome;
    private String email;
    private String fotoUrl;
    private int aptidoesComuns;
    private int dificuldadesComuns;
    private int distancia;

    public User(String nome, String fotoUrl, int aptidoesComuns, int dificuldadesComuns, int distancia) {
        this.nome = nome;
        this.fotoUrl = fotoUrl;
        this.aptidoesComuns = aptidoesComuns;
        this.dificuldadesComuns = dificuldadesComuns;
        this.distancia = distancia;
    }

    public User(String name, String email){
        this.nome = name;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail(){ return email; }

    public void setEmail(){ this.email = email; }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
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
        return "aprox. " + distancia + " kms";
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public void buscarEndereco(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        //PASSANDO A LATITUDE E LONGITUDE QUE TEMOS, E QUERENDO APENAS 1 RESULTADO
        addresses = geocoder.getFromLocation(latitude, longitude,1);

        if (addresses.size() > 0)
            address = addresses.get(0);
        //ATIBUIÇÃO DE DADOS AO USER

    }


}
