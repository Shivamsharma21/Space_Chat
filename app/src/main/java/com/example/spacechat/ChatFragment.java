package com.example.spacechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

     private DatabaseReference Contactref,Usersref;
     private FirebaseAuth firebaseAuth;
     private View view;
     private RecyclerView Private_chatView;
     private String currentUserID;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        Contactref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        Private_chatView = view.findViewById(R.id.private_chat_view);
        Private_chatView.setLayoutManager(new LinearLayoutManager(getContext()));

         return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact>options = new FirebaseRecyclerOptions.Builder<Contact>().
                                                  setQuery(Contactref,Contact.class).build();
        FirebaseRecyclerAdapter<Contact,ChatsViewHolder>adapter = new FirebaseRecyclerAdapter<Contact, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contact model) {
                final String UserId = getRef(position).getKey();
                final String[] userImage = {"default_image"};
                  Usersref.child(UserId).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if (dataSnapshot.exists()){
                              if (dataSnapshot.hasChild("image")){
                                    userImage[0] = dataSnapshot.child("image").getValue().toString();
                                  Picasso.get().load(userImage[0]).into(holder.profileImageView);
                              }
                              String userstatus =dataSnapshot.child("status").getValue().toString();
                              final String username = dataSnapshot.child("name").getValue().toString();

                             // holder.UserStaus.setText(userstatus);
                              holder.Username.setText(username);

                              if(dataSnapshot.child("userState").hasChild("state")){
                                 String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                  String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                  String date = dataSnapshot.child("userState").child("date").getValue().toString();

                                    if (state.equals("online")){
                                    holder.UserStaus.setText("online");
                                    }
                                    else if (state.equals("offline")){
                                    holder.UserStaus.setText("Last seen:"+time+" "+date);
                                    }
                                   }else{
                                  holder.UserStaus.setText("offline");
                              }

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent ChatIntent = new Intent(getActivity(),ChatActivity.class);
                                          ChatIntent.putExtra("Visit_User_ID",UserId);
                                          ChatIntent.putExtra("Visit_user_name",username);
                                          ChatIntent.putExtra("Visit_user_Image", userImage[0]);
                                        startActivity(ChatIntent);
                                    }
                                });
                          }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getContext()).inflate(R.layout.users_display_friends,parent,false);
                ChatsViewHolder chatsViewHolder = new ChatsViewHolder(view);
                return chatsViewHolder;
            }
        };
             Private_chatView.setAdapter(adapter);
             adapter.startListening();
    }

   static class ChatsViewHolder extends RecyclerView.ViewHolder{
       CircularImageView profileImageView;
       TextView Username,UserStaus;

       public ChatsViewHolder(@NonNull View itemView) {
           super(itemView);
          profileImageView =itemView.findViewById(R.id.users_friends_profile);
          Username = itemView.findViewById(R.id.user_name);
          UserStaus = itemView.findViewById(R.id.user_status);
       }
   }
}
