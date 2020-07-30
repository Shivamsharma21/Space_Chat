package com.example.spacechat;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        TextView SenderMessage,ReceiverMessage,ChatTime;
        ImageView MessageSenderImageView ,MessageReceiverImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            ProfileImageView = itemView.findViewById(R.id.message_profile_image);
            SenderMessage = itemView.findViewById(R.id.sender_message);
            ReceiverMessage = itemView.findViewById(R.id.receiver_message);
            MessageSenderImageView = itemView.findViewById(R.id.message_Sender_imageView);
            MessageReceiverImageView = itemView.findViewById(R.id.message_Receiver_imageView);
            ChatTime = itemView.findViewById(R.id.chat_time);
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
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        String senderMessageID = firebaseAuth.getCurrentUser().getUid();
        Messages messages = userList.get(position);
        String FromUserID = messages.getFrom();
        String MessageType = messages.getType();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FromUserID);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")){
                    String ReceiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(ReceiverImage).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.ProfileImageView.setVisibility(View.GONE);
        holder.ChatTime.setVisibility(View.GONE);
        holder.SenderMessage.setVisibility(View.GONE);
        holder.ReceiverMessage.setVisibility(View.GONE);

        holder.MessageReceiverImageView.setVisibility(View.GONE);
        holder.MessageSenderImageView.setVisibility(View.GONE);

        if (MessageType.equals("text")){
            holder.ReceiverMessage.setVisibility(View.INVISIBLE);
            holder.ProfileImageView.setVisibility(View.INVISIBLE);
            holder.SenderMessage.setVisibility(View.INVISIBLE);
             if (FromUserID.equals(senderMessageID)){
                 holder.SenderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                 holder.SenderMessage.setTextColor(Color.WHITE);
                 holder.SenderMessage.setVisibility(View.VISIBLE);
                 holder.SenderMessage.setText(messages.getMessage());
                 holder.ChatTime.setVisibility(View.VISIBLE);
                 holder.ChatTime.setText(messages.getTime());
             }else{

                 holder.ReceiverMessage.setBackgroundResource(R.drawable.reciever_message_layout);
                 holder.ReceiverMessage.setTextColor(Color.BLACK);
                 holder.ReceiverMessage.setVisibility(View.VISIBLE);
                 holder.ProfileImageView.setVisibility(View.VISIBLE);
                 holder.ReceiverMessage.setText(messages.getMessage());
                 holder.ChatTime.setVisibility(View.VISIBLE);
                 holder.ChatTime.setText(messages.getTime());
             }
        }else if(MessageType.equals("Images")){
              if (FromUserID.equals(senderMessageID)){
                holder.MessageSenderImageView.setVisibility(View.VISIBLE);
                holder.MessageReceiverImageView.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.MessageSenderImageView);
              }else{
                  holder.MessageSenderImageView.setVisibility(View.INVISIBLE);
                  holder.MessageReceiverImageView.setVisibility(View.VISIBLE);
                  Picasso.get().load(messages.getMessage()).into(holder.MessageReceiverImageView);
              }
        }else if(MessageType.equals("pdf") || MessageType.equals("docx")){
            if (FromUserID.equals(senderMessageID)){
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/spacedata-96f20.appspot.com/o/Image%20Files%2Fsend_file_icon.png?alt=media&token=485800bc-1e06-4293-acc2-c854a6d0f743")
                             .into(holder.MessageSenderImageView);
                holder.MessageSenderImageView.setVisibility(View.VISIBLE);
                holder.MessageReceiverImageView.setVisibility(View.INVISIBLE);

                  holder.itemView.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userList.get(position).getMessage()));
                          holder.itemView.getContext().startActivity(intent);
                      }
                  });

            }else{
                holder.MessageSenderImageView.setVisibility(View.INVISIBLE);
                holder.MessageReceiverImageView.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/spacedata-96f20.appspot.com/o/Image%20Files%2Fsend_file_icon.png?alt=media&token=485800bc-1e06-4293-acc2-c854a6d0f743")
                        .into(holder.MessageReceiverImageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
