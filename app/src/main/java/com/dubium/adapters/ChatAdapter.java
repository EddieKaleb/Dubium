package com.dubium.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.model.Chat;

import java.util.List;

/**
 * Created by eddie on 22/01/2018.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {

    RelativeLayout mChatContainer;
    ImageView mIvFotoChat;
    TextView mTvNomeChat;
    TextView mTvUltimaMensagem;
    TextView mTvHoraMensagem;

    public ChatAdapter(Context context, List<Chat> chats) {
        super(context, 0, chats);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Chat chat = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_chat, parent, false);

        mIvFotoChat = (ImageView) convertView.findViewById(R.id.iv_foto_chat);
        mTvNomeChat = (TextView) convertView.findViewById(R.id.tv_nome_chat);
        mTvUltimaMensagem = (TextView) convertView.findViewById(R.id.tv_ultima_mensagem);
        mTvHoraMensagem = (TextView) convertView.findViewById(R.id.tv_hora_mensagem);
        mChatContainer = (RelativeLayout) convertView.findViewById(R.id.chat_container);

        if (chat.getPhotoUrl() != null) {
            Glide.with(mIvFotoChat.getContext())
                    .load(chat.getPhotoUrl())
                    .into(mIvFotoChat);
        }

        mTvNomeChat.setText(chat.getName());
        mTvUltimaMensagem.setText(chat.getMessage());
        mTvHoraMensagem.setText(chat.getTime());

        mChatContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir o chat privado
            }
        });

        return convertView;
    }
}
