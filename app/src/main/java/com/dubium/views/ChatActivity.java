package com.dubium.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dubium.BaseActivity;
import com.dubium.R;
import com.dubium.adapters.MessageAdapter;
import com.dubium.database.FirebaseDatabaseManager;
import com.dubium.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private String currentUserId;
    private String friendUserId;
    private String chatId = "";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final String FRIENDLY_MSG_LENGTH_KEY = "friendly_msg_length";

    public static final int RC_SIGN_IN = 1;
    public static final int RC_PHOTO_PIKER = 2;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseDatabaseManager mFirebaseDatabaseManager;

    private ChildEventListener mChildEventListener;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mUsername = mFirebaseAuth.getCurrentUser().getDisplayName();

        currentUserId = mFirebaseAuth.getUid();
        friendUserId = getIntent().getStringExtra("friendId");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseDatabaseManager = new FirebaseDatabaseManager();

        /** SE OS USUÁRIOS NUNCA SE COMUNICARAM É INICIALIZADO
         * UM CHAT NO BANCO DE DADOS ENTRE OS DOIS
         *
         * SE JÁ EXISTE UM CHAT ENTRE ELES, ENTÃO O ID DESSE CHAT EH
         * PARA SER UTILIZADO PELA ACTIVITY
         * ****/
        initializeChat();

        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.lv_messages);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.bt_photo_picker);
        mMessageEditText = (EditText) findViewById(R.id.et_message);
        mSendButton = (Button) findViewById(R.id.bt_send);

        // Initialize message ListView and its adapter
        final List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PIKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click

                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
                String time = format.format(new Date());

                Log.i("time", time);
                Message message = new Message(mMessageEditText.getText().toString(),
                        mUsername, mFirebaseAuth.getUid(), time, null);

                //mMessagesDatabaseReference.push().setValue(message);
                /*mFirebaseDatabase.getReference().child("chats").child(chatId)
                        .child("messages").push().setValue(message);*/

                if(chatId.equals("")){
                    createChat();
                }
                mMessagesDatabaseReference.push().setValue(message);                // Clear input box
                mMessageEditText.setText("");
            }
        });
    }

    /***** MENSAGEM EM FORMA DE FOTO *****/
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PIKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();

            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri).addOnSuccessListener
                    (this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
                            String time = format.format(new Date());
                            Message message = new Message(null, mUsername, mFirebaseAuth.getUid(), time, downloadUrl.toString());

                            /*mFirebaseDatabase.getReference().child("chats").child(chatId)
                                    .child("messages").push().setValue(message);*/

                            if(chatId.equals("")){
                                createChat();
                            }
                            mMessagesDatabaseReference.push().setValue(message);

                        }
                    });
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        /*detachDatabaseReadListener();
        mMessageAdapter.clear();*/
    }

    private void onSignedInitialize(){
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            mFirebaseDatabase.getReference().child("chats").child(chatId)
                    .child("messages").addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            mFirebaseDatabase.getReference().child("chats").child(chatId)
                    .child("messages").removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void initializeChat(){

        DatabaseReference ref = mFirebaseDatabase.getReference();
        ref.child("users").child(currentUserId).child("conversations").child(friendUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /***** INICIALIZA UM CHAT ENTRE OS USUÁRIOS *****/
                if(!dataSnapshot.exists()){

                    chatId = "";

                }
                /***** PEGA O CHAT_ID DE UM CHAT JÁ EXISTENTE ENTRE USERS *****/
                else{

                    Query query = mDatabaseReference.child("users").child(currentUserId).child("conversations").child(friendUserId);
                    query.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("initializeChat", "Chat existe");
                            chatId = dataSnapshot.getChildren().iterator().next().getKey();
                            Log.i("createChat", chatId);
                            Log.i("mDatabaseReference", "chats/" + chatId + "/messages/");
                            mMessagesDatabaseReference = mDatabaseReference.child("chats").child(chatId).child("messages");
                            onSignedInitialize();

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("createChat", "ERRO");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void createChat(){

        Log.i("initializeChat", "Chat não existe");
        chatId = mFirebaseDatabaseManager.initializeUserChat(currentUserId, friendUserId);
        Log.i("mDatabaseReference", "chats/" + chatId + "/messages/");
        mMessagesDatabaseReference = mDatabaseReference.child("chats").child(chatId).child("messages");
        onSignedInitialize();
    }

}
