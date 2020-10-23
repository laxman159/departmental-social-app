package com.e.thedept20;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.e.thedept20.Notifications.NotificationsFragment;
import com.e.thedept20.Search.FindFriendsFragment;
import com.e.thedept20.Utils.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentPortalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentPortalFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int NOTICE_FRAGMENT = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentPortalFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentPortalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentPortalFragment newInstance(String param1, String param2)
        {
        StudentPortalFragment fragment = new StudentPortalFragment();
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
        view = inflater.inflate(R.layout.fragment_student_portal, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) view.findViewById(R.id.sp_searchuser);

        setupViewPager();


        return view;
        }

    private void setupViewPager()
        {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new RoutineFragment()); //index 0
        adapter.addFragment(new NoticeBoardFragment());//index 1
        adapter.addFragment(new MainFragment());

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sp_tabss);
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.getTabAt(1).setIcon(R.drawable.error1);;
        tabLayout.getTabAt(0).setText("Routine");
        tabLayout.getTabAt(2).setText("Todo's");
        }

    @Override
    public void onStart()
        {
        super.onStart();
        mViewPager.setCurrentItem(NOTICE_FRAGMENT);
        }
}