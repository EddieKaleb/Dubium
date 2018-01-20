package com.dubium.fragments;

import android.os.Bundle;
import android.os.Handler;

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
            listFilter();

            return mRootView;
        }

        /* ESSE MÉTODO PEGA USUARIO ATUAL A PARTIR DO CURRENT E CHAMA O LISTAR, QUE VAI LISTAR, OBVIAMENTE */
        public void listFilter(){
            Query query = mDatabaseReference.child("users");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                        if (objSnapshot.getValue(User.class).getUid().equals(currentUser.getUid()))
                            userActual = objSnapshot.getValue(User.class);
                    }
                    if (initialRefresh) {
                        listar(userActual);
                        initialRefresh = false;
                    }else{
                        listar(userActual);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {            }
            });
        }
        /* VAI LISTAR OS USUARIOS DA MSM CIDADE E A PARTIR DE OUTROS PARAMETROS Q VOU COLOCAR AFTER*/
        public void listar(final User atual){
            final ArrayList<User> users = new ArrayList<>();

            Query query = mDatabaseReference.child("users");
            query.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user;

                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        user = objSnapshot.getValue(User.class);
                        if(!(userActual == null)) {
                            if(!(userActual.getLatitude() == 0)){
                                if (user.getCity().equals(userActual.getCity()) && user.getUid() != userActual.getUid())
                                    if((distance(userActual.getLatitude(), user.getLatitude(),
                                            userActual.getLongitude(), user.getLongitude(),
                                            0.0, 0.0)) < 40)
                                        users.add(user);
                            }
                        }
                    }
                    if (users.size() <= 0) {
                        //Toast.makeText(getActivity(), "Não há nenhum usuario proximo",Toast.LENGTH_SHORT).show();
                    } else {

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

        }

        /*ESSE AQUI VAI FAZER A TRASIÇÃO (PASSANDO OS DADOS) PRA O USER DO ADAPTER*/
        private ArrayList<UserViewHolder> filterUserList(ArrayList<User> users){
            ArrayList<UserViewHolder> l = new ArrayList<UserViewHolder>();

            for(User u: users){
                UserViewHolder aux = new UserViewHolder();
                aux.setName(u.getName());

                DecimalFormat df = new DecimalFormat("0.#");
                aux.setDistancia(df.format(distance(userActual.getLatitude(), u.getLatitude(),
                        userActual.getLongitude(), u.getLongitude(),
                        0.0, 0.0)));

                aux.setAptidoesComuns(1);
                aux.setDificuldadesComuns(1);
                l.add(aux);
            }

            return l;
        }

        private void refreshContent() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listFilter();
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
