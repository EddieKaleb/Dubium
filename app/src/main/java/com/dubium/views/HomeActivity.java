package com.dubium.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.fragments.ChatsFragment;
import com.dubium.fragments.HomeFragment;
import com.dubium.fragments.ProfileFragment;
import com.dubium.model.UserAdress;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.bottom_navigation) BottomNavigationView mBottomNavigationView;
    @BindView(R.id.home_container) RelativeLayout mHomeContainer;

    // Fragments
    HomeFragment mHomeFragment;
    ChatsFragment mChatsFragment;
    ProfileFragment mProfileFragment;

    LocationManager locationManager;
    private DatabaseReference mDatabase;



    final FragmentManager fragmentManager = getSupportFragmentManager();
    int mMenuPrevItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        mHomeFragment = new HomeFragment();
        mChatsFragment = new ChatsFragment();
        mProfileFragment = new ProfileFragment();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TransitionManager.beginDelayedTransition(mHomeContainer, new Fade());
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, mHomeFragment).commit();

        mMenuPrevItem = R.id.action_home;

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        int id = item.getItemId();
                        if (id != mMenuPrevItem) {
                            mMenuPrevItem = id;
                            TransitionManager.beginDelayedTransition(mHomeContainer, new Fade());
                            switch (id) {
                                case R.id.action_home:
                                    fragmentTransaction.replace(R.id.fragment, mHomeFragment).commit();
                                    return true;
                                case R.id.action_chat:
                                    fragmentTransaction.replace(R.id.fragment, mChatsFragment).commit();
                                    return true;
                                case R.id.action_person:
                                    fragmentTransaction.replace(R.id.fragment, mProfileFragment).commit();
                                    return true;
                            }
                        }
                        return false;
                    }
                });

        alertaValidacaoPermissao();

        // Inicializa o AuthState para verificação de usuário logado.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_location:
                return true;
        }
        return false;
    }

    private void alertaValidacaoPermissao() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão");
        builder.setMessage("Para usar o app você precisa aceitar as permissões ");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Alguma ação
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void find_location(){

        double latitude ;
        double longitude;
        String towers = "";
        Location location = null;
        UserAdress userAdress = new UserAdress();


        FirebaseUser user;
        user = mFirebaseAuth.getCurrentUser();

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

            //Toast.makeText(this, "SEM PERMISSÃO", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "TUDO TOP", Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);

            Criteria crit = new Criteria();
            towers = locationManager.getBestProvider(crit, false);
            location = getLastKnownLocation();
        }

        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            try {
                userAdress.findAdress(latitude, longitude);
                mDatabase.child("users").child(user.getUid()).child("userAddress").setValue(userAdress);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(this, "Location is null! " + towers, Toast.LENGTH_SHORT).show();

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
