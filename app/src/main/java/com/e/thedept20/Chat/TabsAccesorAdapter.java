package com.e.thedept20.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccesorAdapter extends FragmentPagerAdapter
{
    public TabsAccesorAdapter(@NonNull FragmentManager fm, int behavior)
        {
        super(fm, behavior);
        }

    public TabsAccesorAdapter(FragmentManager supportFragmentManager)
        {
        super(supportFragmentManager);
        }

    @NonNull
    @Override
    public Fragment getItem(int position)
        {
        switch (position)
            {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;

            }
        }

    @Override
    public int getCount()
        {
        return 2;
        }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
        {
        switch (position)
            {
            case 0:
                return "Chats";

            case 1:
                return "Friends";
            default:
                return null;
            }
        }
}
