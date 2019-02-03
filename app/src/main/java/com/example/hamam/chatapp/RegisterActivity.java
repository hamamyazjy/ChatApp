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
import android.widget.ProgressBar;
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

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mauth;


    private DatabaseReference storeUserDefaultDataRefernce;
    String deviceToken;

    private EditText registername,registeremail,registerpassword;
    Button button;
    private ProgressDialog loadingBar;


    Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar =findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registername =findViewById(R.id.register_name);
        registeremail =findViewById(R.id.register_email);
        registerpassword=findViewById(R.id.register_password);
        button =findViewById(R.id.create_Account);
        mauth=FirebaseAuth.getInstance();
          loadingBar =new ProgressDialog(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               final  String name = registername.getText().toString();
                final  String email=registeremail.getText().toString();
                final  String password  =registerpassword.getText().toString();

                RegisterAcount(name,email,password);



            }
        });    }

    private void RegisterAcount(final String name, String email, String password) {

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }  if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }else{

            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait, we are creating account ");

            loadingBar.show();

            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){

                String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                          deviceToken = instanceIdResult.getToken();
                        // Do whatever you want with your token now
                        // i.e. store it on SharedPreferences or DB
                        // or directly send it to server
                    }
                });


                String current_UserId = mauth.getCurrentUser().getUid();
                storeUserDefaultDataRefernce= FirebaseDatabase.getInstance().getReference().child("Users").child(current_UserId);
                storeUserDefaultDataRefernce.child("user_name").setValue(name);
                storeUserDefaultDataRefernce.child("user_status").setValue("Hey there, I am using chat App");
                storeUserDefaultDataRefernce.child("device_token").setValue(deviceToken);
                storeUserDefaultDataRefernce.child("user_image").setValue("default_profile");
                storeUserDefaultDataRefernce.child("user_thumb_image").setValue("default_image")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()){
                               Intent intent =new Intent(RegisterActivity.this,Main2Activity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(intent);
                               finish();

                           }
                            }
                        });

            }else{

                Toast.makeText(RegisterActivity.this, "Error Try Again..", Toast.LENGTH_SHORT).show();
            }

           loadingBar.dismiss();
                }
            });

        }

    }
}
