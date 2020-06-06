package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register_Activity extends AppCompatActivity {
     private FirebaseAuth firebaseAuth;
     private Button register_button;
     private EditText Register_Email,Register_Password;
     private TextView Already_Have_Account;
     private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        Initilizer();

    Already_Have_Account.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent Already_have_Account_intent = new Intent(register_Activity.this,login_Activity.class);
            startActivity(Already_have_Account_intent);
        }
    });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount(){
          progressDialog.setTitle("Creating a new account");
          progressDialog.setMessage("Please Wait");
          progressDialog.show();
             String email = Register_Email.getText().toString();
             String password = Register_Password.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   progressDialog.dismiss();
                   Toast.makeText(register_Activity.this, "Account is Created", Toast.LENGTH_SHORT).show();
                    SendUserToLoginActivity();
               }else{
                   progressDialog.dismiss();
                   String message = task.getException().toString();
                   Toast.makeText(register_Activity.this, "Error "+message, Toast.LENGTH_SHORT).show();
               }
            }
        });

    }
    private void Initilizer() {
      register_button = findViewById(R.id.signup_btn);
      Register_Email = findViewById(R.id.signup_email);
      Register_Password = findViewById(R.id.signup_password);
      Already_Have_Account = findViewById(R.id.already_have_account);
      firebaseAuth = FirebaseAuth.getInstance();
      progressDialog = new ProgressDialog(this);
    }
      private void SendUserToLoginActivity(){
        Intent loginintent = new Intent(register_Activity.this,login_Activity.class);
         startActivity(loginintent);
      }
}
