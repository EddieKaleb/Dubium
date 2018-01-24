package com.dubium.fragments;

import android.os.Bundle;
import android.os.Handler;

import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Subject;
import com.dubium.views.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;

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
    final ArrayList<User> users = new ArrayList<>();

    FirebaseUser currentUser;
    User userActual;
    FirebaseAuth mFirebaseAuth;

    FirebaseDatabaseManager fbManager;
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    boolean initialRefresh = true;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        fbManager = new FirebaseDatabaseManager();

        mUsersListView = (ListView) mRootView.findViewById(R.id.lv_usuarios);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.gray);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        listFilter2();

        return mRootView;
    }


    public void listFilter2(){
        Query query = mDatabaseReference.child("users").child(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userActual = dataSnapshot.getValue(User.class);
                listar();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void listar(){

        Query query = mDatabaseReference.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    user = objSnapshot.getValue(User.class);
                    if(!(userActual == null)) {
                        if(!(userActual.getLatitude() == 0)){
                            if(!(user.getLatitude() == 0))
                                if (user.getCity().equals(userActual.getCity()) && user.getUid() != userActual.getUid())
                                    if((distance(userActual.getLatitude(), user.getLatitude(),
                                            userActual.getLongitude(), user.getLongitude(),
                                            0.0, 0.0)) < 40)
                                        users.add(user);
                        }
                    }
                }
                if (users.size() > 0) {
                    ArrayList<UserViewHolder> userListViewHolder;
                    userListViewHolder = filterUserList(users);

                    mUserAdapter = new UserAdapter(getActivity(), userListViewHolder);
                    mUsersListView.setAdapter(mUserAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();
            }
        });

        //MODO MELHOR QUE ERA PRA FUNCIONAR

        /*final ArrayList<User> users = new ArrayList<>();
        Query query = mDatabaseReference.child("cities").child(userActual.getCity());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot obj: dataSnapshot.getChildren()) {
                    String Uid = obj.getKey();

                    Query query2 = mDatabaseReference.child("users").child(Uid);
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            if((distance(userActual.getLatitude(), user.getLatitude(),
                                    userActual.getLongitude(), user.getLongitude(),
                                    0.0, 0.0)) < 40)
                                users.add(user);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                if (users.size() > 0) {
                    ArrayList<UserViewHolder> userListViewHolder;
                    userListViewHolder = filterUserList(users);

                    mUserAdapter = new UserAdapter(getActivity(), userListViewHolder);
                    mUsersListView.setAdapter(mUserAdapter);
                }else{
                    Toast.makeText(getActivity(),"Sem usuarios para listar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });*/
    }

    private ArrayList<UserViewHolder> filterUserList(ArrayList<User> users){
        ArrayList<UserViewHolder> l = new ArrayList<UserViewHolder>();

        for(User u: users){
            UserViewHolder aux = new UserViewHolder();
            aux.setUid(u.getUid());
            aux.setName(u.getName());

            DecimalFormat df = new DecimalFormat("0.#");
            aux.setDistancia(df.format(distance(userActual.getLatitude(), u.getLatitude(),
                    userActual.getLongitude(), u.getLongitude(),
                    0.0, 0.0)));

            l.add(aux);
        }
        return l;
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listFilter2();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }
    /* CALCULA A DISTANCIA COM BASE NAS CORDENADAS*/
    private Double distance(double lat1, double lat2, double lon1, double lon2,
                            double el1, double el2) {

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

        return (Math.sqrt(distance)/1000);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

}
