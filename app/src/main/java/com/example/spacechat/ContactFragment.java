package com.example.spacechat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ContactFragment extends Fragment {

 private View ContactView;
 private RecyclerView ContactList;
 private FirebaseAuth firebaseAuth;
 private DatabaseReference Contactref,UsersRef;
 private String currentUserId;
    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactView = inflater.inflate(R.layout.fragment_contact, container, false);
        ContactList = ContactView.findViewById(R.id.contacts_list);
        ContactList.setLayoutManager(new LinearLayoutManager(getContext()));
        Contactref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        return ContactView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(Contactref,Contact.class).build();
         FirebaseRecyclerAdapter<Contact,ContactViewHolder>adapter = new FirebaseRecyclerAdapter<Contact, ContactViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull Contact model) {

                 String usersID = getRef(position).getKey();

                 UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if (dataSnapshot.hasChild("image")){

                              String username = dataSnapshot.child("name").getValue().toString();
                              String userstatus = dataSnapshot.child("status").getValue().toString();
                              String userPicture = dataSnapshot.child("image").getValue().toString();

                              holder.Username.setText(username);
                              holder.Userstatus.setText(userstatus);
                              Picasso.get().load(userPicture).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);

                          }else{
                              String username = dataSnapshot.child("name").getValue().toString();
                              String userstatus = dataSnapshot.child("status").getValue().toString();

                              holder.Username.setText(username);
                              holder.Userstatus.setText(userstatus);

                          }
                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                     }
                 });

                 holder.Username.setText(model.getName());
                 holder.Userstatus.setText(model.getStatus());
                 Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.ProfileImageView);
             }
             @NonNull
             @Override
             public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_friends,parent,false);
                 ContactViewHolder contactViewHolder = new ContactViewHolder(view);
                 return contactViewHolder;
             }
         };
              ContactList.setAdapter(adapter);
              adapter.startListening();
    }
////End of On start////////////////////////////////////////////////////////////////////////////////

     public static class ContactViewHolder extends RecyclerView.ViewHolder{
                 TextView Username,Userstatus;
                 CircularImageView ProfileImageView;
         public ContactViewHolder(@NonNull View itemView) {
             super(itemView);
             Username = itemView.findViewById(R.id.user_name);
             Userstatus = itemView.findViewById(R.id.user_status);
             ProfileImageView = itemView.findViewById(R.id.users_friends_profile);
         }
     }
}
