package com.example.hamam.chatapp.Holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hamam.chatapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

     public class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }


        public void setUserName(String userName) {
            TextView userNameDisplay =mView.findViewById(R.id.user_name);
            userNameDisplay.setText(userName);
        }


        public void setThumbImage(final Context context , final String thumbImage) {

            final CircleImageView thumb_image =mView.findViewById(R.id.user_image);

            Picasso.with(context).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(thumbImage).placeholder(R.drawable.default_profile).into(thumb_image);
                        }
                    });






        }

        public void setUserOnline(String userOnline) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);
            if (userOnline.equals("true")){

                onlineStatusView.setVisibility(View.VISIBLE);
            }else {
                onlineStatusView.setVisibility(View.INVISIBLE);


            }
        }

         public void setUserStatus(String userstatus) {

            TextView user_status =  mView.findViewById(R.id.user_status);
            user_status.setText(userstatus);


         }
     }
