package com.dubium.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dubium.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        TransitionManager.beginDelayedTransition(mContainerLogin, new Fade());
        mBtnCadastrar.setVisibility(View.GONE);
        mBtnVoltar.setVisibility(View.GONE);
        mEtNome.setVisibility(View.GONE);
        mEtConfirmarSenha.setVisibility(View.GONE);

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
                Intent intent = new Intent(v.getContext(), AptitudesActivity.class);
                startActivity(intent);
            }
        });
    }
}
