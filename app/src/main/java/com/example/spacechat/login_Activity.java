package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_Activity extends AppCompatActivity {
   FirebaseAuth firebaseAuth;

     private Button Login_Button,Login_Phone_Btn;
        private TextView forgetpassword,newuser_register;
         private EditText login_Email,login_password;
          private ProgressDialog progressDialog;

    private void startMainACtivity() {
        Intent MainActivityintent = new Intent(login_Activity.this,MainActivity.class);
        startActivity(MainActivityintent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        firebaseAuth = FirebaseAuth.getInstance();

        Initilizer();
         newuser_register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent registerActivityintent = new Intent(login_Activity.this,register_Activity.class);
                 startActivity(registerActivityintent);
             }
         });

         Login_Button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AllowUsertoLogin();
             }
         });

        Login_Phone_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneLoginintent = new Intent(login_Activity.this,PhoneLoginActivity.class);
                startActivity(phoneLoginintent);
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
            progressDialog = new ProgressDialog(this);
    }



    private void AllowUsertoLogin(){

        progressDialog.setTitle("Sign In to Your account");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        String email = login_Email.getText().toString();
        String password = login_password.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(login_Activity.this, "Sign In Sucessfull", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();

                }else{
                    progressDialog.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(login_Activity.this, "Error "+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
   private void SendUserToMainActivity(){
            Intent sendtoMainActivity = new Intent(login_Activity.this,MainActivity.class);
            sendtoMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(sendtoMainActivity);
            finish();
   }
}
