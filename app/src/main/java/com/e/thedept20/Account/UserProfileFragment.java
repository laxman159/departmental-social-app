package com.e.thedept20.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.GridImageApapter;
import com.e.thedept20.Adapters.Posts;
import com.e.thedept20.Chat.ChatUserActivity;
import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.LoginRegister.StartActivity;
import com.e.thedept20.R;
import com.e.thedept20.SettingsActivity;
import com.e.thedept20.Utils.UniversalImageLoader;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context getmContext;
    private FirebaseAuth mAuth;
    private DatabaseReference profileuserRef, friendsRef, allPostRef, Requestref;
    private String currentuserID, currentuserID2, PostKey;
    private Context mContext;
    private TextView mPosts, mFriends, mFriendsText, mRequest, mDisplayName, mUsername, usname, mDescription, mEditprofile, collefeName, collegerollNum, collegeDesignation, collegeEmaail;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView gridView;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private int countfriends = 0, countpost = 0, countrequest = 0;
    private ImageButton mButton;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2)
        {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
        }


    @Override
    public void onStop()
        {
        initImageLoader();
        setProfileDetails();
        GridSetup();
        // ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onStop();
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
        //  ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_instagram_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        profileuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        allPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Requestref = FirebaseDatabase.getInstance().getReference().child("Friendsrequests");
        currentuserID = mAuth.getCurrentUser().getUid();
        collefeName = view.findViewById(R.id.college_name);
        collegerollNum = view.findViewById(R.id.college_rollnumber);
        collegeDesignation = view.findViewById(R.id.college_designation);
        collegeEmaail = view.findViewById(R.id.college_email);
        usname = view.findViewById(R.id.display_username);

        getmContext = getContext();


        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.custom_instagramprofile_name);
        mDescription = view.findViewById(R.id.description);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPosts = view.findViewById(R.id.tvPosts);
        mFriends = view.findViewById(R.id.tvFollowers);
        mRequest = view.findViewById(R.id.tvFollowing);
        mEditprofile = view.findViewById(R.id.textEditProfile);
        mEditprofile.setVisibility(View.INVISIBLE);
        gridView = view.findViewById(R.id.gridView);
        mFriendsText = view.findViewById(R.id.textFollowers);
        toolbar = view.findViewById(R.id.tttttt);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        setHasOptionsMenu(true);
        GridSetup();
        // ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // mContext =getContext();
        initImageLoader();
        setProfileDetails();
        //GridSetup();


        friendsRef.child(currentuserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    countfriends = (int) dataSnapshot.getChildrenCount();
                    mFriends.setText(Integer.toString(countfriends));
                    if (Integer.parseInt(mFriends.getText().toString()) > 0)
                        {
                        mFriends.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                                {
                                SendUserToFriendsActivity();
                                }
                        });
                        }

                    } else
                    {
                    mFriends.setText("0");
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        Requestref.child(currentuserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    countrequest = (int) dataSnapshot.getChildrenCount();
                    mRequest.setText(Integer.toString(countrequest));
                    if (Integer.parseInt(mRequest.getText().toString()) > 0)
                        {
                        mRequest.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                                {
                                SendUserToNotificationActivity();

                                }
                        });

                        }

                    } else
                    {
                    mRequest.setText("0");
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });


        return view;
        }

    private void initImageLoader()
        {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getContext());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
        }

    private void setProfileDetails()
        {
        profileuserRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                String s_myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                String s_username = dataSnapshot.child("Username").getValue().toString();
                String s_fullname = dataSnapshot.child("Fullname").getValue().toString();
                String s_profileStatus = dataSnapshot.child("Status").getValue().toString();
                String s_collegeName = dataSnapshot.child("College name").getValue().toString();
                String s_collegeRollNum = dataSnapshot.child("College roll number").getValue().toString();
                String s_collegeDesignation = dataSnapshot.child("Designation").getValue().toString();
                String s_college_email = dataSnapshot.child("Email").getValue().toString();
                // String s_college_email1=dataSnapshot.child("email").getValue().toString();
                Glide.with(getmContext).load(s_myProfileImage).into(mProfilePhoto);
               // Picasso.get().load(s_myProfileImage).placeholder(R.drawable.user).into(mProfilePhoto);
                mUsername.setText(s_username);
                mDisplayName.setText(s_fullname);
                mDescription.setText(s_profileStatus);
                collefeName.setText(s_collegeName);
                collegeDesignation.setText(s_collegeDesignation);
                collegerollNum.setText(s_collegeRollNum);
                collegeEmaail.setText(s_college_email);
                usname.setText("@" + s_username);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });


        }


    private void GridSetup()
        {
       /* ArrayList<String> imageURls=new ArrayList<>();
        imageURls.add("https://free-images.com/sm/e1b0/calidris_alba_bird_nature.jpg");
        imageURls.add("https://free-images.com/sm/fc7f/adorable_animal_background_164489.jpg");
        imageURls.add("https://free-images.com/sm/6126/japanese_squirrel_squirrel_63152.jpg");
        setupGridView(imageURls);*/

        final ArrayList<Posts> photos = new ArrayList<>();
        final ArrayList<String> key = new ArrayList<>();
        final Query mypostsquery = allPostRef.orderByChild("Uid")
                .startAt(currentuserID).endAt(currentuserID + "\uf8ff");
        mypostsquery.addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                countpost = (int) dataSnapshot.getChildrenCount();
                mPosts.setText(Integer.toString(countpost));

                for (DataSnapshot singlesnapshot : dataSnapshot.getChildren())
                    {
                    photos.add(singlesnapshot.getValue(Posts.class));
                    key.add(singlesnapshot.getKey());
                    }
                int gridwidth = 100; //(int) (getResources().getDisplayMetrics().widthPixels);
                int imagewidth = gridwidth / 3;
                gridView.setColumnWidth(imagewidth);

                final ArrayList<String> imageurls = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++)
                    {
                    imageurls.add(photos.get(i).getPostImage());

                    }
                GridImageApapter apapter = new GridImageApapter(getmContext, R.layout.layout_grid_imageview, "", imageurls);
                gridView.setAdapter(apapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                        //Posts posts=(Posts)parent.getItemAtPosition(position);

                        Intent intent = new Intent(getActivity(), ViewAllPostActivity.class);
                        intent.putExtra("username", mUsername.getText());
                        intent.putExtra("post", photos.get(position).getPostImage());
                        intent.putExtra("postkey", key.get(position));
                        intent.putExtra("profileimage", photos.get(position).getProfileImage());
                        intent.putExtra("time", photos.get(position).getDate());
                        intent.putExtra("uid", photos.get(position).getUid());
                        //intent.putExtra("uid",currentuserID);
                        intent.putExtra("description", photos.get(position).getDescription());
                        startActivity(intent);
                        }
                });

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        }

    private void SendUserToSettingActivity()
        {
        //Intent loginIntent = new Intent(MyProfileInstagramActivity.this, SettingActivity.class);
        //startActivity(loginIntent);

        }

    private void SendUserToFriendsActivity()
        {
        // Intent loginIntent = new Intent(MyProfileInstagramActivity.this, FriendListActivity.class);
        // startActivity(loginIntent);

        }

    private void SendUserToNotificationActivity()
        {
        // Intent loginIntent = new Intent(MyProfileInstagramActivity.this, NotificationsActivity.class);
        // startActivity(loginIntent);

        }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
        inflater.inflate(R.menu.settings_navigation_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {

        int id = item.getItemId();

        if (id == R.id.set_friends)
            {
            //firebaseAuth.signOut();
            //checkUserStatus();
            }
        if (id == R.id.set_logout)
            {

            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Logout")

                    .setContentText("Are you sure?")
                    .setConfirmText("Yes")

                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                            {
                            sDialog.dismissWithAnimation();
                            mAuth.signOut();
                            checkUserStatus();
                            }
                    })

                    .show();

            //startActivity(new Intent(getApplicationContext(), ChatUserActivity.class));
            }
        if (id == R.id.set_settings)
            {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        return super.onOptionsItemSelected(item);
        }

    private void checkUserStatus()
        {
        //get Current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            {
            //user is signed in stay here
            //set mail of logged in user


            } else
            {
            //user not signed in
            startActivity(new Intent(getActivity(), StartActivity.class));
            }

        }

    @Override
    public void onStart()
        {
        checkUserStatus();

        super.onStart();
        }

}