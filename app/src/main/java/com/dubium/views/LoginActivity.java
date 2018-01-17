package com.dubium.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.database.EmailPasswordLogin;
import com.dubium.database.FacebookLogin;
import com.dubium.database.GoogleLogin;
import com.dubium.model.User;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.container_login) LinearLayout mContainerLogin;
    @BindView(R.id.container_social_login) LinearLayout mContainerSocialLogin;
    @BindView(R.id.btn_google) RelativeLayout mBtnGoogle;
    @BindView(R.id.btn_facebook) RelativeLayout mBtnFacebook;
    @BindView(R.id.btn_cadastre_se) Button mBtnCadastreSe;
    @BindView(R.id.btn_login) Button mBtnLogin;
    @BindView(R.id.btn_cadastrar) Button mBtnCadastrar;
    @BindView(R.id.btn_voltar) Button mBtnVoltar;
    @BindView(R.id.et_nome) EditText mEtNome;
    @BindView(R.id.et_email) EditText mEtEmail;
    @BindView(R.id.et_senha) EditText mEtSenha;
    @BindView(R.id.et_confirmar_senha) EditText mEtConfirmarSenha;
    @BindView(R.id.tv_login_first) TextView mTvLoginFirst;
    @BindView(R.id.tv_login_second) TextView mTvLoginSecond;

    private GoogleLogin mGoogleLogin;
    private FacebookLogin mFacebookLogin;
    private EmailPasswordLogin mEmailPasswordLogin;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Code block to generate KeyHash to Facebook login setup.
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.dubium",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        TransitionManager.beginDelayedTransition(mContainerLogin, new Fade());
        mBtnCadastrar.setVisibility(View.GONE);
        mBtnVoltar.setVisibility(View.GONE);
        mEtNome.setVisibility(View.GONE);
        mEtConfirmarSenha.setVisibility(View.GONE);

        mGoogleLogin = new GoogleLogin(this, mFirebaseAuth);
        mFacebookLogin = new FacebookLogin(this, mFirebaseAuth);
        mEmailPasswordLogin = new EmailPasswordLogin(this, mFirebaseAuth);

        initializeAuthStateListener();

        mBtnCadastreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(mContainerLogin, new AutoTransition());
                mEtNome.requestFocus();
                mContainerSocialLogin.setVisibility(View.GONE);
                mTvLoginFirst.setText("Cadastre-se");
                mTvLoginSecond.setVisibility(View.GONE);
                mEtNome.setVisibility(View.VISIBLE);
                mEtConfirmarSenha.setVisibility(View.VISIBLE);
                mBtnLogin.setVisibility(View.GONE);
                mBtnCadastreSe.setVisibility(View.GONE);
                mBtnCadastrar.setVisibility(View.VISIBLE);
                mBtnVoltar.setVisibility(View.VISIBLE);
            }
        });

        mBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(mContainerLogin, new AutoTransition());
                mTvLoginFirst.setText(R.string.tv_login_first);
                mTvLoginSecond.setVisibility(View.VISIBLE);
                mContainerSocialLogin.setVisibility(View.VISIBLE);
                mEtNome.setVisibility(View.GONE);
                mEtConfirmarSenha.setVisibility(View.GONE);
                mBtnLogin.setVisibility(View.VISIBLE);
                mBtnCadastreSe.setVisibility(View.VISIBLE);
                mBtnCadastrar.setVisibility(View.GONE);
                mBtnVoltar.setVisibility(View.GONE);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateForm()) {
                    return;
                }

                mEmailPasswordLogin.signIn(mEtEmail.getText().toString(), mEtSenha.getText().toString());
            }
        });

        mBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                googleSignIn();

            }
        });

        mBtnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                facebookSignIn();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mGoogleLogin.revokeAccess();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Sign in.
        if (requestCode == RC_GOOGLE_SIGN_IN) {

            mGoogleLogin.signIn(data);
        }
        // Facebook Sign in.
        else{
            mFacebookLogin.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Sign in with Google
    private void googleSignIn() {

        Intent signInIntent = mGoogleLogin.getSignIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

    }

    // Sign in with Facebook
    private void facebookSignIn(){

        mFacebookLogin.signIn();

    }

    // Initialize the AuthStateListener to verify if user is logged.
    private void initializeAuthStateListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEtEmail.setError("Campo vazio!");
            valid = false;
        } else {
            mEtEmail.setError(null);
        }

        String password = mEtSenha.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mEtSenha.setError("Campo vazio!");
            valid = false;
        } else {
            mEtSenha.setError(null);
        }

        return valid;
    }


}
