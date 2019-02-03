package com.example.hamam.chatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hamam.chatapp.Adapters.MyFragmentsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class Main2Activity extends AppCompatActivity {

private Toolbar toolbar;

    private FirebaseAuth auth;

    private MyFragmentsAdapter myFragmentsAdapter;

    private ViewPager myViewPager;
    private TabLayout myTablayout;
    FirebaseUser currentuser;

    private DatabaseReference UserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

              auth=  FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser();

            if (currentuser != null){
                String online_user_id =auth.getCurrentUser().getUid();
                UserReference= FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);



              }

       myViewPager =findViewById(R.id.main_tabs_pager);
       myFragmentsAdapter =new MyFragmentsAdapter(getSupportFragmentManager());
       myViewPager.setAdapter(myFragmentsAdapter);


       myTablayout =findViewById(R.id.main_tabs);
       myTablayout.setupWithViewPager(myViewPager);





    toolbar =findViewById(R.id.main_page_toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("ChatApp");

    }




    @Override
    protected void onStart() {
        super.onStart();

          currentuser = auth.getCurrentUser();
        if(currentuser==null){
           LogoutUser();

        } else  if(currentuser != null){


            UserReference.child("online").setValue("true");

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentuser != null){


            UserReference.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId()==R.id.logout_main_button){

            if (currentuser != null){
                UserReference.child("online").setValue(ServerValue.TIMESTAMP);


            }
            auth.signOut();
            LogoutUser();
        }
        if (item.getItemId()==R.id.accountsettings_main_button){

            Intent intent =new Intent(Main2Activity.this,SettingActivity.class);
            startActivity(intent);
        } if (item.getItemId()==R.id.main_all_user){
            Intent i =new Intent(Main2Activity.this,AllUserActivity.class);
            startActivity(i);
        }

return true;
    }

    private void LogoutUser() {

        Intent intent =new Intent(Main2Activity.this,StartPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
