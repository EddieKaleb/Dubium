package com.dubium.views;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.Toast;

        import com.dubium.R;
        import com.dubium.model.User;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.io.IOException;
        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;

public class LocationActivity extends AptitudesActivity {

    LocationManager locationManager;
    public FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();


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
                find_location();
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void find_location(){
        double latitude ;
        double longitude;
        String towers = "";
        Location location = null;
        User user = new User();

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

        }else{
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            Criteria crit = new Criteria();
            towers = locationManager.getBestProvider(crit, false);
            location = getLastKnownLocation();
        }

        if (location != null) {
            Toast.makeText(this, "LOCALIZAÇÃO ATUALIZADA", Toast.LENGTH_SHORT).show();

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            try {
                user.findAdress(latitude, longitude);
                mDatabase.child("users").child(currentUser.getUid()).child("city").setValue(user.getCity());
                mDatabase.child("users").child(currentUser.getUid()).child("state").setValue(user.getState());
                mDatabase.child("users").child(currentUser.getUid()).child("latitude").setValue(user.getLatitude());
                mDatabase.child("users").child(currentUser.getUid()).child("longitude").setValue(user.getLongitude());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "religue seu gps" + towers, Toast.LENGTH_SHORT).show();
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        Location l = null;
        for (String provider : providers) {
            try {
                l = locationManager.getLastKnownLocation(provider);
            } catch (SecurityException e) {
            }

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                Log.d("found best location: %s", String.valueOf(1));
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }
}
