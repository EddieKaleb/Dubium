package com.dubium.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.fragments.ProfileFragment;
import com.dubium.model.Subject;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;

public class DifficultiesActivity extends AptitudesActivity {

    ArrayList<Subject> mAptitudesList;
    ArrayList<Subject> mDifficultiesList;
    FirebaseDatabaseManager mDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mTvProsseguir.setText("PROSSEGUIR");
        mTvFirst.setText("Para continuar");
        mTvSecond.setText("Adicione suas dificuldades!");
        mIvIcon.setImageResource(R.drawable.ic_help);

        if (getIntent().getExtras().getString("calling-activity") != null){
            setUserSubjects(mFirebaseAuth.getCurrentUser().getUid(), "difficulties", mAdapterSetup);
            mViewMensagem.setVisibility(View.GONE);
            mLvDisciplinas.setVisibility(View.VISIBLE);
            mLvDisciplinas.setAdapter(mAdapterSetup);
            mTvProsseguir.setText("SALVAR");
            mTvFirst.setText("Você está sem dificuldades");
            mTvSecond.setText("Adicione pelo menos uma!");
            mTvProsseguir.setVisibility(View.VISIBLE);
        } else {
            mLvDisciplinas.setAdapter(mAdapterSearch);
        }

        mAptitudesList = (ArrayList<Subject>) getIntent().getExtras().get("Aptitudes");

        mDatabaseManager = new FirebaseDatabaseManager();

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDifficultiesList = mMinhasDisciplinas;

                if (getIntent().getExtras().getString("calling-activity") != null) {
                    HashMap<String, Boolean> subjects = new HashMap<>();

                    for (Subject s : mMinhasDisciplinas) subjects.put(s.getId(), true);
                    mFirebaseDatabaseManager.addDifficultiesToUser(mFirebaseAuth.getCurrentUser().getUid(), subjects);
                    finish();
                } else {

                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                    User user = new User(fbUser.getUid(), fbUser.getDisplayName(), fbUser.getEmail());

                    mDatabaseManager.saveUser(user);

                    for (Subject s : mAptitudesList) {
                        mDatabaseManager.addAptitudeToUser(fbUser.getUid(), s);
                    }

                    for (Subject s : mDifficultiesList) {
                        mDatabaseManager.addDifficultieToUser(fbUser.getUid(), s);
                    }
                    Intent intent = new Intent(v.getContext(), LocationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
