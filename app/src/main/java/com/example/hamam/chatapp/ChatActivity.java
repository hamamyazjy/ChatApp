package com.example.hamam.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamam.chatapp.Adapters.MessageAdapter;
import com.example.hamam.chatapp.Model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverdId;
    private String messageReceiverdName;

    private Toolbar chatToolBar;
    private TextView userNameTitle;
   private TextView  userLastSeen;
   private CircleImageView userChatProfileImage;
   private DatabaseReference rootRef;


   private ImageButton sendMessageButton;
   private ImageButton selectImageButton;
   private EditText InputMessageText;

   private FirebaseAuth mAuth;
   private String messageSenderId;
   private RecyclerView usermessageList;

    private final List<Messages> messageList =new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private static int Gallery_Pick =1;

    private StorageReference MessageImageStorageRef;
    private ProgressDialog  dialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth= FirebaseAuth.getInstance();
        messageSenderId =mAuth.getCurrentUser().getUid();

        messageReceiverdId =getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverdName =getIntent().getExtras().get("user_name").toString();
         MessageImageStorageRef=FirebaseStorage.getInstance().getReference().child("Messages_Pictures");




        chatToolBar =findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view =layoutInflater.inflate(R.layout.chat_custom_bar,null);

        getSupportActionBar().setCustomView(action_bar_view);

        userNameTitle =findViewById(R.id.custom_profile_name);
        userLastSeen =findViewById(R.id.custom_user_seen_last);
        userChatProfileImage =findViewById(R.id.custom_image);

        dialog = new ProgressDialog(this);
        sendMessageButton =findViewById(R.id.send_message);
        selectImageButton= findViewById(R.id.select_image);
        InputMessageText=findViewById(R.id.imput_message);

        messageAdapter =new MessageAdapter(messageList);

        usermessageList=findViewById(R.id.messages_list_of_user);

        linearLayoutManager =new LinearLayoutManager(this);
        usermessageList.setHasFixedSize(true);
        usermessageList.setLayoutManager(linearLayoutManager);
        usermessageList.setAdapter(messageAdapter);

        FetchMessage();
        userNameTitle.setText(messageReceiverdName);

         rootRef.child("Users").child(messageReceiverdId).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              final String online =dataSnapshot.child("online").getValue().toString(); //return false
              final String userThumb =dataSnapshot.child("user_thumb_image").getValue().toString();

                 Picasso.with(ChatActivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE)
                         .placeholder(R.drawable.default_profile)
                         .into(userChatProfileImage, new Callback() {
                             @Override
                             public void onSuccess() {

                             }



                             @Override
                             public void onError() {
                                 Picasso.with(ChatActivity.this).load(userThumb).placeholder(R.drawable.default_profile).into(userChatProfileImage);
                             }
                         });

                 if (online.equals("true")){

                     userLastSeen.setText("oline");


                 }else {
                         LastSeenTime getTime =new LastSeenTime();
                         long last_seen =Long.parseLong(online);
                         String lastSeenDisplayTime = getTime.getTimeAgo(last_seen,getApplicationContext());


                     userLastSeen.setText(lastSeenDisplayTime);

                 }



             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


         sendMessageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 semdMessage();


             }
         });


        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent =new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);









            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data != null){

            dialog.setTitle("Sending Chat Image");
            dialog.setMessage("PLease wait, While your chat message  is sending...");
            dialog.show();

            Uri imageUri =data.getData();

           final String message_sender_ref = "Messages/"+ messageSenderId +"/" +messageReceiverdId;
            final  String message_receiver_ref = "Messages/"+ messageReceiverdId +"/" +messageSenderId;

            DatabaseReference  user_message_key =rootRef.child("Messages").child(messageSenderId).child(messageReceiverdId).push();
           final String message_push_id =user_message_key.getKey();

            StorageReference filePath =MessageImageStorageRef.child(message_push_id +".jpg");
           filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                   if (task.isSuccessful())
                   {

                       String  downloadUri =task.getResult().getDownloadUrl().toString();


                       Map messageTextBody =new HashMap();
                       messageTextBody.put("message", downloadUri);
                       messageTextBody.put("type","image");
                       messageTextBody.put("time", ServerValue.TIMESTAMP);
                       messageTextBody.put("from", messageSenderId);



                       Map messageBodyDetails =new HashMap();
                       messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
                       messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);

                       rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                           @Override
                           public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                               if (databaseError != null){
                                   Log.d("Chat_log ", databaseError.getMessage().toString());

                               }
                               InputMessageText.setText("");

                               dialog.dismiss();
                           }
                       });

                       Toast.makeText(ChatActivity.this, "Picture send Successfully", Toast.LENGTH_SHORT).show();
             dialog.dismiss();

                   }else
                       {
                           Toast.makeText(ChatActivity.this, "Picture not send . Try Again ", Toast.LENGTH_SHORT).show();
                           dialog.dismiss();

                       }
                   }

           });

        }


    }

    private void FetchMessage() {

    rootRef.child("Messages").child(messageSenderId).child(messageReceiverdId).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Messages messages =dataSnapshot.getValue(Messages.class);
            messageList.add(messages);
            messageAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    private void semdMessage() {


        String messageText =InputMessageText.getText().toString();
        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this, "Please Write to message", Toast.LENGTH_SHORT).show();

        }
        else
        {

        }
        String message_sender_ref = "Messages/"+ messageSenderId +"/" +messageReceiverdId;
        String message_receiver_ref = "Messages/"+ messageReceiverdId +"/" +messageSenderId;

         DatabaseReference  user_message_key =rootRef.child("Messages").child(messageSenderId).child(messageReceiverdId).push();
         String message_push_id =user_message_key.getKey();

        Map messageTextBody =new HashMap();
        messageTextBody.put("message",messageText);
        messageTextBody.put("seen",false);
        messageTextBody.put("type","text");
        messageTextBody.put("time", ServerValue.TIMESTAMP);
        messageTextBody.put("from", messageSenderId);



        Map messageBodyDetails =new HashMap();
        messageBodyDetails.put(message_sender_ref+"/"+message_push_id,messageTextBody);
        messageBodyDetails.put(message_receiver_ref+"/"+message_push_id,messageTextBody);

        rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if (databaseError != null)
                {

                    Log.d("Chat_log",databaseError.getMessage().toString());
                }
                InputMessageText.setText("");
            }
        });







    }
}
