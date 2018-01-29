package com.dubium.views;

        import android.annotation.SuppressLint;
        import android.app.AlertDialog;
        import android.content.Context;
        import android.content.DialogInterface;
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
        import com.dubium.database.FirebaseDatabaseManager;
        import com.dubium.model.Subject;
        import com.dubium.model.User;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.List;

        import butterknife.BindView;
        import butterknife.ButterKnife;

public class LocationActivity extends AptitudesActivity {

    LocationManager locationManager;
    public FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    FirebaseDatabaseManager mDatabaseManager;
    ArrayList<Subject> mAptitudesList;
    ArrayList<Subject> mDifficultiesList;

    boolean prosseguir = false;

    @BindView(R.id.setup_divider)
    View mSetupDivider;

    public static final int REQUEST_LOCATION = 2;

    String towers = "";
    Location location = null;
    double latitude = 0;
    double longitude = 0;

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
        mTvSecond.setText("habilite sua localização!");
        mIvIcon.setImageResource(R.drawable.ic_localizacao);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        mDatabaseManager = new FirebaseDatabaseManager();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        mViewMensagem.getLayoutParams().height = height - 180;
        Log.w("Height", "" + height);
        mViewMensagem.requestLayout();

        mAptitudesList = (ArrayList<Subject>) getIntent().getExtras().get("Aptitudes");
        mDifficultiesList = (ArrayList<Subject>) getIntent().getExtras().get("Difficulties");

        mTvProsseguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                String photoUrl = "";

                if(fbUser.getPhotoUrl() != null){
                    photoUrl = fbUser.getPhotoUrl().toString();
                }

                User user = new User(fbUser.getUid(), fbUser.getDisplayName(), fbUser.getEmail(), photoUrl);

                mDatabaseManager.saveUser(user);

                for (Subject s : mAptitudesList) {
                    mDatabaseManager.addAptitudeToUser(fbUser.getUid(), s);
                }

                for (Subject s : mDifficultiesList) {
                    mDatabaseManager.addDifficultieToUser(fbUser.getUid(), s);
                }

                find_location();
            }
        });
    }
    public void find_location(){

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();

        }
        else{
            User user = new User();

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){

                Log.w("checkSelfPermission", "log");
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Criteria crit = new Criteria();
                towers = locationManager.getBestProvider(crit, false);
                location = getLastKnownLocation();
            }

            if (location != null) {
                prosseguir = true;
                Toast.makeText(this, "Localização atualizada", Toast.LENGTH_SHORT).show();

                longitude = location.getLongitude();
                latitude = location.getLatitude();

                try {
                    user.findAdress(latitude, longitude);
                    mDatabase.child("users").child(currentUser.getUid()).child("city").setValue(user.getCity());
                    mDatabase.child("users").child(currentUser.getUid()).child("state").setValue(user.getState());
                    mDatabase.child("users").child(currentUser.getUid()).child("latitude").setValue(user.getLatitude());
                    mDatabase.child("users").child(currentUser.getUid()).child("longitude").setValue(user.getLongitude());

                } catch (IOException e) { e.printStackTrace(); }
            }

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.w("onRequestPermissions", "OK");
                // We can now safely use the API we requested access to
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Criteria crit = new Criteria();
                towers = locationManager.getBestProvider(crit, false);
                location = getLastKnownLocation();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Ative sua localização", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
