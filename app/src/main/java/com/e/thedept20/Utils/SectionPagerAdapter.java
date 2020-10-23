package com.e.thedept20.Utils;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionPagerAdapter extends FragmentPagerAdapter
{

    private List<Fragment> fragmentList = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fm)
        {
        super(fm);
        }

    @Override
    public Fragment getItem(int position)
        {
        return fragmentList.get(position);
        }

    @Override
    public int getCount()
        {
        return fragmentList.size();
        }

    public void addFragment(Fragment fragment)
        {
        fragmentList.add(fragment);
        }


}
