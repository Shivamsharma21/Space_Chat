package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAcessAdapter extends FragmentPagerAdapter {
    public TabAcessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                     ChatFragment chatFragment = new ChatFragment();
                      return chatFragment;
            case 1:
                    GroupsFragment groupsFragment = new GroupsFragment();
                     return groupsFragment;
            case 2:
                    ContactFragment contactFragment = new ContactFragment();
                     return contactFragment;
            default:
                     return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Contracts";
            default:
                return null;
        }
        //return super.getPageTitle(position);
    }
}
