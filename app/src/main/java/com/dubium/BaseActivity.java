package com.dubium;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by eddie on 10/01/2018.
 */

public class BaseActivity extends AppCompatActivity {

    public FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
    }
}
