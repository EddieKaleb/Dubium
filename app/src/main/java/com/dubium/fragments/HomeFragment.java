package com.dubium.fragments;

import android.os.Bundle;
import android.os.Handler;

import com.dubium.model.UserAdress;
import com.google.firebase.auth.FirebaseAuth;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.dubium.R;
import com.dubium.adapters.UserAdapter;
import com.dubium.model.User;
import com.dubium.views.HomeActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
                    //USERADRESS ESTA VINDO NULO
                    if (user != getActualUser() && (user.getmUserAdress() != null))
                        users.add(user);
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

            if(u.getmUserAdress() != null) {
                aux.setDistancia(distance(current.getmUserAdress().getLatitude(), u.getmUserAdress().getLatitude(),
                        current.getmUserAdress().getLongitude(), u.getmUserAdress().getLongitude(),
                        0.0, 0.0));
            }else{
                aux.setDistancia(1000);
            }
            aux.setDistancia(1000);
            aux.setAptidoesComuns(1);
            aux.setDificuldadesComuns(1);
            l.add(aux);
        }

        return l;
    }

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
