package com.dubium.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Subject;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    View mRootView;
    ImageView mIvFotoPerfil;
    FlexboxLayout mAptidoesContainer;
    FlexboxLayout mDificuldadesContainer;

    ChipCloud mChipsAptitudes;
    ChipCloud mChipCloud2;

    FirebaseDatabaseManager mFirebaseDatabaseManager;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;

    List<Subject> mAptitudes;
    List<Subject> mDifficulties;


    public ProfileFragment() {
        mFirebaseDatabaseManager = new FirebaseDatabaseManager();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        List<Subject> subjects = mFirebaseDatabaseManager.getSubjects(mRootView.getContext());
        mAptitudes = mFirebaseDatabaseManager.getUserDifficulties(mRootView.getContext(), mFirebaseUser.getUid(), subjects);


        ChipCloudConfig config = new ChipCloudConfig()
                .selectMode(ChipCloud.SelectMode.multi)
                .uncheckedChipColor(Color.parseColor("#E5E5E5"))
                .uncheckedTextColor(Color.parseColor("#666666"))
                .useInsetPadding(true);

        mChipsAptitudes = new ChipCloud(mRootView.getContext(), mAptidoesContainer, config);
        mChipsAptitudes.addChips(mAptitudes);


        mAptidoesContainer = (FlexboxLayout) mRootView.findViewById(R.id.aptidoes_container);
        mDificuldadesContainer = (FlexboxLayout) mRootView.findViewById(R.id.dificuldades_container);

        mIvFotoPerfil = (ImageView) mRootView.findViewById(R.id.iv_foto_perfil);




        mChipCloud2 = new ChipCloud(mRootView.getContext(), mDificuldadesContainer, config);
        mChipCloud2.addChip("Espanhol");
        mChipCloud2.addChip("Francês");
        mChipCloud2.addChip("Chinês");
        mChipCloud2.addChip("Cálculo");
        mChipCloud2.addChip("Metodologia");
        mChipCloud2.addChip("Tailandês");

        Glide.with(mIvFotoPerfil.getContext())
                .load("https://www.what-dog.net/Images/faces2/scroll001.jpg")
                .into(mIvFotoPerfil);

        return mRootView;
    }

}
