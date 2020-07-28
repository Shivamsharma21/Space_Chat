package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class Group_Chat_Activity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference Groupref,GroupNameref,groupMessageKeyRef;
    private ImageButton imageButton;
    private ScrollView scrollView;
    private Toolbar toolbar;
    private EditText user_message_text;
    private TextView displaymessage,Group_Chat_Time;
    private String groupName,currentUserID, UserName,currentDate,currentTime,username;

    @Override
    protected void onStart() {
        super.onStart();
        GroupNameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                             if (dataSnapshot.exists()){
                                 DisplayMessages(dataSnapshot);
                             }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat_);

        groupName = getIntent().getExtras().get("group name").toString();
        GroupNameref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();

        Initilized();
        GroupInfo();
         imageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SendInfoToDatabase();
                 user_message_text.setText("");
                 scrollView.fullScroll(ScrollView.FOCUS_DOWN);

             }
         });
    }

    private void Initilized() {
        imageButton = findViewById(R.id.Sending_image);
        scrollView  = findViewById(R.id.scroll_View);
        toolbar = findViewById(R.id.group_chat_layout);
        user_message_text = findViewById(R.id.input_group_message);
        displaymessage = findViewById(R.id.textview);
        Group_Chat_Time = findViewById(R.id.group_chat_time);
        setSupportActionBar(toolbar);
         getSupportActionBar().setTitle(groupName);

    }
    private void GroupInfo(){
        Groupref = FirebaseDatabase.getInstance().getReference().child("Users");
          Groupref.child(currentUserID).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()) {
                      username = dataSnapshot.child("name").getValue().toString();
                  }
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }
void SendInfoToDatabase(){

      String messageKey = GroupNameref.push().getKey();

        String Message = user_message_text.getText().toString();

            if(TextUtils.isEmpty(Message)){
                imageButton.setEnabled(false);
            }else{
                Calendar calendarfordate = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                currentDate = simpleDateFormat.format(calendarfordate.getTime());

                Calendar calenderfortime = Calendar.getInstance();
                SimpleDateFormat simpletimeFormat = new SimpleDateFormat("hh:mm a");
                currentTime = simpletimeFormat.format(calenderfortime.getTime());

                HashMap<String,Object> groupmessageKey = new HashMap<>();
                GroupNameref.updateChildren(groupmessageKey);

                groupMessageKeyRef = GroupNameref.child(messageKey);

                HashMap<String,Object>MessageInfoMap = new HashMap<>();
                MessageInfoMap.put("Name ",username);
                MessageInfoMap.put("Date ",currentDate);
                MessageInfoMap.put("Time ",currentTime);
                MessageInfoMap.put("Message ",Message);

                groupMessageKeyRef.updateChildren(MessageInfoMap);
            }
     }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
       Iterator iterator = dataSnapshot.getChildren().iterator();
           while(iterator.hasNext()){
               String ChatDate = (String) ((DataSnapshot)iterator.next()).getValue();
               String ChatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
               String ChatName= (String) ((DataSnapshot)iterator.next()).getValue();
               String ChatTime = (String) ((DataSnapshot)iterator.next()).getValue();
               displaymessage.append(ChatMessage+"\n"+ChatTime+"\n"+ChatName+"\n");
               scrollView.fullScroll(ScrollView.FOCUS_DOWN);

           }
    }
}