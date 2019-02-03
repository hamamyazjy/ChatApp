package com.example.hamam.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Thread thread = new Thread(){
            @Override
            public void run() {


                try{
                    sleep(3000);

                }catch (Exception e ){
                   e.printStackTrace();
                }finally {
                    Intent intent =new Intent(MainActivity.this,Main2Activity.class);
                    startActivity(intent);
                    finish();

                }
            }



        };
        thread.start();



    }
}
