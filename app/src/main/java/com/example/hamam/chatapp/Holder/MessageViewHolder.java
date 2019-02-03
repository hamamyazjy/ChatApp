package com.example.hamam.chatapp.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hamam.chatapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageText;
    public CircleImageView userProfileImage;
    public ImageView messagePicture;





    public MessageViewHolder(View itemView) {
        super(itemView);

        messageText =itemView.findViewById(R.id.messages_text);
        messagePicture =itemView.findViewById(R.id.message_image_view);
         userProfileImage =itemView.findViewById(R.id.message_profile_image);



    }


}
