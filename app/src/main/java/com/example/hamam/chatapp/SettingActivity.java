package com.example.hamam.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {


    private CircleImageView settingDisplayProfileImage;
    private TextView settingDisplayName,settingDisplayStatus;
    private Button settingsChangeProfileImage,settingChangeStatus;
   private DatabaseReference   getUserDataReference;
   private StorageReference storeProfileImagestorageRef;
   private StorageReference thumbImageRef;

 private ProgressDialog dialog;
    private final static int Gallery_pick=1;

   private FirebaseAuth  mAuth;
    Bitmap thumb_Bitmap = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        dialog =new ProgressDialog(this);
        mAuth =FirebaseAuth.getInstance();
      String online_user_id =mAuth.getCurrentUser().getUid();
        getUserDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
      getUserDataReference.keepSynced(true);

        storeProfileImagestorageRef = FirebaseStorage.getInstance().getReference().child("profile_Image");

        thumbImageRef =FirebaseStorage.getInstance().getReference().child("Thumb_images");



        settingDisplayProfileImage =findViewById(R.id.setting_profile);
        settingDisplayName =findViewById(R.id.setting_username);
        settingDisplayStatus =findViewById(R.id.setting_user_status);
        settingsChangeProfileImage =findViewById(R.id.setting_change_image);
        settingChangeStatus =findViewById(R.id.setting_change_status);


       getUserDataReference.addValueEventListener(new ValueEventListener() {

           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      String name = dataSnapshot.child("user_name").getValue().toString();
      String status =dataSnapshot.child("user_status").getValue().toString();
      String image =dataSnapshot.child("user_image").getValue().toString();
      final String thumb_image =dataSnapshot.child("user_thumb_image").getValue().toString();

      settingDisplayName.setText(name);
      settingDisplayStatus.setText(status);

      if (!image.equals("default_profile")){

          Picasso.with(SettingActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).
                  placeholder(R.drawable.default_profile).into(settingDisplayProfileImage, new Callback() {
              @Override
              public void onSuccess() {

              }

              @Override
              public void onError() {
                  Picasso.with(SettingActivity.this).load(thumb_image).placeholder(R.drawable.default_profile).into(settingDisplayProfileImage);

              }
          });


      }


           }


           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });



        settingsChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent =new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);


            }

        });
        settingChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String old_status =settingDisplayStatus.getText().toString();
                Intent intent =new Intent(SettingActivity.this,StatusActivity.class);
                 intent.putExtra("user_status",old_status);

                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_pick && resultCode==RESULT_OK && data != null){
            Uri imageUri =data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                dialog.setTitle("Updating Profile Image");
                dialog.setMessage("Please Wait, Update Image Profile");
                dialog.show();
                Uri resultUri = result.getUri();
                File thumb_filePathUri =new File(resultUri.getPath());

               String user_id =mAuth.getCurrentUser().getUid();

           try{
           thumb_Bitmap =    new Compressor(this)
                       .setMaxWidth(200)
                       .setMaxHeight(200)
                       .setQuality(50).compressToBitmap(thumb_filePathUri);


           }catch (IOException e){
               e.printStackTrace();
           }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
           final byte[]  thumb_byte= byteArrayOutputStream.toByteArray();

             StorageReference filePath =storeProfileImagestorageRef.child(user_id+".jpg");
             final StorageReference thumb_filepath =thumbImageRef.child(user_id+".jpg");



             filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

              if (task.isSuccessful()){
                  Toast.makeText(SettingActivity.this, "Saving your image in Data Base", Toast.LENGTH_SHORT).show();


                final String downloadUri =task.getResult().getDownloadUrl().toString();
                UploadTask uploadTask =thumb_filepath.putBytes(thumb_byte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                        String thumb_downloadUri =thumb_task.getResult().getDownloadUrl().toString();
                        if (thumb_task.isSuccessful()){
                            Map update_user_data =new HashMap();
                            update_user_data.put("user_image",downloadUri);
                            update_user_data.put("user_thumb_image",thumb_downloadUri);

                            getUserDataReference.updateChildren(update_user_data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Toast.makeText(SettingActivity.this, "Image Updated Success", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dialog.dismiss();
                        }

                    }
                });


              }else
              {
                  Toast.makeText(SettingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                  dialog.dismiss();
              }
        }
    });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
