package com.dubium.views;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import com.dubium.R;

        import butterknife.BindView;
        import butterknife.ButterKnife;

public class LocationActivity extends AptitudesActivity {

    @BindView(R.id.setup_divider)
    View mSetupDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSetupDivider.setVisibility(View.GONE);
        mSvDisciplinas.setVisibility(View.GONE);
        mTvProsseguir.setVisibility(View.VISIBLE);
        mTvProsseguir.setText("PERMITIR");
        mTvProsseguir.setGravity(Gravity.END);
        mTvFirst.setText("Para finalizar");
        mTvSecond.setText("Precisamos acessar sua localização!");
        mIvIcon.setImageResource(R.drawable.ic_localizacao);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mViewMensagem.getLayoutParams().height = height - 180;
        Log.w("Height", "" + height);
        mViewMensagem.requestLayout();

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
