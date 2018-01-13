package com.dubium.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mUsersDatabaseReference = mFirebaseDatabase.getReference().child("users");


    boolean initialRefresh = true;

    public final ArrayList<User> users = new ArrayList<>();

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);

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

        mUsersDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user;
                users.clear();

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    user = objSnapshot.getValue(User.class);
                    users.add(user);
                }
                if (users.size() <= 0) {
                    Toast.makeText(getActivity(), "Não há nenhum pedido",
                            Toast.LENGTH_SHORT).show();
                } else {
                    UserAdapter adapter = new UserAdapter(getActivity(), users );

                    mUsersListView.setAdapter(adapter);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {Toast.makeText(getActivity(), "ERRO", Toast.LENGTH_SHORT).show();}
        });

    }

}
