package com.dubium.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dubium.model.User;
import com.dubium.views.AptitudesActivity;
import com.dubium.views.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by marcus-vinicius on 17/01/18.
 */

public class EmailPasswordLogin {

    private DatabaseReference mUserReference;
    private FirebaseAuth mFirebaseAuth;

    private Context mContext;

    private String TAG = "EmailPasswordLogin";


    public EmailPasswordLogin(Context context, FirebaseAuth firebaseAuth){
        this.mContext = context;
        this.mFirebaseAuth = firebaseAuth;

    }


}
