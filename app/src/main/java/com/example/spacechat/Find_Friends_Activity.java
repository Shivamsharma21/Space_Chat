package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Find_Friends_Activity extends AppCompatActivity {
       private Toolbar Find_Friends_toolbar;
       private RecyclerView FindFriends_RecyclerView;
       private DatabaseReference UserRef;
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friends_);

         UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Find_Friends_toolbar = findViewById(R.id.find_friends_app_bar);
        FindFriends_RecyclerView = findViewById(R.id.find_firends_recyclerview);
          FindFriends_RecyclerView.setLayoutManager(new LinearLayoutManager(this));

          setSupportActionBar(Find_Friends_toolbar);
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          getSupportActionBar().setDisplayShowHomeEnabled(true);
          getSupportActionBar().setTitle("Find Friends");



       }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact>options = new FirebaseRecyclerOptions.Builder<Contact>().setQuery(UserRef,Contact.class).build();

        FirebaseRecyclerAdapter<Contact, FindFreindsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contact, FindFreindsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFreindsViewHolder holder, final int position, @NonNull Contact model) {
                        holder.username.setText(model.getName());
                        holder.status.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileimage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 String visit_User_Id = getRef(position).getKey();
                                Intent profileIntent = new Intent(Find_Friends_Activity.this,ProfileActivity.class);
                                profileIntent.putExtra("visit_User_Id",visit_User_Id);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFreindsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_friends,parent,false);
                        FindFreindsViewHolder viewHolder = new FindFreindsViewHolder(view);
                        return viewHolder;
                    }
                };

            FindFriends_RecyclerView.setAdapter(adapter);
            adapter.startListening();

       }
    public static class FindFreindsViewHolder extends RecyclerView.ViewHolder{
        TextView username,status;
        CircularImageView profileimage;
        public FindFreindsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            status = itemView.findViewById(R.id.user_status);
            profileimage = itemView.findViewById(R.id.users_friends_profile);
        }
    }


}
