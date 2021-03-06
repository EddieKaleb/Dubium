package com.dubium.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.fragments.ChatsFragment;
import com.dubium.model.Chat;
import com.dubium.views.ChatActivity;
import com.dubium.views.HomeActivity;
import com.facebook.login.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eddie on 22/01/2018.
 */

public class ChatAdapter extends ArrayAdapter<Chat> implements Serializable{

    RelativeLayout mChatContainer;
    ImageView mIvFotoChat;
    TextView mTvNomeChat;
    TextView mTvUltimaMensagem;
    TextView mTvHoraMensagem;
    Context mContext;

    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

    public ChatAdapter(Context context, List<Chat> chats) {
        super(context, 0, chats);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Chat chat = getItem(position);


        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_chat, parent, false);

        mIvFotoChat = (ImageView) convertView.findViewById(R.id.iv_foto_chat);
        mTvNomeChat = (TextView) convertView.findViewById(R.id.tv_nome_chat);
        mTvUltimaMensagem = (TextView) convertView.findViewById(R.id.tv_ultima_mensagem);
        mTvHoraMensagem = (TextView) convertView.findViewById(R.id.tv_hora_mensagem);
        mChatContainer = (RelativeLayout) convertView.findViewById(R.id.chat_container);

        String photoUrl = null;

        if(chat.getPhotoUrl() == null){}
        else if(chat.getPhotoUrl().equals("")){
            photoUrl = null;
        }
        else{
            photoUrl = chat.getPhotoUrl();
            Log.i("FriendPhotoUrl", chat.getPhotoUrl()+"");

            Glide.with(mIvFotoChat.getContext())
                    .load(chat.getPhotoUrl())
                    .into(mIvFotoChat);
        }

        mTvNomeChat.setText(chat.getName());
        mTvUltimaMensagem.setText(chat.getMessage());
        mTvHoraMensagem.setText(chat.getTime());

        final String friendPhotoUrl = photoUrl;

        mChatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putString("friendUid", chat.getFriendId());
                mBundle.putString("friendPhotoUrl", friendPhotoUrl);
                mBundle.putString("friendName", chat.getName());

                intent.putExtras(mBundle);

                mContext.startActivity(intent);
            }
        });

        return convertView;

    }


}
