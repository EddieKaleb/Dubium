package com.dubium.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.dubium.fragments.UserViewHolder;
import com.dubium.model.User;
import com.dubium.views.ChatActivity;

/**
 * Created by eddie on 09/01/2018.
 */

public class UserAdapter extends ArrayAdapter<UserViewHolder> {

    RelativeLayout mUsuarioContainer;
    ImageView mIvFotoPerfil;
    TextView mTvNome;
    TextView mTvAptidoesComuns;
    TextView mTvDificuldadesComuns;
    TextView mTvDistancia;
    Context mContext;

    public UserAdapter(Context context, ArrayList<UserViewHolder> users) {
        super(context, 0, users);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserViewHolder u = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_user, parent, false);

        mUsuarioContainer = (RelativeLayout) convertView.findViewById(R.id.usuario_container);
        mIvFotoPerfil = (ImageView) convertView.findViewById(R.id.iv_foto_perfil);
        mTvNome = (TextView) convertView.findViewById(R.id.tv_nome);
        mTvAptidoesComuns = (TextView) convertView.findViewById(R.id.tv_aptidoes_comum);
        mTvDificuldadesComuns = (TextView) convertView.findViewById(R.id.tv_dificuldades_comum);
        mTvDistancia = (TextView) convertView.findViewById(R.id.tv_distancia);

        mTvNome.setText(u.getName());
        mTvDistancia.setText(u.getDistancia());
        mTvAptidoesComuns.setText(u.getAptidoesComuns());
        mTvDificuldadesComuns.setText(u.getDificuldadesComuns());

        if (u.getPhotoUrl() != null) {
            Glide.with(mIvFotoPerfil.getContext())
                    .load(u.getPhotoUrl())
                    .into(mIvFotoPerfil);
        }

        final String friendId = u.getuId();
        mUsuarioContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("friendId", friendId);
                intent.putExtras(mBundle);

                mContext.startActivity(intent);
            }
        });

        return  convertView;
    }
}
