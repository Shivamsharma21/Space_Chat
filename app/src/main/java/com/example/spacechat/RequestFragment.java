package com.example.spacechat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class RequestFragment extends Fragment {
         private DatabaseReference Chatref,UserRef,ContactRef;
         private FirebaseAuth firebaseAuth;
         private View RequestFregmentView;
         private RecyclerView RequestList;
         private String currentUID;
         public RequestFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestFregmentView = inflater.inflate(R.layout.fragment_request, container, false);

        RequestList = RequestFregmentView.findViewById(R.id.chat_request_list);
        RequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        Chatref = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        return  RequestFregmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contact>options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(Chatref.child(currentUID),Contact.class).build();//bug
        FirebaseRecyclerAdapter<Contact,RequestChatViewHolder>adapter = new FirebaseRecyclerAdapter<Contact, RequestChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestChatViewHolder holder, int position, @NonNull Contact model) {
                   holder.Acceptreq_btn.findViewById(R.id.accept_req_btn).setVisibility(View.VISIBLE);
                   holder.Declinereq_btn.findViewById(R.id.decline_req_btn).setVisibility(View.VISIBLE);
                    final String list_userId = getRef(position).getKey();
                     DatabaseReference getTyperef = getRef(position).child("request").getRef();
                    getTyperef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             if (dataSnapshot.exists()){
                                  String type = dataSnapshot.getValue().toString();
                                       if (type.equals("received")){
                                     UserRef.child(list_userId).addValueEventListener(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.hasChild("image")){

                                               final String userpicture = dataSnapshot.child("image").getValue().toString();
                                             Picasso.get().load(userpicture).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);

                                           }
                                               final String username = dataSnapshot.child("name").getValue().toString();
                                           final String userstatus = dataSnapshot.child("status").getValue().toString();
                                               holder.Username.setText(username);
                                               holder.Userstatus.setText(userstatus);

                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence[] options = new CharSequence[]{
                                                            "Accept",
                                                            "Decline"
                                                    };
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle( username+" Chat Request");
                                                //Builder has 2 conditions Accept and Decline the User Request those Value are
                                                    //Recognized by the '0' and '1'
                                      builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   if (which == 0){
                                                          //if it zero That mean user IS Select the Accept the Request
                                                          ContactRef.child(currentUID).child(list_userId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                   if (task.isSuccessful()){
                                                              ContactRef.child(list_userId).child(currentUID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                      @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                      Chatref.child(currentUID).child(list_userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                          if (task.isSuccessful()){
                                                                              Chatref.child(list_userId).child(currentUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                  @Override
                                                                                  public void onComplete(@NonNull Task<Void> task) {
                                                                                      if (task.isSuccessful()){
                                                                                          Toast.makeText(getContext(), "New Contacts Saved", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                  }
                                                                              });
                                                                          }
                                                                          }
                                                                      });
                                                                          }
                                                                      });
                                                                  }
                                                                }
                                                            });
                                                          }if (which == 1){
                                                          // if its 1 that's mean user want to decline the Request
                                                     Chatref.child(currentUID).child(list_userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Void> task) {
                                                             if (task.isSuccessful()){
                                                                 Chatref.child(list_userId).child(currentUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                         if (task.isSuccessful()){
                                                                             Toast.makeText(getContext(), "Contacts Deleted", Toast.LENGTH_SHORT).show();
                                                                         }
                                                                     }
                                                                 });
                                                             }
                                                         }
                                                     });
                                                           }
                                                       }
                                                   });
                                                 builder.show();
                                                }
                                            });
                                         }
                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) {
                                         }
                                     });
                                           /* here we are checking If the Request IS already is 'sent'
                                                to the User the we are Stop the User For Sending the request
                                                again to that one.
                                            */
                                 }else if(type.equals("sent")){
                                           Button Request_sent_btn = holder.itemView.findViewById(R.id.accept_req_btn);
                                           Request_sent_btn.setText("Request Send");
                                           holder.itemView.findViewById(R.id.decline_req_btn).setVisibility(View.INVISIBLE);
                                           UserRef.child(list_userId).addValueEventListener(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                   if (dataSnapshot.hasChild("image")){
                                                       final String userpicture = dataSnapshot.child("image").getValue().toString();
                                                       Picasso.get().load(userpicture).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);

                                                   }
                                                   final String username = dataSnapshot.child("name").getValue().toString();
                                                   final String userstatus = dataSnapshot.child("status").getValue().toString();
                                                   holder.Username.setText(username);
                                                   holder.Userstatus.setText(userstatus);

                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           CharSequence[] options = new CharSequence[]{
                                                                   "Decline"
                                                           };
                                                           AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                           builder.setTitle( username+" Send Request");
                                                           builder.setItems(options, new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int which) {
                                                                   if (which == 0){
                                                                       Chatref.child(currentUID).child(list_userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if (task.isSuccessful()){
                                                                                   Chatref.child(list_userId).child(currentUID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                           if (task.isSuccessful()){
                                                                                               Toast.makeText(getContext(), "Request Deleted", Toast.LENGTH_SHORT).show();
                                                                                           }
                                                                                       }
                                                                                   });
                                                                               }
                                                                           }
                                                                       });
                                                                   }
                                                               }
                                                           });
                                                           builder.show();
                                                       }
                                                   });
                                               }
                                               @Override
                                               public void onCancelled(@NonNull DatabaseError databaseError) {
                                               }
                                           });//this is Uerrefend
                                       }//this is the end of received
                             }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

            }

            @NonNull
            @Override
            public RequestChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getContext()).inflate(R.layout.users_display_friends,parent,false);
                RequestChatViewHolder requestChatViewHolder = new RequestChatViewHolder(view);
                return requestChatViewHolder;
            }
        };

        RequestList.setAdapter(adapter);
        adapter.startListening();

         }

         ////                    CLASS -->>> Request Chat Holder ////
     private static class RequestChatViewHolder extends RecyclerView.ViewHolder{

          private TextView Username,Userstatus,Acceptreq_btn,Declinereq_btn;
          private CircularImageView ProfileImageView;

         public RequestChatViewHolder(@NonNull View itemView) {
             super(itemView);
            Username = itemView.findViewById(R.id.user_name);
            Userstatus = itemView.findViewById(R.id.user_status);
            ProfileImageView = itemView.findViewById(R.id.users_friends_profile);
            Acceptreq_btn = itemView.findViewById(R.id.accept_req_btn);
            Declinereq_btn = itemView.findViewById(R.id.decline_req_btn);
         }
     }
}