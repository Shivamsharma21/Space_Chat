package com.example.spacechat;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages>userList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserRef;

    public MessageAdapter(List<Messages>userList){
        this.userList = userList;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        CircularImageView ProfileImageView;
        TextView SenderMessage,ReceiverMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfileImageView = itemView.findViewById(R.id.message_profile_image);
            SenderMessage = itemView.findViewById(R.id.sender_message);
            ReceiverMessage = itemView.findViewById(R.id.receiver_message);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String senderMessageID = firebaseAuth.getCurrentUser().getUid();
        Messages messages = userList.get(position);
        String FromUserID = messages.getFrom();
  //      Log.d("FromUserID",FromUserID);
        String MessageType = messages.getType();
//        Log.d("MEssageType",MessageType);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FromUserID);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){
                    String ReceiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(ReceiverImage).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);
                Log.d("Image Data",ReceiverImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (MessageType.equals("text")){
            holder.ReceiverMessage.setVisibility(View.INVISIBLE);
            holder.ProfileImageView.setVisibility(View.INVISIBLE);
            holder.SenderMessage.setVisibility(View.INVISIBLE);
             if (FromUserID.equals(senderMessageID)){
                 holder.SenderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                 holder.SenderMessage.setTextColor(Color.WHITE);
                 holder.SenderMessage.setVisibility(View.VISIBLE);
                 holder.SenderMessage.setText(messages.getMessage());

             }else{

                 Log.d("Hey Its Reciver","Receiver Side View");
                 holder.ReceiverMessage.setBackgroundResource(R.drawable.reciever_message_layout);
         //        holder.SenderMessage.setVisibility(View.INVISIBLE);
                 holder.ReceiverMessage.setTextColor(Color.BLACK);
                 holder.ReceiverMessage.setVisibility(View.VISIBLE);
                 holder.ProfileImageView.setVisibility(View.VISIBLE);
                 holder.ReceiverMessage.setText(messages.getMessage());
             }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
