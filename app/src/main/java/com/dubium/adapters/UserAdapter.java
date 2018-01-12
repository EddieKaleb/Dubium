package com.dubium.adapters;

import android.content.Context;
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
import com.dubium.model.User;

/**
 * Created by eddie on 09/01/2018.
 */

public class UserAdapter extends ArrayAdapter<User> {

    RelativeLayout mUsuarioContainer;
    ImageView mIvFotoPerfil;
    TextView mTvNome;
    TextView mTvAptidoesComuns;
    TextView mTvDificuldadesComuns;
    TextView mTvDistancia;

    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User u = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_user, parent, false);

        mUsuarioContainer = (RelativeLayout) convertView.findViewById(R.id.usuario_container);
        mIvFotoPerfil = (ImageView) convertView.findViewById(R.id.iv_foto_perfil);
        mTvNome = (TextView) convertView.findViewById(R.id.tv_nome);
        mTvAptidoesComuns = (TextView) convertView.findViewById(R.id.tv_aptidoes_comum);
        mTvDificuldadesComuns = (TextView) convertView.findViewById(R.id.tv_dificuldades_comum);
        mTvDistancia = (TextView) convertView.findViewById(R.id.tv_distancia);

        mTvNome.setText(u.getNome().toUpperCase());
        mTvDistancia.setText(u.getDistancia());
        mTvAptidoesComuns.setText(u.getAptidoesComuns());
        mTvDificuldadesComuns.setText(u.getDificuldadesComuns());

        if (u.getFotoUrl() != null) {
            Glide.with(mIvFotoPerfil.getContext())
                    .load(u.getFotoUrl())
                    .into(mIvFotoPerfil);
        }

        mUsuarioContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre o perfil do usuário, por enquanto vai abrir o chat para adiantar essa parte
            }
        });

        return  convertView;
    }
}
