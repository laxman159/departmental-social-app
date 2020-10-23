package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.e.thedept20.Account.UserProfileFragment;
import com.e.thedept20.AddPost.NextActivity;
import com.e.thedept20.AddPost.ShareActivity;
import com.e.thedept20.Chat.ChatFragment;
import com.e.thedept20.Chat.FriendsFragment;
import com.e.thedept20.Chat.TabsAccesorAdapter;
import com.e.thedept20.Home.HomeFragment;
import com.e.thedept20.Notifications.NotificationsFragment;
import com.e.thedept20.Utils.SectionPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.e.thedept20.SettingProfileActivity.Gallery_Pick;

public class StudentsProtalActivity extends AppCompatActivity
{

    public static final int RESULT = -1;
    private static final int ROUTINE_FRAGMENT = 1;
    private Toolbar mtoolbar;
    private ViewPager userChatViewPager;
    private TabLayout chatTabLayout;
    private TabsAccesorAdapter mytabaccesorApapter;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentuser;
    private StorageReference userRoutineRef;
    private DatabaseReference settingref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_protal);
        mtoolbar = findViewById(R.id._studentprotal_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Student Portal");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();
        currentuser = mAuth.getCurrentUser().getUid();
        setupViewPager();

        userRoutineRef = FirebaseStorage.getInstance().getReference().child("Routine");
        //imageView = view.findViewById(R.id.imageView2);

        settingref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser);


        }

    private void setupViewPager()
        {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainFragment()); //index 0
        adapter.addFragment(new RoutineFragment());
        adapter.addFragment(new AttendenceFragment());
        userChatViewPager = findViewById(R.id.studentportal_user_chat_tabs_pager);//index 1
        userChatViewPager.setAdapter(adapter);
        userChatViewPager.setOffscreenPageLimit(2);
        chatTabLayout = findViewById(R.id.studentportal_chat_user_tablayout);
        chatTabLayout.setupWithViewPager(userChatViewPager);

        chatTabLayout.getTabAt(1).setText("Routine");
        chatTabLayout.getTabAt(2).setText("Attendence");
        chatTabLayout.getTabAt(0).setText("Todo");


        }


    @Override
    protected void onStart()
        {
        super.onStart();
        userChatViewPager.setCurrentItem(ROUTINE_FRAGMENT);
        }
}