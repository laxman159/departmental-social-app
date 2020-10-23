package com.e.thedept20.Chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.e.thedept20.R;
import com.e.thedept20.Utils.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Objects;

public class ChatUserActivity extends AppCompatActivity
{

    private Toolbar mtoolbar;
    private ViewPager userChatViewPager;
    private TabLayout chatTabLayout;
    private TabsAccesorAdapter mytabaccesorApapter;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentuser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        mtoolbar = findViewById(R.id.bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Messages");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // userChatViewPager = findViewById(R.id.user_chat_tabs_pager);
        //mytabaccesorApapter = new TabsAccesorAdapter(getSupportFragmentManager());
        //userChatViewPager.setAdapter(mytabaccesorApapter);
        //chatTabLayout = findViewById(R.id.chat_user_tablayout);
        //chatTabLayout.setupWithViewPager(userChatViewPager);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();
        currentuser = mAuth.getCurrentUser().getUid();
        setupViewPager();


        }

    private void setupViewPager()
        {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment()); //index 0
        adapter.addFragment(new FriendsFragment());
        userChatViewPager = findViewById(R.id.user_chat_tabs_pager);//index 1
        userChatViewPager.setAdapter(adapter);
        userChatViewPager.setOffscreenPageLimit(2);
        chatTabLayout = findViewById(R.id.chat_user_tablayout);
        chatTabLayout.setupWithViewPager(userChatViewPager);

        chatTabLayout.getTabAt(0).setText(R.string.ChatsFragmentName);
        chatTabLayout.getTabAt(1).setText(R.string.FriendsFragmentName);


        }


}