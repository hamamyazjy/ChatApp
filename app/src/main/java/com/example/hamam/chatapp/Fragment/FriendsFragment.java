package com.example.hamam.chatapp.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hamam.chatapp.ChatActivity;
import com.example.hamam.chatapp.Holder.FriendsViewHolder;
import com.example.hamam.chatapp.Model.Friends;
import com.example.hamam.chatapp.ProfileActivity;
import com.example.hamam.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendsList;
   private DatabaseReference friendReference;
   private DatabaseReference UsersReference;
   private FirebaseAuth mAuth;

     String name;

   String online_user_id;
   private View myMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMainView=inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendsList=myMainView.findViewById(R.id.friends_list);

        mAuth=FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();

        friendReference= FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendReference.keepSynced(true);

        UsersReference= FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);

   myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

         return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.all_user_display_layout,
                        FriendsViewHolder.class,
                        friendReference
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {

                        viewHolder.setDate(model.getDate());
                        final String list_user_id =getRef(position).getKey();

                        UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                                 name =dataSnapshot.child("user_name").getValue().toString();
                                  String thumb_image =dataSnapshot.child("user_thumb_image").getValue().toString();

                                  if (dataSnapshot.hasChild("online")){
                                      String online_status = (String) dataSnapshot.child("online").getValue().toString();
                                      viewHolder.setUserOnline(online_status);


                                  }




                                viewHolder.setUserName(name);
                                viewHolder.setThumbImage( getContext(),thumb_image);
                                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        CharSequence options[] = new CharSequence[]{
                                                name + "'s Profile", "send message"
                                        };
                                        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");


                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int position) {

                                                if (position ==0){
                                                    Intent profileActivity =new Intent(getContext(), ProfileActivity.class);
                                                    profileActivity.putExtra("visit_user_id",list_user_id);
                                                    startActivity(profileActivity);

                                                }
                                                if (position ==1){
                                                  if (dataSnapshot.child("online").exists()){
                                                      Intent ChatsActivity =new Intent(getContext(), ChatActivity.class);
                                                      ChatsActivity.putExtra("visit_user_id",list_user_id);
                                                      ChatsActivity.putExtra("user_name",name);
                                                      startActivity(ChatsActivity);
                                                  }else{
                                                      UsersReference.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP)
                                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {


                                                                  }
                                                              });

                                                  }

                                                }

                                            }
                                        });
                              builder.show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Intent ChatsActivity =new Intent(getContext(), ChatActivity.class);
                                ChatsActivity.putExtra("visit_user_id",list_user_id);
                                ChatsActivity.putExtra("user_name",name);
                                startActivity(ChatsActivity);
                            }
                        });



                    }
                };
        myFriendsList.setAdapter(firebaseRecyclerAdapter);
     }
}
