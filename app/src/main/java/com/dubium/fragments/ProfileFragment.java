package com.dubium.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.dubium.model.Message;
import com.dubium.views.AptitudesActivity;
import com.dubium.views.DifficultiesActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;

import static android.app.Activity.RESULT_OK;

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
    ImageView mIvRemoverFoto;
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
    FirebaseStorage mFirebaseStorage;
    StorageReference mProfilePhotosStorageReference;

    ProgressDialog mProgressDialog;

    static final int NEW_DATA = 1;
    static final int RC_PHOTO_PIKER = 2;
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

        mTvNomePerfil = (TextView) mRootView.findViewById(R.id.tv_nome_perfil);
        mTvCidadePerfil = (TextView) mRootView.findViewById(R.id.tv_cidade_perfil);
        mTvEstadoPerfil = (TextView) mRootView.findViewById(R.id.tv_estado_perfil);

        mContainerAptidoes = (RelativeLayout) mRootView.findViewById(R.id.titulo_aptidoes);
        mContainerDificuldades = (RelativeLayout) mRootView.findViewById(R.id.titulo_dificuldades);

        mIvFotoPerfil = (ImageView) mRootView.findViewById(R.id.iv_foto_perfil);
        mIvEditar = (ImageView) mRootView.findViewById(R.id.iv_editar);
        mIvConfirmar = (ImageView) mRootView.findViewById(R.id.iv_confirmar);
        mIvEditarFoto = (ImageView) mRootView.findViewById(R.id.iv_editar_foto);
        mIvRemoverFoto = (ImageView) mRootView.findViewById(R.id.iv_apagar_foto);
        mIvIconeAptidoes = (ImageView) mRootView.findViewById(R.id.ic_cerebro);
        mIvIconeDificuldades = (ImageView) mRootView.findViewById(R.id.ic_duvida);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Carregando...");
        mProgressDialog.setTitle("Nova Foto");

        mFirebaseDatabaseManager = new FirebaseDatabaseManager();
        mChipsAptitudes = new ChipCloud(mRootView.getContext(), mAptidoesContainer, config);
        mChipsDifficulties = new ChipCloud(mRootView.getContext(), mDificuldadesContainer, config);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePhotosStorageReference = mFirebaseStorage.getReference().child("profile_photos");

        exitEditMode();

        if (editMode) initEditMode();
    }

    private void initEditMode() {
        mIvConfirmar.setVisibility(View.VISIBLE);
        mIvEditarFoto.setVisibility(View.VISIBLE);
        mIvRemoverFoto.setVisibility(View.VISIBLE);
        mIvEditar.setVisibility(View.GONE);
        mIvIconeAptidoes.setImageResource(R.drawable.ic_editar);
        mIvIconeDificuldades.setImageResource(R.drawable.ic_editar);
    }

    private void exitEditMode() {
        mIvConfirmar.setVisibility(View.GONE);
        mIvEditarFoto.setVisibility(View.GONE);
        mIvRemoverFoto.setVisibility(View.GONE);
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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PIKER);
            }
        });

        mIvRemoverFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseDatabaseManager.saveProfilePhoto(mFirebaseUser.getUid(), "");
                mIvFotoPerfil.setImageResource(R.drawable.ic_anonymous);
            }
        });
    }

    private void refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void loadData() {
        mTvNomePerfil.setText(mFirebaseUser.getDisplayName().toUpperCase());
        mFirebaseDatabaseManager.setUserAptitudes(mFirebaseUser.getUid(), mChipsAptitudes);
        mFirebaseDatabaseManager.setUserDifficulties(mFirebaseUser.getUid(), mChipsDifficulties);
        mFirebaseDatabaseManager.setProfilePhoto(mFirebaseUser.getUid(), mIvFotoPerfil);
    }

    private void loadPhoto(Uri uri) {
        StorageReference photoRef = mProfilePhotosStorageReference.child(uri.getLastPathSegment());
        photoRef.putFile(uri)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Glide.with(mIvFotoPerfil.getContext()).load(downloadUrl).into(mIvFotoPerfil);
                        mFirebaseDatabaseManager.saveProfilePhoto(mFirebaseUser.getUid(), downloadUrl.toString());
                    }
                }).addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressDialog.show();
            }
        }).addOnCompleteListener(getActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                mProgressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NEW_DATA:
                    refresh();
                    editMode = true;
                    break;
                case RC_PHOTO_PIKER:
                    Uri selectedImageUri = data.getData();
                    loadPhoto(selectedImageUri);
                    refresh();
                    break;
            }
        }
    }

}