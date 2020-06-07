package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ViewPager mviewpager;
     private TabLayout mytablayout;
      private TabAcessAdapter tabAcessAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
         startLoginActivity();
        }
    }
    private void startLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this,login_Activity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
       Toolbar toolbar =  (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Space Chat");
  ///new code
          mviewpager = findViewById(R.id.main_tabs_page);
          tabAcessAdapter = new TabAcessAdapter(getSupportFragmentManager());
            mviewpager.setAdapter(tabAcessAdapter);

    mytablayout = findViewById(R.id.main_tabs);
    mytablayout.setupWithViewPager(mviewpager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option){
             firebaseAuth.signOut();
             startLoginActivity();
        }
        if (item.getItemId() == R.id.main_find_setting_option){
            //firebaseAuth.signOut();
        }
        if (item.getItemId() == R.id.main_find_friend_option){

        }
        return true;
    }
}
