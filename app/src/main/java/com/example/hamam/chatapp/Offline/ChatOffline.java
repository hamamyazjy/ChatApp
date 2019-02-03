package com.example.hamam.chatapp.Offline;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class ChatOffline extends Application {

    private DatabaseReference UsersReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Load image offline  picass
        Picasso.Builder builder =new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this , Integer.MAX_VALUE));
        Picasso  built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String online_user_id = mAuth.getCurrentUser().getUid();

            UsersReference =FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);

            UsersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    UsersReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                 //   UsersReference.child("online").setValue(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }






    }
}