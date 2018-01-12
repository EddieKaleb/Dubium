package com.dubium.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dubium.R;
import butterknife.ButterKnife;

public class DifficultiesActivity extends AptitudesActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mTvProsseguir.setText("FINALIZAR");
        mTvFirst.setText("Para finalizar");
        mTvSecond.setText("Adicione suas dificuldades!");
        mIvIcon.setImageResource(R.drawable.ic_help);

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
