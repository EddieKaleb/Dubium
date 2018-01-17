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

    FirebaseAuth mFirebaseAuth;
    FirebaseUser currentUser;
    User userActual;
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");

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
        if (initialRefresh == true)
            initialRefresh = false;

       /* if (initialRefresh == true) {
            users.add(new User("Marcus Vinicius","https://www.nationalgeographic.com/content/dam/animals/thumbs/rights-exempt/mammals/d/domestic-dog_thumb.jpg", 1, 2, 12));
            initialRefresh = false;
        }

        mUserAdapter = new UserAdapter(mRootView.getContext(), users);

        mUsersListView.setAdapter(mUserAdapter);*/

        return mRootView;
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //mUserAdapter.add(new User("Marcus Vinicius","https://www.nationalgeographic.com/content/dam/animals/thumbs/rights-exempt/mammals/d/domestic-dog_thumb.jpg", 1, 2, 12));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }

    public void listar(){
        final ArrayList<User> users = new ArrayList<>();

        Query query = mUsersDatabaseReference.orderByChild("users");
        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                users.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    user = objSnapshot.getValue(User.class);
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

    private ArrayList<UserViewHolder> filterUserList(ArrayList<User> users){

        ArrayList<UserViewHolder> l = new ArrayList<UserViewHolder>();

        mUsersDatabaseReference = mUsersDatabaseReference.child(currentUser.getUid());
        mUsersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userActual = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });

        for(User u: users){
            UserViewHolder aux = new UserViewHolder();
            aux.setName(u.getName());

            if(u.getmUserAdress() != null) {
                aux.setDistancia(distance(userActual.getmUserAdress().getLatitude(), u.getmUserAdress().getLatitude(),
                        userActual.getmUserAdress().getLongitude(), u.getmUserAdress().getLongitude(),
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
