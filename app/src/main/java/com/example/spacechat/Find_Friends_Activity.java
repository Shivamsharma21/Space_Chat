package com.example.spacechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class Find_Friends_Activity extends AppCompatActivity {
       private Toolbar Find_Friends_toolbar;
       private RecyclerView FindFriends_RecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__friends_);
        Find_Friends_toolbar = findViewById(R.id.find_friends_app_bar);
        FindFriends_RecyclerView = findViewById(R.id.find_firends_recyclerview);
          FindFriends_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
          setSupportActionBar(Find_Friends_toolbar);
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          getSupportActionBar().setDisplayShowHomeEnabled(true);
          getSupportActionBar().setTitle("Find Friends");
    }
}