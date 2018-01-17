package com.dubium.database;

import android.util.Log;

import com.dubium.model.Subject;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class FirebaseDatabaseManager {

    private DatabaseReference mDatabase;

    public FirebaseDatabaseManager(){

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    // Save an user on database
    public void saveUser(User user) {

        String uId = user.getUid();

        mDatabase.child("users").child(uId).setValue(user);
    }

    public void addAptitudeToUser(String uId, Subject subject){

        mDatabase.child("users").child(uId).child("aptitudes").child(subject.getId()).setValue(subject.getName());
    }

    public void addDifficultieToUser(String uId, Subject subject){

        mDatabase.child("users").child(uId).child("difficulties").child(subject.getId()).setValue(subject.getName());
    }
}
