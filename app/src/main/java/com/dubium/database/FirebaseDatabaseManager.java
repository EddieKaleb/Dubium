package com.dubium.database;

import android.content.Context;
import android.widget.Toast;

import com.dubium.model.Subject;
import com.dubium.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

        user.setCity("Campina Grande");
        user.setState("Paraíba");
        user.setLatitude(-7.2305600);
        user.setLongitude(-35.8811100);

        mDatabase.child("users").child(uId).setValue(user);
    }

    public void addAptitudeToUser(String uId, Subject subject){

        mDatabase.child("users").child(uId).child("aptitudes").child(subject.getId()).setValue(true);
    }

    public void addDifficultieToUser(String uId, Subject subject){

        mDatabase.child("users").child(uId).child("difficulties").child(subject.getId()).setValue(true);
    }

    public void addSubjects(String id, Subject subject){

        mDatabase.child("subjects").child(id).setValue(subject);
    }

    public ArrayList<Subject> getSubjects(Context c){
        final Context context = c;

        final ArrayList<Subject> list = new ArrayList<>();

        Query query = mDatabase.child("subjects");

        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Subject subject;

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    subject = objSnapshot.getValue(Subject.class);
                    list.add(subject);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "ERRO", Toast.LENGTH_SHORT).show();
            }
        });

        return list;
    }

}