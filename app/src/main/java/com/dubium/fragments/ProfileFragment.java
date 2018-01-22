package com.dubium.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Subject;
import com.dubium.views.AptitudesActivity;
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

    RelativeLayout mContainerPerfil;
    RelativeLayout mContainerAptidoes;
    RelativeLayout mContainerDificuldades;

    ImageView mIvFotoPerfil;
    ImageView mIvEditar;
    ImageView mIvEditarFoto;
    ImageView mIvConfirmar;
    ImageView mIvIconeAptidoes;
    ImageView mIvIconeDificuldades;

    TextView mTvNomePerfil;
    TextView mTvCidadePerfil;
    TextView mTvEstadoPerfil;


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
        setListeners();
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
        mTvNomePerfil = (TextView) mRootView.findViewById(R.id.tv_nome_perfil);
        mTvCidadePerfil = (TextView) mRootView.findViewById(R.id.tv_cidade_perfil);
        mTvEstadoPerfil = (TextView) mRootView.findViewById(R.id.tv_estado_perfil);
        mContainerAptidoes = (RelativeLayout) mRootView.findViewById(R.id.titulo_aptidoes);
        mContainerDificuldades = (RelativeLayout) mRootView.findViewById(R.id.titulo_dificuldades);
        mIvEditar = (ImageView) mRootView.findViewById(R.id.iv_editar);
        mIvConfirmar = (ImageView) mRootView.findViewById(R.id.iv_confirmar);
        mIvEditarFoto = (ImageView) mRootView.findViewById(R.id.iv_editar_foto);
        mIvIconeAptidoes = (ImageView) mRootView.findViewById(R.id.ic_cerebro);
        mIvIconeDificuldades = (ImageView) mRootView.findViewById(R.id.ic_duvida);

        mFirebaseDatabaseManager = new FirebaseDatabaseManager();
        mChipsAptitudes = new ChipCloud(mRootView.getContext(), mAptidoesContainer, config);
        mChipsDifficulties = new ChipCloud(mRootView.getContext(), mDificuldadesContainer, config);

        mContainerAptidoes.setClickable(false);
        mContainerDificuldades.setClickable(false);
        mIvConfirmar.setVisibility(View.GONE);
        mIvEditarFoto.setVisibility(View.GONE);
    }


    private void setListeners() {
        mIvEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContainerAptidoes.setClickable(true);
                mContainerDificuldades.setClickable(true);
                mIvConfirmar.setVisibility(View.VISIBLE);
                mIvEditarFoto.setVisibility(View.VISIBLE);
                mIvEditar.setVisibility(View.GONE);
                mIvIconeAptidoes.setImageResource(R.drawable.ic_editar);
                mIvIconeDificuldades.setImageResource(R.drawable.ic_editar);
            }
        });

        mIvConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContainerAptidoes.setClickable(false);
                mContainerDificuldades.setClickable(false);
                mIvConfirmar.setVisibility(View.GONE);
                mIvEditarFoto.setVisibility(View.GONE);
                mIvEditar.setVisibility(View.VISIBLE);
                mIvIconeAptidoes.setImageResource(R.drawable.ic_cerebro);
                mIvIconeDificuldades.setImageResource(R.drawable.ic_duvida);
            }
        });

        mContainerAptidoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AptitudesActivity.class);
                intent.putExtra("calling-activity", "ProfileFragment");
                startActivity(intent);
            }
        });
    }


    private void loadData() {
        mTvNomePerfil.setText(mFirebaseUser.getDisplayName().toUpperCase());
        mFirebaseDatabaseManager.setUserAptitudes(mFirebaseUser.getUid(), mChipsAptitudes);
        mFirebaseDatabaseManager.setUserDifficulties(mFirebaseUser.getUid(), mChipsDifficulties);

        if (mFirebaseUser.getPhotoUrl() != null) {
            Glide.with(mIvFotoPerfil.getContext())
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mIvFotoPerfil);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

