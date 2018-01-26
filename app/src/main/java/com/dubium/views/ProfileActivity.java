package com.dubium.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
    ImageView mIvVoltar;

    FlexboxLayout mAptidoesContainer;
    FlexboxLayout mDificuldadesContainer;

    TextView mTvNomePerfil;
    TextView mTvCidadePerfil;
    TextView mTvEstadoPerfil;

    ChipCloudConfig config;
    ChipCloud mChipsAptitudes;
    ChipCloud mChipsDifficulties;

    FirebaseDatabaseManager mFirebaseDatabaseManager;
    FirebaseStorage mFirebaseStorage;
    StorageReference mProfilePhotosStorageReference;

    String uid;
    String name;
    String city;
    String state;
    String photoUrl;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        loadData();
        setListeners();
    }

    private void init() {

        config = new ChipCloudConfig().selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#E5E5E5")).checkedTextColor(Color.parseColor("#666666"))
                .uncheckedChipColor(Color.parseColor("#E5E5E5")).uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true);

        mTvNomePerfil = (TextView) findViewById(R.id.tv_nome_perfil);
        mTvCidadePerfil = (TextView) findViewById(R.id.tv_cidade_perfil);
        mTvEstadoPerfil = (TextView) findViewById(R.id.tv_estado_perfil);

        mBtnChat = (RelativeLayout) findViewById(R.id.btn_chat);
        mIvFotoPerfil = (ImageView) findViewById(R.id.iv_foto_perfil);
        mIvVoltar = (ImageView) findViewById(R.id.iv_voltar);

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
        uid = getIntent().getExtras().getString("uid");
        name = getIntent().getExtras().getString("name");
        city = getIntent().getExtras().getString("city");
        state = getIntent().getExtras().getString("state");
        photoUrl = getIntent().getExtras().getString("photoUrl");

        mTvNomePerfil.setText(name.toUpperCase());
        mTvCidadePerfil.setText(city);
        mTvEstadoPerfil.setText(state);

        mFirebaseDatabaseManager.setUserAptitudes(uid, mChipsAptitudes);
        mFirebaseDatabaseManager.setUserDifficulties(uid, mChipsDifficulties);
        mFirebaseDatabaseManager.setProfilePhoto(uid, mIvFotoPerfil);
    }

    private void setListeners() {
        mBtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("friendUid", uid);
                mBundle.putString("friendPhotoUrl", photoUrl);
                mBundle.putString("friendName", name);

                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        mIvVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
