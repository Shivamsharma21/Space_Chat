package com.example.spacechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.mikhaellopez.circularimageview.CircularImageView;

public class Setting_Activity extends AppCompatActivity {
     private Button UpdateAccountSetting;
     private EditText Username,Userstatus;
     private CircularImageView UserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);

       InitiliazeField();

    }
      private void InitiliazeField(){
        UpdateAccountSetting = findViewById(R.id.update_setting_btn);
        Username = findViewById(R.id.user_name_update);
        Userstatus = findViewById(R.id.Status_update);
        UserProfile = findViewById(R.id.profile_image_);
      }
}
