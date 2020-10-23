package com.e.thedept20.Home;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.e.thedept20.AttendenceFragment;
import com.e.thedept20.Chat.ChatFragment;
import com.e.thedept20.LoginRegister.Register2Activity;
import com.e.thedept20.MainFragment;
import com.e.thedept20.Notifications.Token;
import com.e.thedept20.Search.FindFriendsFragment;
import com.e.thedept20.R;
import com.e.thedept20.AddPost.ShareActivity;
import com.e.thedept20.Account.UserProfileFragment;
import com.e.thedept20.SearchRequestFragment;
import com.e.thedept20.StudentPortalFragment;
import com.e.thedept20.StudentsProtalActivity;
import com.e.thedept20.Utils.OnlineOfflineStatus;
import com.e.thedept20.Utils.Permissions;
import com.e.thedept20.Utils.SectionsPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class DashboardActivity extends AppCompatActivity


{

    private static final int HOME_FRAGMENT = 1;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    final private int Gallery_Pick = 1;
    ImageView imageView;
    ImageButton imageButton;
    //ActionBar actionBar;
    Toolbar toolbar1;
    String mUID;
    FirebaseUser user;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    private DatabaseReference UserRef;
    private String currentuser;
    private StorageReference userRoutineRef;
    private DatabaseReference settingref;
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout, mRelativeLayout1;
    //private OnlineOfflineStatus onlineOfflineStatus = new OnlineOfflineStatus();
    private BottomNavigationViewEx.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationViewEx.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                    //handel item click
                    switch (item.getItemId())
                        {
                        case R.id.nav_home:
                            mRelativeLayout.setVisibility(View.VISIBLE);
                            HomeFragment fragment1 = new HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_findfriends:

                            mRelativeLayout.setVisibility(View.INVISIBLE);
                            SearchRequestFragment fragment2 = new SearchRequestFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;

                        case R.id.nav_addpost:
                            ShareActivity shareActivity = new ShareActivity();
                            mRelativeLayout.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(getApplicationContext(), ShareActivity.class);
                            startActivity(intent);

                            return true;
                        case R.id.nav_notifications:
                            mRelativeLayout.setVisibility(View.INVISIBLE);
                            // Intent intent1=new Intent(getApplicationContext(), StudentsProtalActivity.class);
                            // startActivity(intent1);
                            StudentPortalFragment fragment4 = new StudentPortalFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.commit();
                            return true;
                        case R.id.nav_userprofile:
                            mRelativeLayout.setVisibility(View.INVISIBLE);
                            UserProfileFragment fragment5 = new UserProfileFragment();
                            FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                            ft5.replace(R.id.content, fragment5, "");
                            ft5.commit();
                            return true;


                        }
                    return false;
                    }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //toolbar1=findViewById(R.id.custom_instagramprofile_toolbar);

        //imageView=findViewById(R.id.message_iv);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.content);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rellayoutparent);
        //mRelativeLayout1 = (RelativeLayout) findViewById(R.id.rellayoutparent1);


        //init
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        mUID = firebaseAuth.getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRoutineRef = FirebaseStorage.getInstance().getReference().child("Routine");

        verifyPermissions(Permissions.PERMISSIONS);

        settingref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser);
        //bottom navigation

        BottomNavigationViewEx navigationView = findViewById(R.id.navigation);
        navigationView.enableItemShiftingMode(false);
        navigationView.enableAnimation(true);
        navigationView.enableShiftingMode(true);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);



//     HomeFragment fragment1=new HomeFragment();
//     FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
//      ft1.replace(R.id.content,fragment1,"");
//      ft1.commit();
        CheckUserExistance();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        setupViewPager();
        }

    @Override
    protected void onResume()
        {
        CheckUserExistance();
        super.onResume();

        }

    private void setupViewPager()
        {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new AttendenceFragment()); //index 2
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        tabLayout.getTabAt(2).setIcon(R.drawable.attendance_icbig);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home11);
        tabLayout.getTabAt(0).setIcon(R.drawable.send);
        }

    public void verifyPermissions(String[] permissions)
        {


        ActivityCompat.requestPermissions(
                DashboardActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST

        );

        }

    private void CheckUserExistance()
        {
        final String CurrentUser_id = firebaseAuth.getCurrentUser().getUid();
        user = firebaseAuth.getCurrentUser();
        UserRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (!dataSnapshot.child(CurrentUser_id).hasChild("Fullname"))
                    {
                    SendUserToSetupActivity();
                    } else
                    {
                    mUID = user.getUid();
                    SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Current_USERID", mUID);
                    editor.apply();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {
                }
        });

        }

    @Override
    protected void onStart()
        {
        super.onStart();

        CheckUserExistance();
       // mRelativeLayout.setVisibility(View.INVISIBLE);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        //onlineOfflineStatus.UpdateUserStatus("online");
        }

    private void SendUserToSetupActivity()
        {
        Intent setupIntent = new Intent(getApplicationContext(), Register2Activity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
        }


    @Override
    public void onBackPressed()
        {
        super.onBackPressed();
        finish();
        }


    //inflate option menu


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//        {
//        getMenuInflater().inflate(R.menu.menu_main,menu);
//        return super.onCreateOptionsMenu(menu);
//        }
//    //handle menu item click

    public void updateToken(String token)
        {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) ;
        {


        Uri ImageUri = data.getData();
        CropImage.activity(ImageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
        ;
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
                {
                // Intent SetupIntent = new Intent(SettingProfileActivity.this, SettingProfileActivity.class);
                //startActivity(SetupIntent);


                Uri resultUri = result.getUri();
                Intent SetupIntent = new Intent(getApplicationContext(), StudentsProtalActivity.class);
                //SetupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(SetupIntent);

                final StorageReference filepath = userRoutineRef.child(currentuser + ".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                                {
                                final String downloadURL = uri.toString();
                                settingref.child("Routine").setValue(downloadURL)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                                {
                                                if (task.isSuccessful())
                                                    {
//                                                   Intent SetupIntent = new Intent(SettingProfileActivity.this, SettingsActivity.class);
//                                                   startActivity(SetupIntent);
                                                    //Toast.makeText(SettingActivity.this, "Profile image stored", Toast.LENGTH_SHORT).show();
                                                    //loadingbar.dismiss();
                                                    } else
                                                    {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(getApplicationContext(), "Error occured" + message, Toast.LENGTH_SHORT).show();
                                                    //loadingbar.dismiss();
                                                    }
                                                }

                                        });
                                }
                        });
                        }
                });

                } else
                {
                Toast.makeText(getApplicationContext(), "Error occured:image cant be cropped try again", Toast.LENGTH_SHORT).show();

                }
            }
        }

    @Override
    protected void onStop()
        {
        super.onStop();
       //onlineOfflineStatus.UpdateUserStatus("offline");
        }

    @Override
    protected void onRestart()
        {
        super.onRestart();
        //onlineOfflineStatus.UpdateUserStatus("online");
        }

    @Override
    protected void onDestroy()
        {
        super.onDestroy();
        //onlineOfflineStatus.UpdateUserStatus("offline");
        }
}