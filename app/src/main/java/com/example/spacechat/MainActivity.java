package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ViewPager mviewpager;
     private TabLayout mytablayout;
      private TabAcessAdapter tabAcessAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
         startLoginActivity();
        }
        else{
                VerifyUserExistince();
        }
    }

    private void VerifyUserExistince() {
                   String UserID = firebaseAuth.getCurrentUser().getUid();
                   databaseReference.child("Users").child(UserID).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if ((dataSnapshot.child("name").exists())){
                             Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                         }else{
                             StartToSettingActivity();
                         }
                       }
                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });


    }
    private void StartToSettingActivity(){
        Intent settingintent = new Intent(MainActivity.this,Setting_Activity.class);
        settingintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingintent);
        finish();
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
            startSettingActivity();
        }
        if (item.getItemId() == R.id.main_find_friend_option){
            startFindFriendsActivity();
        }
        if(item.getItemId() == R.id.main_Group_chat_option){
            RequestGroupChat();
        }
        return true;
    }

    private void RequestGroupChat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
         builder.setTitle("Creating A new Group");
         final EditText gropinfo = new EditText(MainActivity.this);
         builder.setView(gropinfo);
         builder.setPositiveButton("Create :", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
               String groupname = gropinfo.getText().toString();
                 if (TextUtils.isEmpty(groupname)){
                     Toast.makeText(MainActivity.this, "Enter Valid Group NAme", Toast.LENGTH_SHORT).show();
                 }else {
                       CreateNewGroup(groupname);
                 }
             }
         });
        builder.setNegativeButton("Cancel :", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
            }
        });
      builder.show();
    }

    private void CreateNewGroup(String name) {
       databaseReference.child("Groups").child(name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
             if (task.isSuccessful()){
                 Toast.makeText(MainActivity.this, "Group Is Created", Toast.LENGTH_SHORT).show();
             }    else{
                 Toast.makeText(MainActivity.this, "Error Wile Creating The Group", Toast.LENGTH_SHORT).show();
             }
           }
       });
    }

    private void startSettingActivity() {

        Intent settingintent = new Intent(MainActivity.this,Setting_Activity.class);
        startActivity(settingintent);
    }
    private void startFindFriendsActivity() {
        Intent FindFrIntent = new Intent(MainActivity.this,Find_Friends_Activity.class);
        startActivity(FindFrIntent);
    }

}
