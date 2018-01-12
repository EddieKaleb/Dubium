package com.dubium.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.fragments.ChatsFragment;
import com.dubium.fragments.HomeFragment;
import com.dubium.fragments.ProfileFragment;

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
                finish();
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

}
