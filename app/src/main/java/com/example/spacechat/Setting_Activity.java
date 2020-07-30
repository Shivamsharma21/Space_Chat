package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class Setting_Activity extends AppCompatActivity {
     private StorageReference UserImageRef;
     private FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;
     private String currentuser;
     private Button UpdateAccountSetting;
     private EditText Username,Userstatus;
     private CircularImageView UserProfile;
     private ProgressDialog loadingbar;
     private Toolbar setting_Toolbar;
     private static final  int Galarypic =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
       setting_Toolbar = findViewById(R.id.user_setting_profile_activity); /////Fix later name
       setSupportActionBar(setting_Toolbar);
       getSupportActionBar().setDisplayShowCustomEnabled(true);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setTitle("Setting");

        firebaseAuth = FirebaseAuth.getInstance();
       databaseReference = FirebaseDatabase.getInstance().getReference();
       currentuser = firebaseAuth.getCurrentUser().getUid();
       RetriveUserInfo();
       InitiliazeField();

       UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               UpdateProfile();
           }
       });
      UserProfile.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent gallaryintent = new Intent();
              gallaryintent.setAction(Intent.ACTION_PICK);
              gallaryintent.setType("image/*");
              startActivityForResult(Intent.createChooser(gallaryintent,"Shivam Graber"),Galarypic);
          }
      });
    }

    private void RetriveUserInfo() {
          databaseReference.child("Users").child(currentuser).addValueEventListener(new ValueEventListener() {
              @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image") ){
                         String retriveusername  = dataSnapshot.child("name").getValue().toString();
                            String retriveuserimage  = dataSnapshot.child("image").getValue().toString();
                              String retriveprofilestatus  = dataSnapshot.child("status").getValue().toString();

                                Username.setText(retriveusername);
                            Userstatus.setText(retriveprofilestatus);
                          Picasso.get().load(retriveuserimage).into(UserProfile); //Set the image to image View From Database

                    }else if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status")){
                          String retriveusername  = dataSnapshot.child("name").getValue().toString();
                            String retriveprofilestatus  = dataSnapshot.child("status").getValue().toString();

                            Username.setText(retriveusername);
                            Userstatus.setText(retriveprofilestatus);

                  }else{
                      Toast.makeText(Setting_Activity.this, "Update Profile", Toast.LENGTH_SHORT).show();
                  }
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }

    private void UpdateProfile() {

        String UserName = Username.getText().toString();
        String UserStatus = Userstatus.getText().toString();

        if (TextUtils.isEmpty(UserName)){
            Username.setError("User Name Can't be Empty");
        }
        if (TextUtils.isEmpty(UserStatus)){
            Userstatus.setError("Status Can't be Empty");
        }
        else{
            HashMap<String,Object>profile_data = new HashMap<>();
            profile_data.put("uid",currentuser);
            profile_data.put("name",UserName);
            profile_data.put("status",UserStatus);

            databaseReference.child("Users").child(currentuser).updateChildren(profile_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()){
                      Toast.makeText(Setting_Activity.this, "Data Update to database", Toast.LENGTH_SHORT).show();
                        StartMainActivity();
                  }else{
                    String message = task.getException().getMessage().toString();
                      Toast.makeText(Setting_Activity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                  }
                }
            });

        }
    }

    private void InitiliazeField(){
        UpdateAccountSetting = findViewById(R.id.update_setting_btn);
        Username = findViewById(R.id.user_name_update);
        Userstatus = findViewById(R.id.Status_update);
        UserProfile = findViewById(R.id.profile_image_);
        UserImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
         loadingbar = new ProgressDialog(this);
    }
     private void StartMainActivity(){
         Intent Mainintent = new Intent(Setting_Activity.this,MainActivity.class);
         Mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(Mainintent);
         finish();
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

               if (requestCode ==Galarypic && resultCode== Activity.RESULT_OK && data !=null && data.getData() != null){
                   Uri ImageUri = data.getData();

                   // start picker to get image for cropping and then use the image in cropping activity
                   CropImage.activity(ImageUri)
                           .setGuidelines(CropImageView.Guidelines.ON)
                           .setAspectRatio(1,1)
                           .start(this);

               }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (resultCode == RESULT_OK) {
            loadingbar.setTitle("Please Wait");
            loadingbar.setMessage("While We Are Uploading Your Profile Picture");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            Uri resultUri = result.getUri();
            StorageReference Filepath = UserImageRef.child(currentuser+".jpg");
             Filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                           @Override
                           public void onComplete(@NonNull Task<Uri> task) {
                                final String DownloadUrl = task.getResult().toString();
                               Log.d("Download url", DownloadUrl);
                               databaseReference.child("Users").child(currentuser).child("image").setValue(DownloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(Setting_Activity.this, "Image Uploaded To DataBase ", Toast.LENGTH_SHORT).show();
                                       }else{
                                           String message = task.getException().toString();
                                           Toast.makeText(Setting_Activity.this, "Error While Uploading image To Database"+message, Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                           }
                       });
                        loadingbar.dismiss();
                        Toast.makeText(Setting_Activity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        loadingbar.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(Setting_Activity.this, "Error is Occur During the Event"+message, Toast.LENGTH_SHORT).show();
                    }
                 }
             });
             }
        }
    }
}
