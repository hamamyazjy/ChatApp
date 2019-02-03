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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth  mAuth;

    private EditText loginEmail,loginPassword;
    private Button signInbutton;

    private ProgressDialog progressDialog;
     private Toolbar toolbar;

     private DatabaseReference usersreference;

     String deviceToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar =findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign In");
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);


         mAuth=FirebaseAuth.getInstance();
         usersreference = FirebaseDatabase.getInstance().getReference().child("Users");

    loginEmail =findViewById(R.id.register_email);
    loginPassword =findViewById(R.id.password);
    signInbutton =findViewById(R.id.sign_in);
       progressDialog =new ProgressDialog(this);
          signInbutton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String email =loginEmail.getText().toString();
            String password =loginPassword.getText().toString();

            LoginUserAccount(email,password);


        }
    });






    }

    private void LoginUserAccount(String email, String password) {

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }  else{
            progressDialog.setTitle("Login Account");
            progressDialog.setMessage("Please Wait, while we are verifing your credentials...");
            progressDialog.show();

       mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

               if (task.isSuccessful()){

                   final String online_user_id =mAuth.getCurrentUser().getUid();
                 //  String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                   FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                       @Override
                       public void onSuccess(InstanceIdResult instanceIdResult) {
                           deviceToken = instanceIdResult.getToken();
                           // Do whatever you want with your token now
                           // i.e. store it on SharedPreferences or DB
                           // or directly send it to server
                           usersreference.child(online_user_id).child("device_token").setValue(deviceToken)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {

                                           Intent intent =new Intent(LoginActivity.this,Main2Activity.class);
                                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                           startActivity(intent);
                                           finish();
                                       }
                                   });
                       }
                   });

               }else
               {
                   Toast.makeText(LoginActivity.this, "Please Cjeck your email and password", Toast.LENGTH_SHORT).show();
               }
    progressDialog.dismiss();
           }
       });

        }

    }
}
