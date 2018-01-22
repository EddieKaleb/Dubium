package com.dubium.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dubium.R;
import com.dubium.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);

        if(FirebaseAuth.getInstance().getUid().equals(message.getuId())){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message_sent, parent, false);
        }
        else{
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message_received, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.iv_photo);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.text_message_body);
        TextView timePhotoTextView = (TextView) convertView.findViewById(R.id.tv_time_photo);
        TextView timeTextTextView = (TextView) convertView.findViewById(R.id.tv_time_text);


        //TextView authorTextView = (TextView) convertView.findViewById(R.id.tv_name);

        String time = "--:--";
        if(message.getTime() != null){
            time = message.getTime();
        }

        if(message.getPhotoUrl() == null){

            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            timePhotoTextView.setVisibility(View.GONE);
            timeTextTextView.setVisibility(View.VISIBLE);

            messageTextView.setText(message.getText());
            timeTextTextView.setText(time);

        }
        else{

            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            timePhotoTextView.setVisibility(View.VISIBLE);
            timeTextTextView.setVisibility(View.GONE);

            timePhotoTextView.setText(time);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        }
        //authorTextView.setText(message.getName());

        return convertView;
    }
}
