package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference Useref,Chatref;
    private Button SendMessageButton;
    private TextView UserName,UserStatus;
    private CircularImageView UserProfileImage;
    private String reciverUserID,senderUserId,currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        currentState ="new";
        Useref = FirebaseDatabase.getInstance().getReference().child("Users");
        Chatref = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        firebaseAuth = FirebaseAuth.getInstance();
        reciverUserID = getIntent().getExtras().get("visit_User_Id").toString();

        senderUserId = firebaseAuth.getCurrentUser().getUid();

        SendMessageButton = findViewById(R.id.send_message_button);
        UserName = findViewById(R.id.visit_user_name);
        UserStatus = findViewById(R.id.visit_user_status);
        UserProfileImage = findViewById(R.id.visit_profile_image);

        RetriveUserProfile();
       }

  public void RetriveUserProfile(){
          Useref.child(reciverUserID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               if ((dataSnapshot.exists() && dataSnapshot.hasChild("image"))) {

                   String name = dataSnapshot.child("name").getValue().toString();
                   String status = dataSnapshot.child("status").getValue().toString();
                   String image = dataSnapshot.child("image").getValue().toString();

                     UserName.setText(name);
                     UserStatus.setText(status);
                   Picasso.get().load(image).placeholder(R.drawable.profile_image).into(UserProfileImage);
                   ManageChatRequest();
               }else{

                   String name = dataSnapshot.child("name").getValue().toString();
                   String status = dataSnapshot.child("status").getValue().toString();

                   UserName.setText(name);
                   UserStatus.setText(status);
                   ManageChatRequest();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
  }
          void ManageChatRequest(){
              Chatref.child(senderUserId).addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if (dataSnapshot.hasChild(reciverUserID)) {
                          String request = dataSnapshot.child(reciverUserID).child("request").getValue().toString();

                          if (request.equals("sent")) {
                              currentState = "request_sent";
                              SendMessageButton.setText("Cancel Request");
                          }
                      }
                  }
                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });
                 if (!senderUserId.equals(reciverUserID)){

                     SendMessageButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             if (currentState.equals("new"))
                             {
                                 SendMessageButton.setEnabled(false);
                                 SendChatRequest();
                             }if(currentState.equals("request_sent")){
                                 RemoveChatRequest();
                             }
                         }
                     });
                 }else{
                     SendMessageButton.setVisibility(View.INVISIBLE);
                 }
          }
     void SendChatRequest(){
         Chatref.child(senderUserId).child(reciverUserID).child("request").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Chatref.child(reciverUserID).child(senderUserId).child("request").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                                SendMessageButton.setEnabled(true);
                               currentState="request_sent";
                               SendMessageButton.setText("Cancel Request");
                           }
                         }
                     });
                 }
             }
         });
     }
    void RemoveChatRequest(){
        Chatref.child(senderUserId).child(reciverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       Chatref.child(reciverUserID).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()){
                                   SendMessageButton.setEnabled(true);
                                   currentState ="new";
                                 SendMessageButton.setText("Send Chat Request");
                             }
                           }
                       });
                 }
            }
        });
    }
}