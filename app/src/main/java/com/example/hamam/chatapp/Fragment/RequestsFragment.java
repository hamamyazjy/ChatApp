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
import android.widget.Button;
import android.widget.Toast;

import com.example.hamam.chatapp.ChatActivity;
import com.example.hamam.chatapp.Holder.RequestViewHolder;
import com.example.hamam.chatapp.Model.Request;
import com.example.hamam.chatapp.ProfileActivity;
import com.example.hamam.chatapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView myRequestList;
    private View myMainView;

    private DatabaseReference FriendsRequestReference;
   private FirebaseAuth mAuth;
    String online_user_id;
    private DatabaseReference UsersReference;


    private DatabaseReference FriendsDatabaseRef;

    private DatabaseReference FriendsReqDatabaseRef;



    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        myRequestList  =myMainView.findViewById(R.id.request_list);
          mAuth=FirebaseAuth.getInstance();
          online_user_id=mAuth.getCurrentUser().getUid();

        FriendsRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);

        UsersReference=FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");





        myRequestList.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestList.setLayoutManager(linearLayoutManager);




        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request,RequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                        Request.class,
                        R.layout.friend_request_all_user_layout,
                        RequestViewHolder.class,
                        FriendsRequestReference
                ) {
                    @Override
                    protected void populateViewHolder(final RequestViewHolder viewHolder, Request model, int position)
                    {

                        final String list_user_id =getRef(position).getKey();

                        DatabaseReference get_type_ref =getRef(position).child("request_type").getRef();
                        get_type_ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    String request_type =dataSnapshot.getValue().toString();

                                     if (request_type.equals("received"))
                                     {

                                         UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                 final String name =dataSnapshot.child("user_name").getValue().toString();
                                                 String thumb_image =dataSnapshot.child("user_thumb_image").getValue().toString();
                                                 String userstatus =dataSnapshot.child("user_status").getValue().toString();

                                                 viewHolder.setUserName(name);
                                                 viewHolder.setThumbImage(getContext(),thumb_image);
                                                 viewHolder.setUserStatus(userstatus);
                                                 viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {

                                                         CharSequence options[] = new CharSequence[]{
                                                                  "Accept Friend Request",
                                                                  "Cancel Friend Request"
                                                         };
                                                         AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                                         builder.setTitle("Friend Request Options");


                                                         builder.setItems(options, new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialogInterface, int position) {

                                                                 if (position ==0){

                                                                     Calendar calFordATE =Calendar.getInstance();
                                                                     SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMMM-yyyy");
                                                                     final String saveCurrentDate =currentDate.format(calFordATE.getTime());

                                                                     FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                                             .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                 @Override
                                                                                 public void onSuccess(Void aVoid) {

                                                                                     FriendsDatabaseRef.child(list_user_id).child(online_user_id ).child("date").setValue(saveCurrentDate)
                                                                                             .addOnSuccessListener(new OnSuccessListener<Void>() {

                                                                                                 @Override
                                                                                                 public void onSuccess(Void aVoid) {

                                                                                                     FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                 @Override
                                                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                                                     if (task.isSuccessful()) {
                                                                                                                         FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                     @Override
                                                                                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                         if (task.isSuccessful()) {
                                                                                                                                             Toast.makeText(getContext(), "Friend  Request Accepted Successfully", Toast.LENGTH_SHORT).show();
                                                                                                                                         }
                                                                                                                                     }
                                                                                                                                 });

                                                                                                                     }
                                                                                                                 }
                                                                                                             });


                                                                                                 }
                                                                                             });
                                                                                 }
                                                                             });

                                                                 }
                                                                 if (position ==1){
                                                                     FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                 @Override
                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                     if (task.isSuccessful()) {
                                                                                         FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                     @Override
                                                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                                                         if (task.isSuccessful()) {
                                                                                                             Toast.makeText(getContext(), "Friends Req Cancel Successfully", Toast.LENGTH_SHORT).show();

                                                                                                         }
                                                                                                     }
                                                                                                 });

                                                                                     }
                                                                                 }
                                                                             });

                                                                 }

                                                             }
                                                         });
                                                         builder.show();
                                                     }

                                                 });



                                             }
                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });


                                     } else if (request_type.equals("sent"))
                                     {

                                         Button req_set_btn =viewHolder.itemView.findViewById(R.id.request_accept_btn);
                                         req_set_btn.setText("Req sent");



                                         UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                 final String name =dataSnapshot.child("user_name").getValue().toString();
                                                 String thumb_image =dataSnapshot.child("user_thumb_image").getValue().toString();
                                                 String userstatus =dataSnapshot.child("user_status").getValue().toString();

                                                 viewHolder.setUserName(name);
                                                 viewHolder.setThumbImage(getContext(),thumb_image);
                                                 viewHolder.setUserStatus(userstatus);

                                                 viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {

                                                         CharSequence options[] = new CharSequence[]{
                                                                  "Cancel Friend Request",
                                                         };
                                                         AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
                                                         builder.setTitle("Friend Request sent");


                                                         builder.setItems(options, new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialogInterface, int position) {


                                                                 if (position ==0){
                                                                     FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                 @Override
                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                     if (task.isSuccessful()) {
                                                                                         FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                     @Override
                                                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                                                         if (task.isSuccessful()) {
                                                                                                             Toast.makeText(getContext(), "Friends Req Cancel Successfully", Toast.LENGTH_SHORT).show();

                                                                                                         }
                                                                                                     }
                                                                                                 });

                                                                                     }
                                                                                 }
                                                                             });

                                                                 }

                                                             }
                                                         });
                                                         builder.show();

                                                     }
                                                 });


                                             }
                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });

                                     }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                };
                  myRequestList.setAdapter(firebaseRecyclerAdapter);
    }
}
