package com.dubium.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dubium.R;
import com.dubium.adapters.ChatAdapter;
import com.dubium.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment{

    View mRootView;
    ListView mLvChats;

    SwipeRefreshLayout mSwipeRefreshLayout;
    ChatAdapter mChatAdapter;
    List<Chat> mChats = new ArrayList<>();

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    boolean initialRefresh = true;

    public ChatsFragment() { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        init();
        setListeners();
        loadData();
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void init() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh_chats);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.gray);

        mLvChats = (ListView) mRootView.findViewById(R.id.lv_chats);
        mChatAdapter = new ChatAdapter(getContext(), mChats);
        mLvChats.setAdapter(mChatAdapter);
    }

    private void loadData() {
        if (initialRefresh) {
            setChats();
            initialRefresh = false;
        }
    }

    public void setChats(){

        String uId = FirebaseAuth.getInstance().getUid();

        Query query1 = mDatabaseReference.child("users").child(uId).child("conversations");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        final String friendId = objSnapshot.getKey();
                        final String chatId = objSnapshot.getChildren().iterator().next().getKey();

                        final Chat chat = new Chat();

                        chat.setChatId(chatId);
                        chat.setFriendId(friendId);

                        Query query2 = mDatabaseReference.child("users").child(friendId);
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.i("Query1", dataSnapshot.child("name").getValue().toString());

                                String name = dataSnapshot.child("name").getValue().toString();

                                String photoUrl = "";
                                if (dataSnapshot.child("photoUrl").getValue() != null) {
                                    photoUrl = dataSnapshot.child("photoUrl").getValue().toString();
                                }

                                chat.setName(name);
                                chat.setPhotoUrl(photoUrl);

                                Query query3 = mDatabaseReference.child("chats").child(chatId).child("messages").orderByKey().limitToLast(1);
                                query3.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String messageId = dataSnapshot.getChildren().iterator().next().getKey();
                                        Log.i("MessageId", messageId);


                                        String text = "";
                                        if (dataSnapshot.child(messageId).child("text").getValue() != null) {
                                            text = dataSnapshot.child(messageId).child("text").getValue().toString();

                                            if (dataSnapshot.child(messageId).child("text").getValue().toString().length() > 40) {
                                                text = text.substring(0, 30) + "...";
                                            }
                                        } else {
                                            text = "Foto";
                                        }

                                        String time = dataSnapshot.child(messageId).child("time").getValue().toString();

                                        chat.setMessage(text);
                                        chat.setTime(time);

                                        boolean isNewChat = true;
                                        if (mChatAdapter.getCount() == 0)
                                            mChatAdapter.add(chat);
                                        else {
                                            for (int i = 0; i < mChatAdapter.getCount(); i++) {
                                                Chat c = mChatAdapter.getItem(i);
                                                if (c.getFriendId().equals(chat.getFriendId())) {
                                                    mChatAdapter.getItem(i).setMessage(text);
                                                    mChatAdapter.getItem(i).setTime(time);
                                                    mChatAdapter.notifyDataSetChanged();
                                                    isNewChat = false;
                                                }
                                            }
                                            if (isNewChat) mChatAdapter.add(chat);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i("getChats", "erro");
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i("getChats", "erro");
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("getChats", "erro");
            }
        });
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
}
