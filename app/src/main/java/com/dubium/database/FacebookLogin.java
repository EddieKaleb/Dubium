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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class FacebookLogin {

    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseManager mFirebaseDatabaseManager;
    private DatabaseReference mUserReference;

    private Context mContext;

    private static final String TAG = "FacebookLogin";


    public FacebookLogin(Context context, FirebaseAuth firebaseAuth){

        FacebookSdk.sdkInitialize(context.getApplicationContext());
        AppEventsLogger.activateApp(context);

        this.mFirebaseDatabaseManager = new FirebaseDatabaseManager();

        this.mContext = context;
        this.mFirebaseAuth = firebaseAuth;

        initializeFacebookSignIn();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        mCallbackManager.onActivityResult( requestCode, resultCode, data );

    }

    public void signIn(){

        LoginManager.getInstance().logInWithReadPermissions((Activity) mContext, Arrays.asList("email", "public_profile"));

    }

    private void initializeFacebookSignIn(){

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /* If sign in was successful, the user is stored on Firebase Database
                                and the MainActivity is started.
                             */
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

                            ValueEventListener userListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get Post object and use the values to update the UI
                                    User user = dataSnapshot.getValue(User.class);

                                    // If user don't exists
                                    if(user == null){
                                        Intent i = new Intent(mContext, AptitudesActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(i);
                                        ((Activity) mContext).finish();
                                    }
                                    // If user exists
                                    else{
                                        Intent i = new Intent(mContext, HomeActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(i);
                                        ((Activity) mContext).finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    // ...
                                }
                            };

                        } else {

                            // If sign in failed, a message is displayed to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
