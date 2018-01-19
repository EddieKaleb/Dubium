package com.dubium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dubium.R;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    View mRootView;
    ImageView mIvFotoPerfil;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mIvFotoPerfil = (ImageView) mRootView.findViewById(R.id.iv_foto_perfil);

        Glide.with(mIvFotoPerfil.getContext())
                .load("https://www.what-dog.net/Images/faces2/scroll001.jpg")
                .into(mIvFotoPerfil);


        return mRootView;
    }

}
