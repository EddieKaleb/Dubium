package com.dubium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dubium.R;
import com.dubium.adapters.ChatAdapter;
import com.dubium.model.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    View mRootView;
    ListView mLvChats;

    List<Chat> mChats = new ArrayList<>();

    public ChatsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_chats, container, false);

        mLvChats = (ListView) mRootView.findViewById(R.id.lv_chats);

        mChats.add(new Chat("Eddie K.", "Bora bora meu patrão", "12:20",
                "https://img.buzzfeed.com/buzzfeed-static/static/2017-08/9/11/enhanced/buzzfeed-prod-fastlane-02/enhanced-1731-1502293831-8.jpg"));

        mChats.add(new Chat("Héricles", "Me ajuda ai pow", "09:12",
                "https://i.pinimg.com/736x/02/d7/01/02d701b77a984a1b6cf970e6eb0468e1--teacup-maltipoo-maltipoo-puppies.jpg"));


        ChatAdapter mChatAdapter = new ChatAdapter(getContext(), mChats);
        mLvChats.setAdapter(mChatAdapter);

        return mRootView;
    }

}
