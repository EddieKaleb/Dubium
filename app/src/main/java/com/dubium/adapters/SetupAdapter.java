package com.dubium.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import com.dubium.model.Subject;
import com.dubium.views.AptitudesActivity;
import com.dubium.R;

/**
 * Created by eddie on 06/01/2018.
 */

public class SetupAdapter extends ArrayAdapter<Subject> {

    AptitudesActivity mAptitudesActivity;
    TextView mTvDisciplina;
    ImageButton mIbRemoveDisciplina;

    public SetupAdapter(Context context, ArrayList<Subject> disciplinas) {
        super(context, 0, disciplinas);
        if ((Activity) context instanceof AptitudesActivity)
            this.mAptitudesActivity = (AptitudesActivity) context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Subject disciplina = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_setup, parent, false);

        mTvDisciplina = (TextView) convertView.findViewById(R.id.tv_disciplina);
        mIbRemoveDisciplina = (ImageButton) convertView.findViewById(R.id.ib_remove_disciplina);

        mTvDisciplina.setText(disciplina.getName());
        mIbRemoveDisciplina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(disciplina);
                if (mAptitudesActivity != null) {
                    mAptitudesActivity.mDisciplinas.add(disciplina);
                    if (mAptitudesActivity.mMinhasDisciplinas.size() == 0) {
                        mAptitudesActivity.mTvProsseguir.setVisibility(View.GONE);
                        mAptitudesActivity.mViewMensagem.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        return convertView;
    }
}
