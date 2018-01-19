package com.dubium.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.database.EmailPasswordModule;
import com.dubium.database.FacebookModule;
import com.dubium.database.GoogleModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

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

    private GoogleModule mGoogleModule;
    private FacebookModule mFacebookModule;
    private EmailPasswordModule mEmailPasswordModule;
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

        mGoogleModule = new GoogleModule(this, mFirebaseAuth);
        mFacebookModule = new FacebookModule(this, mFirebaseAuth);
        mEmailPasswordModule = new EmailPasswordModule(this, mFirebaseAuth);

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

                if (!validateLoginForm()) {
                    return;
                }

                mEmailPasswordModule.signIn(mEtEmail.getText().toString(), mEtSenha.getText().toString());
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

        mBtnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateRegisterForm()) {
                    return;
                }

                String email = mEtEmail.getText().toString();
                String password = mEtSenha.getText().toString();
                String name = mEtNome.getText().toString();

                mEmailPasswordModule.createAccount(email, password, name);
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
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        mGoogleModule.revokeAccess();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Sign in.
        if (requestCode == RC_GOOGLE_SIGN_IN) {

            mGoogleModule.signIn(data);
        }
        // Facebook Sign in.
        else{
            mFacebookModule.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Sign in with Google
    private void googleSignIn() {

        Intent signInIntent = mGoogleModule.getSignIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

    }

    // Sign in with Facebook
    private void facebookSignIn(){

        mFacebookModule.signIn();

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

    private boolean validateLoginForm() {
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

    private boolean validateRegisterForm(){
        boolean valid = true;

        String name = mEtNome.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mEtNome.setError("Campo vazio!");
            valid = false;
        } else {
            mEtNome.setError(null);
        }

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
        }
        else if (password.length() < 6){
            mEtSenha.setError("A senha deve conter pelos menos 6 caracteres!");
            valid = false;
        }
        else {
            mEtSenha.setError(null);
        }

        String passwordConfirm = mEtConfirmarSenha.getText().toString();
        if (TextUtils.isEmpty(passwordConfirm)) {
            mEtConfirmarSenha.setError("Campo vazio!");
            valid = false;
        } else {
            mEtConfirmarSenha.setError(null);
        }

        if(!password.equals(passwordConfirm)){
            mEtConfirmarSenha.setError("Senha incorreta!");
            valid = false;
        }

        return valid;
    }
}
