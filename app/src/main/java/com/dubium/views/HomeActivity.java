package com.dubium.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.ChangeClipBounds;
import android.support.transition.ChangeScroll;
import android.support.transition.Explode;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.fragments.ChatsFragment;
import com.dubium.fragments.HomeFragment;
import com.dubium.fragments.ProfileFragment;
import com.dubium.model.User;
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
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    int mMenuPrevItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        currentUser = mFirebaseAuth.getCurrentUser();

        mHomeFragment = new HomeFragment();
        mChatsFragment = new ChatsFragment();
        mProfileFragment = new ProfileFragment();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mToolbar.setNavigationIcon(R.drawable.ic_location);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                                    mToolbar.setVisibility(View.VISIBLE);
                                    fragmentTransaction.replace(R.id.fragment, mHomeFragment).commit();
                                    return true;
                                case R.id.action_chat:
                                    mToolbar.setVisibility(View.VISIBLE);
                                    fragmentTransaction.replace(R.id.fragment, mChatsFragment).commit();
                                    return true;
                                case R.id.action_person:
                                    mToolbar.setVisibility(View.GONE);
                                    TransitionManager.beginDelayedTransition(mHomeContainer, new Explode());
                                    fragmentTransaction.replace(R.id.fragment, mProfileFragment).commit();
                                    return true;
                            }
                        }
                        return false;
                    }
                });

        //alertaValidacaoPermissao();

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
            case android.R.id.home:
                find_location();
                return true;
        }
        return false;
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
            Toast.makeText(this, "Localização atualizada", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(this, "Ative sua localização", Toast.LENGTH_SHORT).show();
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
