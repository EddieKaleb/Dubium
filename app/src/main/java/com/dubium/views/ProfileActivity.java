package com.dubium.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

public class ProfileActivity extends Activity {

    RelativeLayout mBtnChat;

    ImageView mIvFotoPerfil;

    FlexboxLayout mAptidoesContainer;
    FlexboxLayout mDificuldadesContainer;

    ChipCloudConfig config;
    ChipCloud mChipsAptitudes;
    ChipCloud mChipsDifficulties;

    FirebaseDatabaseManager mFirebaseDatabaseManager;
    FirebaseStorage mFirebaseStorage;
    StorageReference mProfilePhotosStorageReference;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        init();
        loadData();

    }

    private void init() {

        config = new ChipCloudConfig().selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#E5E5E5")).checkedTextColor(Color.parseColor("#666666"))
                .uncheckedChipColor(Color.parseColor("#E5E5E5")).uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true);

        mBtnChat = (RelativeLayout) findViewById(R.id.btn_chat);
        mIvFotoPerfil = (ImageView) findViewById(R.id.iv_foto_perfil);
        mAptidoesContainer = (FlexboxLayout) findViewById(R.id.aptidoes_container);
        mDificuldadesContainer = (FlexboxLayout) findViewById(R.id.dificuldades_container);

        mChipsAptitudes = new ChipCloud(this, mAptidoesContainer, config);
        mChipsDifficulties = new ChipCloud(this, mDificuldadesContainer, config);


        mFirebaseDatabaseManager = new FirebaseDatabaseManager();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePhotosStorageReference = mFirebaseStorage.getReference().child("profile_photos");

        mBtnChat.setVisibility(View.VISIBLE);

    }

    private void loadData() {
        String uid = getIntent().getExtras().getString("uid");
        mFirebaseDatabaseManager.setUserAptitudes(uid, mChipsAptitudes);
        mFirebaseDatabaseManager.setUserDifficulties(uid, mChipsDifficulties);
        mFirebaseDatabaseManager.setProfilePhoto(uid, mIvFotoPerfil);
    }
}
