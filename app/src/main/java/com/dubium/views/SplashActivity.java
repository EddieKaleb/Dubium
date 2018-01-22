package com.dubium.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dubium.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

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

        Intent activityIntent;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            activityIntent = new Intent(getBaseContext(), HomeActivity.class);
        }
        else{
            activityIntent = new Intent(getBaseContext(), LoginActivity.class);
        }

        startActivity(activityIntent);
        finish();
    }
}
