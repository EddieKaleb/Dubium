package com.dubium.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.views.AptitudesActivity;
import com.dubium.views.DifficultiesActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    View mRootView;

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

    final int NEW_DATA = 1;
    boolean editMode = false;

    public ProfileFragment() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
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

        mIvConfirmar.setVisibility(View.GONE);
        mIvEditarFoto.setVisibility(View.GONE);

        Log.w("init", "Components");

        if (editMode) initEditMode();
    }

    private void initEditMode() {
        mIvConfirmar.setVisibility(View.VISIBLE);
        mIvEditarFoto.setVisibility(View.VISIBLE);
        mIvEditar.setVisibility(View.GONE);
        mIvIconeAptidoes.setImageResource(R.drawable.ic_editar);
        mIvIconeDificuldades.setImageResource(R.drawable.ic_editar);
    }

    private void exitEditMode() {
        mIvConfirmar.setVisibility(View.GONE);
        mIvEditarFoto.setVisibility(View.GONE);
        mIvEditar.setVisibility(View.VISIBLE);
        mIvIconeAptidoes.setImageResource(R.drawable.ic_cerebro);
        mIvIconeDificuldades.setImageResource(R.drawable.ic_duvida);
    }

    private void setListeners() {
        mIvEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode = true;
               initEditMode();
            }
        });

        mIvConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode = false;
                exitEditMode();
            }
        });

        mContainerAptidoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    Intent intent = new Intent(getActivity(), AptitudesActivity.class);
                    intent.putExtra("calling-activity", "AptitudesActivity");
                    startActivityForResult(intent, NEW_DATA);
                }
            }
        });

        mContainerDificuldades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    Intent intent = new Intent(getActivity(), DifficultiesActivity.class);
                    intent.putExtra("calling-activity", "DifficultiesActivity");
                    startActivityForResult(intent, NEW_DATA);
                }
            }
        });

        mIvEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir a galeria
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (NEW_DATA) : {
                Log.w("Result", "New Data");
                if (resultCode == Activity.RESULT_OK) {
                    getFragmentManager().beginTransaction().detach(this).attach(this).commit();
                }
                editMode = true;
                break;
            }
        }
    }
}