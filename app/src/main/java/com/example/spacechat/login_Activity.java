package com.example.spacechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class login_Activity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

     private Button Login_Button,Login_Phone_Btn;
        private TextView forgetpassword,newuser_register;
         private EditText login_Email,login_password;



    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth != null){
            startMainACtivity();
        }
    }

    private void startMainACtivity() {
        Intent MainActivityintent = new Intent(login_Activity.this,MainActivity.class);
        startActivity(MainActivityintent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
      Initilizer();
         newuser_register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent registerActivityintent = new Intent(login_Activity.this,register_Activity.class);
                 startActivity(registerActivityintent);
             }
         });
    }

    private void Initilizer() {
       Login_Button = findViewById(R.id.login_btn);
       Login_Phone_Btn = findViewById(R.id.Using_phone_Button);
        forgetpassword = findViewById(R.id.forget_password);
         newuser_register = findViewById(R.id.Register_New_Account);
          login_Email = findViewById(R.id.login_email);
          login_password = findViewById(R.id.login_password);
    }


}
