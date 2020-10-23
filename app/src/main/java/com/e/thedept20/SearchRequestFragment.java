package com.e.thedept20;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.e.thedept20.Chat.ChatFragment;
import com.e.thedept20.Home.HomeFragment;
import com.e.thedept20.Notifications.NotificationsFragment;
import com.e.thedept20.Search.FindFriendsFragment;
import com.e.thedept20.Utils.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchRequestFragment extends Fragment
{
    private static final int HOME_FRAGMENT = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout, mRelativeLayout1;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchRequestFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchRequestFragment newInstance(String param1, String param2)
        {
        SearchRequestFragment fragment = new SearchRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
        }

    @Override
    public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_request, container, false);


        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.fl_searchuser);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.rellayoutparent2);
        setupViewPager();
        return view;

        }

    private void setupViewPager()
        {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FindFriendsFragment()); //index 0
        adapter.addFragment(new NotificationsFragment()); //index 1

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabss);
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.getTabAt(1).setText("Request");
        tabLayout.getTabAt(0).setText("Search");
        }
}