package com.dubium.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.dubium.R;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends Activity {

    private DatabaseReference mDatabase;
    Intent activityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLogin();
            }
        }, 1000);
    }

    private void loadLogin() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.i("User", user.getDisplayName());

            DatabaseReference mUserReference;
            mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());

            FirebaseAuth.getInstance().signOut();

            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Context context = getBaseContext();

                    // If user don't exists
                    if(dataSnapshot.getValue() == null){
                        activityIntent = new Intent(context, AptitudesActivity.class);
                    }
                    else{
                        activityIntent = new Intent(getBaseContext(), HomeActivity.class);
                    }

                    startActivity(activityIntent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w("HomeActivity", "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };

            mUserReference.addListenerForSingleValueEvent(userListener);
        }

        else{
            activityIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(activityIntent);
            finish();
        }
    }

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
