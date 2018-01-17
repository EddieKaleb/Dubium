package com.dubium.model;

import java.io.Serializable;

/**
 * Created by marcus-vinicius on 16/01/18.
 */

public class Subject implements Serializable{

    private String id;
    private String name;

    public Subject(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
