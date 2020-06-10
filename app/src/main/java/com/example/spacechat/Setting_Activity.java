package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class Setting_Activity extends AppCompatActivity {
     private FirebaseAuth firebaseAuth;
     private DatabaseReference databaseReference;
     private String currentuser;
     private Button UpdateAccountSetting;
     private EditText Username,Userstatus;
     private CircularImageView UserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);

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
            HashMap<String,String>profile_data = new HashMap<>();
            profile_data.put("uid",currentuser);
            profile_data.put("name",UserName);
            profile_data.put("status",UserStatus);

            databaseReference.child("Users").child(currentuser).setValue(profile_data).addOnCompleteListener(new OnCompleteListener<Void>() {
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
      }
     private void StartMainActivity(){
         Intent Mainintent = new Intent(Setting_Activity.this,MainActivity.class);
         Mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(Mainintent);
         finish();
     }
}
