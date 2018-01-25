package com.dubium.database;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dubium.R;
import com.dubium.model.Subject;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

import fisk.chipcloud.ChipCloud;

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class FirebaseDatabaseManager {

    private DatabaseReference mDatabase;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;

    public FirebaseDatabaseManager() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
    }

    // Save an user on database
    public void saveUser(User user) {
        String uId = user.getUid();
        mDatabase.child("users").child(uId).setValue(user);
    }

    public void saveProfilePhoto(String uId, String uri) {
        mDatabase.child("users").child(uId).setValue(uri);
    }

    public void addAptitudeToUser(String uId, Subject subject) {
        mDatabase.child("users").child(uId).child("aptitudes").child(subject.getId()).setValue(true);
    }

    public void addAptitudesToUser(String uId, HashMap<String, Boolean> subjects) {
        mDatabase.child("users").child(uId).child("aptitudes").setValue(subjects);
    }

    public void addDifficultiesToUser(String uId, HashMap<String, Boolean> subjects) {
        mDatabase.child("users").child(uId).child("difficulties").setValue(subjects);
    }

    public void addDifficultieToUser(String uId, Subject subject) {
        mDatabase.child("users").child(uId).child("difficulties").child(subject.getId()).setValue(true);
    }

    public void setUserAptitudes(String uId, final ChipCloud mChipsAptitudes) {
        setUserSubjects(uId, "aptitudes", mChipsAptitudes);
    }

    public void setUserDifficulties(String uId, final ChipCloud mChipsDifficulties) {
        setUserSubjects(uId, "difficulties", mChipsDifficulties);
    }

    public void setUserSubjects(String uId, String subjectsType, final ChipCloud mChips) {

        final ArrayList<Subject> list = new ArrayList<>();

        Query query = mDatabase.child("users").child(uId).child(subjectsType);

        /***** Pega a lista de ids de subjects de um user e a insere em um Map *****/
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {

                    String key = objDataSnapshot.getKey();

                    Query query = mDatabase.child("subjects").child(key);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Subject subject = dataSnapshot.getValue(Subject.class);
                            mChips.addChip(subject);
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
    }

    public void getUserSubjects(String uId, final String subjectsType, final TextView view, final TextView viewQuant) {
        final ArrayList<Subject> list = new ArrayList<>();

        Query query = mDatabase.child("users").child(uId).child(subjectsType);

        /***** Pega a lista de ids de subjects de um user e a insere em um Map *****/
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            int cont = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot objDataSnapshot : dataSnapshot.getChildren()) {

                    String key = objDataSnapshot.getKey();
                    cont++;

                    Query query = mDatabase.child("subjects").child(key);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Subject subject = dataSnapshot.getValue(Subject.class);
                            //view.setText(""+list.get(0).getName());

                            Query query2;
                            if(subjectsType.equals("aptitudes"))
                                query2 = mDatabase.child("users").child(currentUser.getUid()).child("difficulties");
                            else
                                query2 = mDatabase.child("users").child(currentUser.getUid()).child("aptitudes");
                            query2.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                        if(ds.getKey().equals(subject.getId()))
                                            view.setText(" • " + subject.getName());
                                    }
                                    if(view.getText().equals("") || view.getText().equals(""))
                                        view.setText(" • " + subject.getName());
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                if(cont > 0)
                    viewQuant.setText("e mais "+ cont);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void addSubjects(String id, Subject subject) {
        mDatabase.child("subjects").child(id).setValue(subject);
    }

    public ArrayList<Subject> getSubjects(Context c) {
        final Context context = c;

        final ArrayList<Subject> list = new ArrayList<>();

        Query query = mDatabase.child("subjects");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public String initializeUserChat(String uId, String friendId){
        String chatId = mDatabase.child("chats").push().getKey();

        /***** INSERE O CHAT NO USUÁRIO ATUAL *****/
        mDatabase.child("users").child(uId).child("conversations").child(friendId).child(chatId).setValue(true);

        /***** INSERE O CHAT NO USUÁRIO REMETENTE *****/
        mDatabase.child("users").child(friendId).child("conversations").child(uId).child(chatId).setValue(true);

        return chatId;
    }
}