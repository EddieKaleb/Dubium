package com.dubium.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dubium.R;
import com.dubium.model.User;
import com.dubium.views.AptitudesActivity;
import com.dubium.views.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class GoogleModule {

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseManager mFirebaseDatabaseManager;

    private DatabaseReference mUserReference;


    private Context mContext;

    private String TAG = "GoogleModule";

    //Class Constructor
    public GoogleModule(Context context, FirebaseAuth mFirebaseAuth){

        this.mContext = context;

        this.mFirebaseAuth = mFirebaseAuth;
        this.mFirebaseDatabaseManager = new FirebaseDatabaseManager();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

    }

    public void signIn(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase.
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuth(account);
        } catch (ApiException e) {

            Log.w(TAG, "Google sign in failed", e);
        }
    }

    private void firebaseAuth(GoogleSignInAccount acct) {

        /*Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();*/

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //try {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                /* If sign in was successful, the user is stored on Firebase Database
                                and the MainActivity is started.
                                */

                                FirebaseUser fbUser = mFirebaseAuth.getCurrentUser();

                                mUserReference = FirebaseDatabase.getInstance().getReference().child("users").child(fbUser.getUid());

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

                                mUserReference.addListenerForSingleValueEvent(userListener);

                            } else {

                                // If sign in failed, a message is displayed to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(mContext, "Autentication failed", Toast.LENGTH_LONG).show();
                            }

                            //hideProgressDialog();
                        }
                    });
    }

    public void revokeAccess(){

        mGoogleSignInClient.revokeAccess();

    }

    public Intent getSignIntent(){
        return mGoogleSignInClient.getSignInIntent();
    }
}
