package com.dubium.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.dubium.R;
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

/**
 * Created by Marcus Vinícius on 12/01/18.
 */

public class GoogleLogin {

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabaseManager mFirebaseDatabaseManager;

    private Context context;

    private String TAG = "GoogleLogin";

    //Class Constructor
    public GoogleLogin(Context context, FirebaseAuth mFirebaseAuth){

        this.context = context;

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
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                /* If sign in was successful, the user is stored on Firebase Database
                                and the MainActivity is started.
                                */
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                mFirebaseDatabaseManager.saveUser(user);

                                //context.startActivity(new Intent(context, MainActivity.class));

                                Intent i = new Intent(context, HomeActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                                ((Activity) context).finish();
                            } else {

                                // If sign in failed, a message is displayed to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                Toast.makeText(context, "Autentication failed", Toast.LENGTH_LONG).show();
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
