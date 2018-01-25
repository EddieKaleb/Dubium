package com.dubium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dubium.R;
import com.dubium.adapters.ChatAdapter;
import com.dubium.model.Chat;
import com.dubium.model.Message;
import com.dubium.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    View mRootView;
    ListView mLvChats;

    List<Chat> mChats = new ArrayList<>();

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    public ChatsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_chats, container, false);

        mLvChats = (ListView) mRootView.findViewById(R.id.lv_chats);

        /*mChats.add(new Chat("Eddie K.", "Bora bora meu patrão", "12:20",
                "https://img.buzzfeed.com/buzzfeed-static/static/2017-08/9/11/enhanced/buzzfeed-prod-fastlane-02/enhanced-1731-1502293831-8.jpg"));

        mChats.add(new Chat("Héricles", "Me ajuda ai pow", "09:12",
                "https://i.pinimg.com/736x/02/d7/01/02d701b77a984a1b6cf970e6eb0468e1--teacup-maltipoo-maltipoo-puppies.jpg"));*/


        ChatAdapter mChatAdapter = new ChatAdapter(getContext(), mChats);
        mLvChats.setAdapter(mChatAdapter);
        setChats(mChatAdapter);

        return mRootView;
    }

    public void setChats(final ChatAdapter chatAdapter){

        String uId = FirebaseAuth.getInstance().getUid();

        Query query1 = mDatabaseReference.child("users").child(uId).child("conversations");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    String friendId = objSnapshot.getKey();
                    final String chatId = objSnapshot.getChildren().iterator().next().getKey();

                    final Chat chat = new Chat();

                    chat.setFriendId(friendId);

                    //mChats.add(chat);

                    Query query2 = mDatabaseReference.child("users").child(friendId);
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("Query1", dataSnapshot.child("name").getValue().toString());

                            String name = dataSnapshot.child("name").getValue().toString();

                            String photoUrl = null;
                            if(dataSnapshot.child("photoUrl").getValue() != null){
                                photoUrl = dataSnapshot.child("photoUrl").getValue().toString();
                            }

                            //User user = dataSnapshot.getValue(User.class);

                            chat.setName(name);
                            chat.setPhotoUrl(photoUrl);

                            Query query3 = mDatabaseReference.child("chats").child(chatId).child("messages").orderByKey().limitToLast(1);
                            query3.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String messageId = dataSnapshot.getChildren().iterator().next().getKey();
                                    Log.i("MessageId", messageId);

                                    String text = dataSnapshot.child(messageId).child("text").getValue().toString();
                                    String time = dataSnapshot.child(messageId).child("time").getValue().toString();

                                    chat.setMessage(text);
                                    chat.setTime(time);

                                    chatAdapter.add(chat);
                                    chatAdapter.notifyDataSetChanged();
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
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("getChats", "erro");
            }
        });

    }


}
