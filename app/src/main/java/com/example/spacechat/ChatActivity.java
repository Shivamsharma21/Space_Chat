package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
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
     ImageButton SendMessageButton,SendFilesButton;
     ImageView GreenOnlineIndicator;
     String saveCurrentDate,saveCurrentTime,checker="",myUrl;
     Uri fileUri;
     StorageTask Uploadtask;


     @RequiresApi(api = Build.VERSION_CODES.N)
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
         GreenOnlineIndicator = findViewById(R.id.user_online_status);
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
      DisplayUserLastSeen();
     }
    void DisplayUserLastSeen(){
       RootRef.child("Users").child(messageReciverId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("userState").hasChild("state")){
                   String state = dataSnapshot.child("userState").child("state").getValue().toString();
                   String time = dataSnapshot.child("userState").child("time").getValue().toString();
                   String date = dataSnapshot.child("userState").child("date").getValue().toString();

                   if (state.equals("online")){
                       Userlastseen.setText("online");
                      // if(GreenOnlineIndicator !=null){
                      // GreenOnlineIndicator = findViewById(R.id.user_online_status);
                     //  GreenOnlineIndicator.setVisibility(View.VISIBLE);
                      // }
                   }
                   else if (state.equals("offline")){
                       Userlastseen.setText("Last seen"+time+" "+date);
                    //   if(GreenOnlineIndicator != null){
                    //   Log.d("Offline","state Block");
                  //     GreenOnlineIndicator = findViewById(R.id.user_online_status);
                //       GreenOnlineIndicator.setVisibility(View.INVISIBLE);
                      //  }
                   }
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yy");
        saveCurrentDate = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = simpleTimeFormat.format(calendar.getTime());
        SendFilesButton = findViewById(R.id.send_files_btn);

        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Images",
                        "Pdf Files",
                        "Ms World"
                };
                final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select The File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            checker = "Images";
                            Intent imageIntent = new Intent();
                            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                            imageIntent.setType("image/*");
                            startActivityIfNeeded(imageIntent.createChooser(imageIntent, "Select Image"), 21);
                        }

                        if (which == 1) {
                            checker = "Pdf Files";
                            Intent imageIntent = new Intent();
                            imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                            imageIntent.setType("application/pdf");
                            startActivityIfNeeded(imageIntent.createChooser(imageIntent, "Select Pdf File"), 21);

                        }

                        if (which == 2) {
                            checker = "Ms World";
                        }
                    }
                });
                builder.show();
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
           if (requestCode == 21 && resultCode == RESULT_OK && data != null && data.getData() !=null ){
               fileUri = data.getData();
                if (!checker.equals("Images")){

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Pdf Files");
                    final String messageSenderRef = "Message" + "/"+currentUserID +"/"+ messageReciverId;
                    final String messageReciverref = "Message" +"/"+ messageReciverId +"/"+ currentUserID;

                    final DatabaseReference messageKeyRef = RootRef.child("Message").child(messageSenderRef).child(messageReciverref).push();

                    final String messagePushId = messageKeyRef.getKey();

                    final StorageReference filePath = storageReference.child(messagePushId +"."+"pdf");

                     filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             if (task.isSuccessful()){

                                 HashMap messageTextBody = new HashMap();
                                 messageTextBody.put("Message",task.getResult().getStorage().getDownloadUrl().toString());
                                 messageTextBody.put("type",checker);
                                 messageTextBody.put("from",currentUserID);
                                 messageTextBody.put("to",messageReciverId);
                                 messageTextBody.put("messageID",messagePushId);
                                 messageTextBody.put("date",saveCurrentDate);
                                 messageTextBody.put("time",saveCurrentTime);

                                 HashMap messageBodyDetails = new HashMap();
                                 messageBodyDetails.put(messageSenderRef+ "/" + messagePushId,messageTextBody);
                                 messageBodyDetails.put(messageReciverref+ "/" +messagePushId,messageTextBody);

                    RootRef.updateChildren(messageTextBody);

                             }
                         }
                     });
                }
                else if(checker.equals("Images")){
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                    final String messageSenderRef = "Message" + "/"+currentUserID +"/"+ messageReciverId;
                    final String messageReciverref = "Message" +"/"+ messageReciverId +"/"+ currentUserID;

                    final DatabaseReference messageKeyRef = RootRef.child("Message").child(messageSenderRef).child(messageReciverref).push();

                   final String messagePushId = messageKeyRef.getKey();

                    final StorageReference filePath = storageReference.child(messagePushId +"."+"jpg");
                      Uploadtask = filePath.putFile(fileUri);
                      Uploadtask.continueWithTask(new Continuation() {
                          @Override
                          public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                              return filePath.getDownloadUrl();
                          }
                      }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                          @Override
                          public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()){
                                    Uri imageUrL = task.getResult();
                                         myUrl = imageUrL.toString();
                                    String messagePushId = messageKeyRef.getKey();

                                    HashMap messageTextBody = new HashMap();
                                    messageTextBody.put("Message",myUrl);
                                    messageTextBody.put("type",checker);
                                    messageTextBody.put("from",currentUserID);
                                    messageTextBody.put("to",messageReciverId);
                                    messageTextBody.put("messageID",messagePushId);
                                    messageTextBody.put("date",saveCurrentDate);
                                    messageTextBody.put("time",saveCurrentTime);

                                    HashMap messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef+ "/" + messagePushId,messageTextBody);
                                    messageBodyDetails.put(messageReciverref+ "/" +messagePushId,messageTextBody);


                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ChatActivity.this, "Message is Sent Successfully", Toast.LENGTH_SHORT).show();
                                                InputMessage.setText("");
                                            }
                                        }
                                    });

                                }
                          }
                      });

                }else{
                    Toast.makeText(this, "Didn't select anything", Toast.LENGTH_SHORT).show();
                }
           }
     }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
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
           messageTextBody.put("to",messageReciverId);
           messageTextBody.put("messageID",messagePushId);
           messageTextBody.put("date",saveCurrentDate);
           messageTextBody.put("time",saveCurrentTime);

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