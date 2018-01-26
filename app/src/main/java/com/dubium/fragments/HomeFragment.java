package com.dubium.fragments;

import android.os.Bundle;
import android.os.Handler;

import com.dubium.database.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.dubium.R;
import com.dubium.adapters.UserAdapter;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View mRootView;

    ListView mUsersListView;

    UserAdapter mUserAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    FirebaseUser currentUser;
    User mUserActual;
    FirebaseAuth mFirebaseAuth;

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    final ArrayList<UserViewHolder> userListViewHolder = new ArrayList<>();

    boolean initialRefresh = true;

    private final int RATIO = 40;

    public HomeFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        init();
        setListeners();
        loadData();
    }

    private void init() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();

        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.gray);

        mUsersListView = (ListView) mRootView.findViewById(R.id.lv_usuarios);
        mUserAdapter = new UserAdapter(getActivity(), userListViewHolder);
        mUsersListView.setAdapter(mUserAdapter);
    }

    private void loadData() {
        if (initialRefresh) {
            getUserAndNearbies();
            initialRefresh = false;
        }
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    public void getUserAndNearbies() {
        Query query = mDatabaseReference.child("users").child(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserActual = dataSnapshot.getValue(User.class);
                getNearbyUsers();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void getNearbyUsers() {

        Query query = mDatabaseReference.child("users");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    User nearbyUser = objSnapshot.getValue(User.class);

                    if (mUserActual != null && !currentUser.getUid().equals(nearbyUser.getUid())) {
                        if (mUserActual.getLatitude() != 0 && nearbyUser.getLatitude() != 0) {
                            if (nearbyUser.getCity().equals(mUserActual.getCity()) && nearbyUser.getUid() != mUserActual.getUid()) {

                                double actualLat  = mUserActual.getLatitude();
                                double actualLong = mUserActual.getLongitude();
                                double nearbyLat  = nearbyUser.getLatitude();
                                double nearbyLong = nearbyUser.getLongitude();

                                double distance = getKmsDistance(actualLat, nearbyLat, actualLong, nearbyLong,0.0,0.0);

                                if (distance < RATIO) addUserViewHolder(nearbyUser, distance);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addUserViewHolder(User user, double distance) {
        UserViewHolder userViewHolder = new UserViewHolder();

        userViewHolder.setUid(user.getUid());
        userViewHolder.setName(user.getName());
        userViewHolder.setPhotoUrl(user.getPhotoUrl());
        userViewHolder.setCity(user.getCity());
        userViewHolder.setState(user.getState());

        DecimalFormat df = new DecimalFormat("0.#");
        userViewHolder.setDistancia(df.format(distance));

        mUserAdapter.add(userViewHolder);
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private  void refresh() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    /* CALCULA A DISTANCIA COM BASE NAS CORDENADAS*/
    private Double getKmsDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = deg2rad(lat2 - lat1);
        Double lonDistance = deg2rad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return (Math.sqrt(distance) / 1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
