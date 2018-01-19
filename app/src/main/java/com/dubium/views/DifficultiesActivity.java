package com.dubium.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Subject;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

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

        mAptitudesList = (ArrayList<Subject>) getIntent().getExtras().get("Aptitudes");

        mDatabaseManager = new FirebaseDatabaseManager();

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDifficultiesList = mMinhasDisciplinas;

                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                User user = new User(fbUser.getUid(), fbUser.getDisplayName(), fbUser.getEmail());

                mDatabaseManager.saveUser(user);

                for(Subject s : mAptitudesList){
                    mDatabaseManager.addAptitudeToUser(fbUser.getUid(), s);
                }

                for(Subject s : mDifficultiesList){
                    mDatabaseManager.addDifficultieToUser(fbUser.getUid(), s);
                }
                Intent intent = new Intent(v.getContext(), LocationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
