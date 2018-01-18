package com.dubium.fragments;

import android.os.Bundle;
import android.os.Handler;

import com.dubium.model.UserAddress;
import com.google.firebase.auth.FirebaseAuth;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
    User userActual;
    FirebaseAuth mFirebaseAuth;

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    boolean initialRefresh = true;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();

        currentUser = mFirebaseAuth.getCurrentUser();

        mUsersListView = (ListView) mRootView.findViewById(R.id.lv_usuarios);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.gray);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        listar();

        return mRootView;
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listar();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }

    public void listar(){
        final ArrayList<User> users = new ArrayList<>();
        users.clear();

        Query query = mDatabaseReference.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    user = objSnapshot.getValue(User.class);
                    if (user != getActualUser()) {
                        //pra testar se ta pegando a city
                        Toast.makeText(getActivity(), getAddressUser(user.getUid()),
                                Toast.LENGTH_SHORT).show();
                        users.add(user);
                    }
                }
                if (users.size() <= 0) {
                    Toast.makeText(getActivity(), "Não há nenhum usuario proximo",
                            Toast.LENGTH_SHORT).show();
                } else {

                    ArrayList<UserViewHolder> userListViewHolder;
                    userListViewHolder = filterUserList(users);

                    mUserAdapter = new UserAdapter(getActivity(), userListViewHolder );

                    mUsersListView.setAdapter(mUserAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public User getActualUser(){
        mDatabaseReference = mDatabaseReference.child("users").child(currentUser.getUid());
        mDatabaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userActual = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
        return userActual;
    }

    private ArrayList<UserViewHolder> filterUserList(ArrayList<User> users){

        ArrayList<UserViewHolder> l = new ArrayList<UserViewHolder>();

        User current;
        current = getActualUser();

        for(User u: users){
            UserViewHolder aux = new UserViewHolder();
            aux.setName(u.getName());

            if(u.getmUserAddress() != null) {
                aux.setDistancia(distance(current.getmUserAddress().getLatitude(), u.getmUserAddress().getLatitude(),
                        current.getmUserAddress().getLongitude(), u.getmUserAddress().getLongitude(),
                        0.0, 0.0));
            }else{
                aux.setDistancia(1000);
            }
            aux.setAptidoesComuns(1);
            aux.setDificuldadesComuns(1);
            l.add(aux);
        }

        return l;
    }

    String city;
    private String getAddressUser(String UId){

        Query query = mDatabaseReference.child("users").child(UId).child("userAddress");
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserAddress ua;

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    ua = objSnapshot.getValue(UserAddress.class);
                    city = ua.getCity();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();
            }
        });
        return city;
    }

    /*private ArrayList<String> apComuns(){
        final ArrayList<String> aptidoes = new ArrayList<>();

        Query query = mDatabaseReference.child("users");
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    String UId = objSnapshot.getValue(User.class).getUid();
                    Toast.makeText(getActivity(), "add " + UId , Toast.LENGTH_SHORT).show();

                    Query query2 = mDatabaseReference.child("users").child(UId).child("aptitudes");
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                                aptidoes.add(String.valueOf(ds.getValue()));

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "ERRO2", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();
            }
        });
        return aptidoes;

    }*/



    private double distance(double lat1, double lat2, double lon1, double lon2,
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
        return Math.sqrt(distance);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

}
