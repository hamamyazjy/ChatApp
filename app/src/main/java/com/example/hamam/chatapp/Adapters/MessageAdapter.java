package com.example.hamam.chatapp.Adapters;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hamam.chatapp.Holder.MessageViewHolder;
import com.example.hamam.chatapp.Model.Messages;
import com.example.hamam.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;

    private DatabaseReference UserDatabaseReference;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View V = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_layout_of_user,parent,false);

        mAuth=FirebaseAuth.getInstance();

       return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String message_sender_id = mAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);

        String formuserId = messages.getFrom();
        String fromMessageType = messages.getType();

        UserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(formuserId);
        UserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child("user_name").getValue().toString();
                String userImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(holder.messagePicture.getContext()).load(userImage)
                        .placeholder(R.drawable.default_profile).into(holder.userProfileImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fromMessageType.equals("text")){

            holder.messagePicture.setVisibility(View.INVISIBLE);


            if (formuserId.equals(message_sender_id))
            {

                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setGravity(Gravity.RIGHT);

            }else {
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.LEFT);
            }

            holder.messageText.setText(messages.getMessage());


        }else {

            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageText.setPadding(0,0,0,0);

            Picasso.with(holder.userProfileImage.getContext()).load(messages.getMessage())
                    .placeholder(R.drawable.default_profile).into(holder.messagePicture);


        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();




    }
}
