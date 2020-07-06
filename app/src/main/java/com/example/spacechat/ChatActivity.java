package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

     LinearLayoutManager linearLayoutManager;
    private final List<Messages>messagesList = new ArrayList<>();
     RecyclerView UsermessageList;
     MessageAdapter messageAdapter;
     DatabaseReference MessageRef,RootRef;
     FirebaseAuth firebaseAuth;
     Toolbar ChatToolbar;
     String messageReciverId,messageRecivername,MessageReciverImage,currentUserID;
     CircularImageView UserImage;
     TextView Username,Userlastseen;
     EditText InputMessage;
     ImageButton SendMessageButton;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         RootRef = FirebaseDatabase.getInstance().getReference();
         MessageRef = FirebaseDatabase.getInstance().getReference();
         firebaseAuth = FirebaseAuth.getInstance();
         currentUserID = firebaseAuth.getCurrentUser().getUid();


        messageReciverId = getIntent().getExtras().get("Visit_User_ID").toString();
        messageRecivername = getIntent().getExtras().get("Visit_user_name").toString();
        MessageReciverImage = getIntent().getExtras().get("Visit_user_Image").toString();

        Toast.makeText(this, messageReciverId, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, messageRecivername, Toast.LENGTH_SHORT).show();

       Initilize();

       Username.setText(messageRecivername);
         Picasso.get().load(MessageReciverImage).placeholder(R.drawable.profile_image).into(UserImage);

       SendMessageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                 SendMessage();
           }
       });
     }

    void Initilize(){

        ChatToolbar = findViewById(R.id.chat_bar_layout);

        setSupportActionBar(ChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Actionbarview = layoutInflater.inflate(R.layout.chat_app_bar_layout,null);

        actionBar.setCustomView(Actionbarview);

        UserImage = findViewById(R.id.chat_firends_profile);
        Username = findViewById(R.id.chat_friends_name);
        Userlastseen = findViewById(R.id.chat_firends_last_scan);
        InputMessage  = findViewById(R.id.input_chat_text);
        SendMessageButton = findViewById(R.id.send_private_message_btn);

        messageAdapter = new MessageAdapter(messagesList);//bug

        UsermessageList = findViewById(R.id.chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
         UsermessageList.setLayoutManager(linearLayoutManager);
         UsermessageList.setAdapter(messageAdapter) ;
     }

    @Override
    protected void onStart() {
        super.onStart();
         RootRef.child("Message").child(currentUserID).child(messageReciverId).addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 Messages message = dataSnapshot.getValue(Messages.class);
                 messagesList.add(message);
                 messageAdapter.notifyDataSetChanged();
                 UsermessageList.smoothScrollToPosition(UsermessageList.getAdapter().getItemCount());
             }

             @Override
             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }

    void SendMessage(){
       String text = InputMessage.getText().toString();
       if (TextUtils.isEmpty(text)){
           SendMessageButton.setEnabled(false);
       }else{
           String messageSenderRef = "Message" + "/"+currentUserID +"/"+ messageReciverId;
           String messageReciverref = "Message" +"/"+ messageReciverId +"/"+ currentUserID;

           DatabaseReference messageKeyRef = RootRef.child("Message").child(messageSenderRef).child(messageReciverref).push();

           String messagePushId = messageKeyRef.getKey();

           HashMap messageTextBody = new HashMap();
           messageTextBody.put("Message",text);
           messageTextBody.put("type","text");
           messageTextBody.put("from",currentUserID);

           HashMap messageBodyDetails = new HashMap();
           messageBodyDetails.put(messageSenderRef+ "/" + messagePushId,messageTextBody);
           messageBodyDetails.put(messageReciverref+ "/" +messagePushId,messageTextBody);


           RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
               @Override
               public void onComplete(@NonNull Task task) {
                   if (task.isSuccessful()){
                       Toast.makeText(ChatActivity.this, "Message is Send", Toast.LENGTH_SHORT).show();
                       InputMessage.setText("");
                   }
               }
           });

       }

   }
}