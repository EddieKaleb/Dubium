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

    public void signIn(String email, String password){
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginEmail", "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

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
                            // If sign in fails, display a message to the user.
                            Log.w("LoginEmail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Falha na autenticação.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}