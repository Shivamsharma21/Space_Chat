package com.example.spacechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class login_Activity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null){
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
    }
}
