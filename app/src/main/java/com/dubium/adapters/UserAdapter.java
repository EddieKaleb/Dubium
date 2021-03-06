package com.dubium.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.dubium.R;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.fragments.UserViewHolder;
import com.dubium.model.User;
import com.dubium.views.ChatActivity;
import com.dubium.views.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by eddie on 09/01/2018.
 */

public class UserAdapter extends ArrayAdapter<UserViewHolder> {

    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();;
    FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();;

    FirebaseDatabaseManager fbManager = new FirebaseDatabaseManager();
    RelativeLayout mUsuarioContainer;
    ImageView mIvFotoPerfil;
    TextView mTvNome;
    TextView mTvAptidaoComum;
    TextView mTvAptidoesComum;
    TextView mTvDificuldadeComum;
    TextView mTvDificuldadesComum;
    TextView mTvDistancia;
    Context mContext;

    public UserAdapter(Context context, ArrayList<UserViewHolder> users) {
        super(context, 0, users);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserViewHolder u = getItem(position);

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_user, parent, false);

        mUsuarioContainer = (RelativeLayout) convertView.findViewById(R.id.usuario_container);
        mIvFotoPerfil = (ImageView) convertView.findViewById(R.id.iv_foto_perfil);
        mTvNome = (TextView) convertView.findViewById(R.id.tv_nome);
        mTvAptidoesComum = (TextView) convertView.findViewById(R.id.tv_aptidoes_comum);
        mTvAptidaoComum = (TextView) convertView.findViewById(R.id.tv_aptidao_comum);
        mTvDificuldadeComum = (TextView) convertView.findViewById(R.id.tv_dificuldade_comum);
        mTvDificuldadesComum = (TextView) convertView.findViewById(R.id.tv_dificuldades_comum);
        mTvDistancia = (TextView) convertView.findViewById(R.id.tv_distancia);

        if(u.getName().length() > 30){
            String[] nome = u.getName().split(" ");
            mTvNome.setText(nome[0] + " " + nome[1]);
        } else{ mTvNome.setText(u.getName().toUpperCase()); }

        mTvDistancia.setText(u.getDistancia());

        fbManager.getUserSubjects(u.getuId(), "aptitudes",mTvAptidaoComum, mTvAptidoesComum);
        fbManager.getUserSubjects(u.getuId(), "difficulties",mTvDificuldadeComum, mTvDificuldadesComum);

        String photoUrl = null;

        if(u.getPhotoUrl() == null){

        }
        else if(u.getPhotoUrl().equals("")){
            Log.i("FriendPhotoUrl", u.getPhotoUrl()+"");

            photoUrl = null;
        }
        else{
            photoUrl = u.getPhotoUrl();
            Log.i("FriendPhotoUrl", u.getPhotoUrl()+"");

            Glide.with(mIvFotoPerfil.getContext())
                    .load(u.getPhotoUrl())
                    .into(mIvFotoPerfil);
        }


        mUsuarioContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("uid", u.getuId());
                mBundle.putString("name", u.getName());
                mBundle.putString("city", u.getCity());
                mBundle.putString("state", u.getState());
                mBundle.putString("photoUrl", u.getPhotoUrl());

                intent.putExtras(mBundle);
                mContext.startActivity(intent);
            }
        });

        return  convertView;
    }
}
