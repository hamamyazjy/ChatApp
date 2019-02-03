package com.example.hamam.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hamam.chatapp.Holder.AllUsersViewHolder;
import com.example.hamam.chatapp.Model.AllUsers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class AllUserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView allUserList;

    private EditText SearchInputText;
    private ImageView SearchButton;


  private DatabaseReference allDatabaserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

     toolbar =findViewById(R.id.all_user_app_bar);
     setSupportActionBar(toolbar);
     getSupportActionBar().setTitle("All Users");
     getSupportActionBar().setDisplayHomeAsUpEnabled(true);

     SearchInputText=findViewById(R.id.search_input_text);
     SearchButton =findViewById(R.id.search_btn);


     allUserList=findViewById(R.id.all_user_list);
    allUserList.setHasFixedSize(true);
    allUserList.setLayoutManager(new LinearLayoutManager(this));

     allDatabaserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    allDatabaserRef.keepSynced(true);



    SearchButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String searchUserName =SearchInputText.getText().toString();
            if (TextUtils.isEmpty(searchUserName)){
                Toast.makeText(AllUserActivity.this, "Please write user name to search", Toast.LENGTH_SHORT).show();

            }
            SearchForPeopleAndFriends(searchUserName);

        }
    });
    }



    private void SearchForPeopleAndFriends(String searchUserName ){

        Toast.makeText(this, "Searching", Toast.LENGTH_SHORT).show();

        Query searchPeopleAndFriend = allDatabaserRef.orderByChild("user_name").startAt(searchUserName)
                .endAt(searchUserName + "\uf8ff");


        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(AllUsers.class,R.layout.all_user_display_layout
                ,AllUsersViewHolder.class,searchPeopleAndFriend) {
                    @Override
                    protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {

                           viewHolder.setUser_name(model.getUser_name());
                           viewHolder.setUser_status(model.getUser_status());
                           viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());


                  viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                        String visit_user_id =getRef(position).getKey();
                          Intent i =new Intent(AllUserActivity.this,ProfileActivity.class);
                         i.putExtra("visit_user_id",visit_user_id);
                          startActivity(i);

                      }
                  });


                    }
                };
        allUserList.setAdapter(firebaseRecyclerAdapter);
    }

}
