package com.example.spacechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager mviewpager;
     private TabLayout mytablayout;
      private TabAcessAdapter tabAcessAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
