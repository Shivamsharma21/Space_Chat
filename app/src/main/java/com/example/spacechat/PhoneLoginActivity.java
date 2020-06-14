package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private Button Verifycode,Send_verifycode;
    private EditText Phonenumber_input,Code_input;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String TAG;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        Initilized();
        Send_verifycode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String PhoneNumber = Phonenumber_input.getText().toString();

                if(TextUtils.isEmpty(PhoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Please Enter A valid Phone Number", Toast.LENGTH_SHORT).show();
                        }else{
                         loadingbar.setTitle("Please Wait");
                         loadingbar.setMessage("While We Sending The Code To Your Device");
                         loadingbar.setCanceledOnTouchOutside(false);
                         loadingbar.show();
                         PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            PhoneNumber,
                            60,TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                 signInWithPhoneAuthCredential(phoneAuthCredential);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                   loadingbar.dismiss();
                                    Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();

                                    Send_verifycode.setVisibility(View.INVISIBLE);
                                    Phonenumber_input.setVisibility(View.INVISIBLE);

                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId,
                                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                    // The SMS verification code has been sent to the provided phone number, we
                                    // now need to ask the user to enter the code and then construct a credential
                                    // by combining the code with a verification ID.
                                    Log.d(TAG, "onCodeSent:" + verificationId);

                                    // Save verification ID and resending token so we can use them later
                                    mVerificationId = verificationId;
                                    mResendToken = token;
                                    Toast.makeText(PhoneLoginActivity.this, "Code Is Been Send To Your Account", Toast.LENGTH_SHORT).show();

                                    Phonenumber_input.setVisibility(View.INVISIBLE);
                                    Send_verifycode.setVisibility(View.INVISIBLE);
                                    Verifycode.setVisibility(View.VISIBLE);
                                    Code_input.setVisibility(View.VISIBLE);
                                            loadingbar.dismiss();
                                                                    }
                            }
                    );
                }

            }
        });

           Verifycode.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                       String VerifyingCode = Code_input.getText().toString();
                         if (TextUtils.isEmpty(VerifyingCode)){
                             Toast.makeText(PhoneLoginActivity.this, "Enter the Verify Code", Toast.LENGTH_SHORT).show();
                         }else{
                             loadingbar.setTitle("Please Wait");
                             loadingbar.setMessage("While We Verify Your Device");
                             loadingbar.setCanceledOnTouchOutside(false);
                             loadingbar.show();
                             PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, VerifyingCode);
                              signInWithPhoneAuthCredential(credential);
                         }
               }
           });

    }

    private void Initilized() {
      Verifycode = findViewById(R.id.verify_button);
      Send_verifycode = findViewById(R.id.send_verification_code_button);
      Phonenumber_input = findViewById(R.id.phonenumber);
      Code_input = findViewById(R.id.verification_code_input);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PhoneLoginActivity.this, "Sign In Complete", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                            SendToMainActivity();
                        }
                else {
                        }
                    }
                });
    }

  private void SendToMainActivity(){
      Intent mainintent = new Intent(PhoneLoginActivity.this,MainActivity.class);
          mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
          startActivity(mainintent);
          finish();
  }

}
