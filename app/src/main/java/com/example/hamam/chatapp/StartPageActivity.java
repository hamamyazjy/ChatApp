package com.example.hamam.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {


    Button NeedNewAccountButton;
    Button AlredyHaveAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);


  NeedNewAccountButton = findViewById(R.id.need_account_button);
  AlredyHaveAccountButton =findViewById(R.id.already_account_button);




  NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Intent i =new Intent(StartPageActivity.this,RegisterActivity.class);
          startActivity(i);
      }
  });

  AlredyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          Intent i =new Intent(StartPageActivity.this,LoginActivity.class);
          startActivity(i);

      }
  });
    }
}
