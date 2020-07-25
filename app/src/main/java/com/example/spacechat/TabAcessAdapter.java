package com.example.spacechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/// This Class is Used For Access The TAbs OF Fragment In the Main UI of App like - "Chat","Contact","Chat Request" etc.

public class TabAcessAdapter extends FragmentPagerAdapter {
    public TabAcessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

/*      getItem Method has A return Type OF a Fragment and It will take The Position of Fragment  as an Integer
          Once It get The Position inside the Switch case It will Return A Specific Fragment(Requested by the User).
  * */
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

            case 3:
                    RequestFragment requestFragment = new RequestFragment();
                      return  requestFragment;

            default:
                      return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }


    // getPageTitle have Used To Set The Title of The Fragment Return by getItem.

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
            case 3:
                return "Chat Request";
            default:
                return null;
        }
        //return super.getPageTitle(position);
    }
}
