package com.example.hamam.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private EditText statusInput;
    private Button  statusChangeButton;

    private DatabaseReference changeStatusRef;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Toolbar toolbar =findViewById(R.id.status_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusInput =findViewById(R.id.status_input);
        statusChangeButton =findViewById(R.id.save_status_button);

   dialog =new ProgressDialog(this);

 String old_status =getIntent().getExtras().get("user_status").toString();
         statusInput.setText(old_status);

        mAuth =FirebaseAuth.getInstance();
        String user_id =mAuth.getCurrentUser().getUid();
        changeStatusRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        statusChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_status = statusInput.getText().toString();
                ChangeProfileStatus(new_status);


            }
        });



    }

    private void ChangeProfileStatus(String new_status) {

        if (TextUtils.isEmpty(new_status)){
            Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();

        }else {
            dialog.setTitle("Change Profile Status ");
            dialog.setMessage("Please Wait, Update profile Status...");
            dialog.show();
      changeStatusRef.child("user_status").setValue(new_status)
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()) {
                          dialog.dismiss();

                          Intent intent = new Intent(StatusActivity.this, SettingActivity.class);
                          startActivity(intent);
                          Toast.makeText(StatusActivity.this, "Status Change Successfully...", Toast.LENGTH_SHORT).show();
                      }else
                      {
                          Toast.makeText(StatusActivity.this, "Error To Change Status", Toast.LENGTH_SHORT).show();
                      }
                  }
              });



        }

    }
}
