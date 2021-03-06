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

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference Useref,Chatref,Contactref,Notificationref;
    private Button SendMessageButton,DeclineMessageButton;
    private TextView UserName,UserStatus;
    private CircularImageView UserProfileImage;
    private String reciverUserID,senderUserId,currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Useref = FirebaseDatabase.getInstance().getReference().child("Users");
        Chatref = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        Contactref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        Notificationref = FirebaseDatabase.getInstance().getReference().child("Notifications");
        firebaseAuth = FirebaseAuth.getInstance();
        reciverUserID = getIntent().getExtras().get("visit_User_Id").toString();

        senderUserId = firebaseAuth.getCurrentUser().getUid();

        SendMessageButton = findViewById(R.id.send_message_button);
        currentState ="new";
        UserName = findViewById(R.id.visit_user_name);
        UserStatus = findViewById(R.id.visit_user_status);
        UserProfileImage = findViewById(R.id.visit_profile_image);
        DeclineMessageButton = findViewById(R.id.decline_message_button);

        RetriveUserProfile();
       }
/*

     This Function Retrieve The Details Of the User from the Database,
     It WILL retrieve the Name,status,image Of the user and Set them
     to the User Profile

  */
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

  /*This Function is Maintain the Chat Request
    1 Recognize the Status OF request In terms OF(sent)and (receive) and Show The Details To user Screen
    2 After Verify if Receiver Accept The Request IT will Add the Sender Into Its
      Contact List.
    3 Also Help tho remove The Request Which is Send By the Receiver

    */
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
                                         if(request.equals("received")){
                                            currentState = "request_received";
                                    SendMessageButton.setText("Accept Request");
                                DeclineMessageButton.setVisibility(View.VISIBLE);
                              DeclineMessageButton.setEnabled(true);
                               DeclineMessageButton.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       RemoveChatRequest();
                                   }
                               });
                          }
                      }else{
                          Contactref.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(reciverUserID)){
                                                    currentState ="friends";
                                                        SendMessageButton.setText("Remove Person");

                                            }
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {

                              }
                          });
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
                                  if (currentState.equals("new")) {
                                        SendMessageButton.setEnabled(false);
                                   SendChatRequest();
                               }if(currentState.equals("request_sent")){
                            RemoveChatRequest();
                       }if(currentState.equals("request_received")){
                 AcceptChatRequest();
                             }
                                  if (currentState.equals("friends")){
                                         RemoveContact();
                                  }
                         }
                     });
                 }else{
                     SendMessageButton.setVisibility(View.INVISIBLE);
                 }
          }
          /////////////////////////////////////////////////////////////////////////////////////////////////////
   // This Function is USed to send The Chat Request to the new Other Users.
     void SendChatRequest(){
         Chatref.child(senderUserId).child(reciverUserID).child("request").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Chatref.child(reciverUserID).child(senderUserId).child("request").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               HashMap<String,String>NotificationMap = new HashMap<>();
                               NotificationMap.put("from",senderUserId);
                               NotificationMap.put("type","request");
          Notificationref.child(reciverUserID).push().setValue(NotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
             }
         });
     }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Remove THe Request Of Chat Which is Triggered by Sender
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
                        DeclineMessageButton.setEnabled(false);
                   DeclineMessageButton.setVisibility(View.INVISIBLE);

                             }
                           }
                       });
                 }
            }
        });
    }
/*
Used To accept the Request Send By The Sender User and add the user
in Contact List
  */
   void AcceptChatRequest(){
        Contactref.child(senderUserId).child(reciverUserID).setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   Contactref.child(reciverUserID).child(senderUserId).setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()){
                              Chatref.child(senderUserId).child(reciverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      if (task.isSuccessful()){
                                          Chatref.child(reciverUserID).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                            currentState ="friends";
                                      SendMessageButton.setText("Remove Chat");
                                  SendMessageButton.setEnabled(true);
                               DeclineMessageButton.setEnabled(false);
                           DeclineMessageButton.setVisibility(View.INVISIBLE);
                                              }
                                          });
                                        }
                                    }
                                });
                             }
                         }
                     });
                   }
               }
         });
     }

     // Used for Remove the contact from the Contact List//

     void RemoveContact(){

         Contactref.child(senderUserId).child(reciverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()){
                     Contactref.child(reciverUserID).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if (task.isSuccessful()){
                                 SendMessageButton.setEnabled(true);
                                 currentState ="new";
                                 SendMessageButton.setText("Send Chat Request");
                                 DeclineMessageButton.setEnabled(false);
                                 DeclineMessageButton.setVisibility(View.INVISIBLE);
                             }
                         }
                     });
                 }
             }
         });
     }
}
//////////////////////// Accept Chat Method Ends