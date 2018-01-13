package com.dubium.database;

import com.dubium.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class FirebaseDatabaseManager {

    private DatabaseReference mDatabase;

    public FirebaseDatabaseManager(){

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    // Save an user on database
    public void saveUser(FirebaseUser fbUser) {

        String userId = fbUser.getUid();
        String userEmail = fbUser.getEmail();
        String userName = fbUser.getDisplayName();

        User user = new User(userName, userEmail);

        mDatabase.child("users").child(userId).setValue(user);
    }
}
