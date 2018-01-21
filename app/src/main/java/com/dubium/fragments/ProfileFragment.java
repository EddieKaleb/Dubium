package com.dubium.fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Subject;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    View mRootView;
    ImageView mIvFotoPerfil;
    FlexboxLayout mAptidoesContainer;
    FlexboxLayout mDificuldadesContainer;

    ChipCloudConfig config;
    ChipCloud mChipsAptitudes;
    ChipCloud mChipsDifficulties;

    FirebaseDatabaseManager mFirebaseDatabaseManager;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;


    public ProfileFragment() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRootView = view;
        init();
        loadData();
    }

    private void init() {
        config = new ChipCloudConfig().selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(Color.parseColor("#E5E5E5")).checkedTextColor(Color.parseColor("#666666"))
                .uncheckedChipColor(Color.parseColor("#E5E5E5")).uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true);

        mAptidoesContainer = (FlexboxLayout) mRootView.findViewById(R.id.aptidoes_container);
        mDificuldadesContainer = (FlexboxLayout) mRootView.findViewById(R.id.dificuldades_container);
        mIvFotoPerfil = (ImageView) mRootView.findViewById(R.id.iv_foto_perfil);

        mFirebaseDatabaseManager = new FirebaseDatabaseManager();
        mChipsAptitudes = new ChipCloud(mRootView.getContext(), mAptidoesContainer, config);
        mChipsDifficulties = new ChipCloud(mRootView.getContext(), mDificuldadesContainer, config);
    }

    private void loadData() {
        mFirebaseDatabaseManager.setUserAptitudes(mFirebaseUser.getUid(), mChipsAptitudes);
        mFirebaseDatabaseManager.setUserDifficulties(mFirebaseUser.getUid(), mChipsDifficulties);
        Glide.with(mIvFotoPerfil.getContext())
                .load("https://www.what-dog.net/Images/faces2/scroll001.jpg")
                .into(mIvFotoPerfil);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

