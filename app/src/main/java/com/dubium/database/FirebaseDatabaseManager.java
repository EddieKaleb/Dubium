package com.dubium.database;

import android.content.Context;
import android.util.Log;
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
import java.util.List;
import java.util.Map;

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

        mDatabase.child("users").child(uId).child("aptitudes").child(subject.getId()).setValue(true);
    }

    public void addDifficultieToUser(String uId, Subject subject){

        mDatabase.child("users").child(uId).child("difficulties").child(subject.getId()).setValue(true);
    }

    public ArrayList<Subject> getUserAptitudes(String uId){

        return this.getUserSubjects(uId, "aptitudes");
    }

    public ArrayList<Subject> getUserDifficulties(String uId){

        return this.getUserSubjects(uId, "difficulties");
    }

    public ArrayList<Subject> getUserSubjects(String uId, String subjectsType){

        final ArrayList<Subject> list = new ArrayList<>();

        Query query = mDatabase.child("users").child(uId).child(subjectsType);

        /***** Pega a lista de ids de subjects de um user e a insere em um Map *****/
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> aptitudesData = (Map<String,Boolean>)dataSnapshot.getValue();

                /***** Para cada id inserido pega a subject correspondente e insere na listAptitudes *****/
                for (Map.Entry<String, Boolean> entry : aptitudesData.entrySet()) {
                    String key = entry.getKey();
                    boolean value = entry.getValue();

                    Query query = mDatabase.child("subjects").child(key);

                    query.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Subject subject = dataSnapshot.getValue(Subject.class);

                            Log.i("subject", subject.getId() + "   " + subject.getName());
                            list.add(subject);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return list;
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