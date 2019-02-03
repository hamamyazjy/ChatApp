package com.example.hamam.chatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button sendFriendRequestButton,declineFriendRequestbutton;
    private TextView profileName,profileStatus;
    private ImageView profileImage;
    private DatabaseReference usersReference;
    private DatabaseReference friendRequestReference;
    private FirebaseAuth mAuth;
    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationReference;


    String sender_user_id;
    String receiver_user_id;
    private String CURRENT_STATE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friendRequestReference=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendRequestReference.keepSynced(true);



        mAuth =FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference =FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationReference.keepSynced(true);


        usersReference= FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id =getIntent().getExtras().get("visit_user_id").toString();



         sendFriendRequestButton=findViewById(R.id.profile_send_req_btn);
        declineFriendRequestbutton=findViewById(R.id.profile_decline_btn);
        profileName=findViewById(R.id.profile_visit_user_name);
        profileStatus=findViewById(R.id.profile_visit_user_status);
        profileImage =findViewById(R.id.profile_visit_user_image);

        CURRENT_STATE ="not_friends";

        usersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name =dataSnapshot.child("user_name").getValue().toString();
                String status =dataSnapshot.child("user_status").getValue().toString();
                String image =dataSnapshot.child("user_image").getValue().toString();
                String thumb_image =dataSnapshot.child("user_thumb_image").getValue().toString();



                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(getBaseContext()).load(thumb_image).placeholder(R.drawable.default_profile).into(profileImage);

                friendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                  if(dataSnapshot.hasChild(receiver_user_id)) {
                                      String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                      if (req_type.equals("sent")){
                                          CURRENT_STATE ="request_sent";
                                          sendFriendRequestButton.setText("Cancel Friend Request");

                                          declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                          declineFriendRequestbutton.setEnabled(false);


                                      }else if (req_type.equals("received")){
                                          CURRENT_STATE ="request_received";
                                          sendFriendRequestButton.setText("Accept Friend Request");

                                          declineFriendRequestbutton.setVisibility(View.VISIBLE);
                                          declineFriendRequestbutton.setEnabled(true);

                                          declineFriendRequestbutton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {

                                                  DeclineFriendRequest();
                                              }
                                          });
                                      }}
                                else {

                                  FriendsReference.child(sender_user_id)
                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                  if (dataSnapshot.hasChild(receiver_user_id)){

                                                      CURRENT_STATE ="friends";
                                                      sendFriendRequestButton.setText("Unfriend This Person");

                                                      declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                      declineFriendRequestbutton.setEnabled(false);

                                                  }
                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });

                              }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        declineFriendRequestbutton.setVisibility(View.INVISIBLE);
        declineFriendRequestbutton.setEnabled(false);

        if (!sender_user_id.equals(receiver_user_id)){
            sendFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequestButton.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friends")){
                        sendFriendRequestToAPerson();

                    }
                    if(CURRENT_STATE.equals("request_sent")){
                        CancelRequestFriend();

                    }
                    if(CURRENT_STATE.equals("request_received")){
                        AcceptFriendRequest();

                    } if(CURRENT_STATE.equals("friends")){
                        UnFriendaFriend();

                    }

                }
            });
        }else{

            declineFriendRequestbutton.setVisibility(View.INVISIBLE);
            sendFriendRequestButton.setVisibility(View.INVISIBLE);


        }}

    private void DeclineFriendRequest() {

        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friend";
                                                sendFriendRequestButton.setText("Send Friend Request");

                                                declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                declineFriendRequestbutton.setEnabled(false);

                                            }
                                        }
                                    });

                        }
                    }
                });

    }



    private void UnFriendaFriend() {
    FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            sendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE ="not_friends";
                                            sendFriendRequestButton.setText("Send Friend Request");

                                            declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                            declineFriendRequestbutton.setEnabled(false);

                                        }
                                    }
                                });


                    }
                }
            });

    }

    private void AcceptFriendRequest() {
        Calendar calFordATE =Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMMM-yyyy");
        final String saveCurrentDate =currentDate.format(calFordATE.getTime());
        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                sendFriendRequestButton.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendRequestButton.setText("Unfriend this person");

                                                                                declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                                                declineFriendRequestbutton.setEnabled(false);
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

    private void CancelRequestFriend() {
      friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
  .addOnCompleteListener(new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful()) {
    friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendFriendRequestButton.setEnabled(true);
                        CURRENT_STATE = "not_friend";
                        sendFriendRequestButton.setText("Send Friend Request");

                        declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                        declineFriendRequestbutton.setEnabled(false);

                    }
                }
            });

}
      }
  });
    }

    private void sendFriendRequestToAPerson() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                HashMap<String,String> notificationData =new HashMap<String, String>();
                                                notificationData.put("from",sender_user_id);
                                                notificationData.put("type","request");
                                                NotificationReference.child(receiver_user_id).push()
                                                        .setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){
                                                            sendFriendRequestButton.setEnabled(true);
                                                            CURRENT_STATE ="request_sent";
                                                            sendFriendRequestButton.setText("Cancel Friend Request");

                                                            declineFriendRequestbutton.setVisibility(View.INVISIBLE);
                                                            declineFriendRequestbutton.setEnabled(false);


                                                        }

                                                    }
                                                });

                                            }
                                        }
                                    });


                        }

                    }
                });


    }
}
