package com.example.hamam.chatapp.Holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hamam.chatapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersViewHolder extends RecyclerView.ViewHolder {

    View mView;
    public AllUsersViewHolder(View itemView) {
        super(itemView);
        mView=itemView;
    }

    public void setUser_name(String user_name) {
        TextView name =mView.findViewById(R.id.user_name);
        name.setText(user_name);
     }
    public void setUser_status(String user_status) {
TextView status =mView.findViewById(R.id.user_status);
    status.setText(user_status);
    }

    public void setUser_thumb_image(final Context context , final String user_thumb_image) {
        final CircleImageView  thumb_image =mView.findViewById(R.id.user_image);

        Picasso.with(context).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.default_profile)
                .into(thumb_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(user_thumb_image).placeholder(R.drawable.default_profile).into(thumb_image);
                    }
                });



    }
}
