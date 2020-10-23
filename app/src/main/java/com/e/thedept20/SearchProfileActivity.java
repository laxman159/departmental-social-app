package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Account.ViewAllPostActivity;
import com.e.thedept20.Adapters.GridImageApapter;
import com.e.thedept20.Adapters.Posts;
import com.e.thedept20.Chat.ChatActivity;
import com.e.thedept20.Chat.ChatUserActivity;
import com.e.thedept20.Utils.FriendRequestActivity;
import com.e.thedept20.Utils.UniversalImageLoader;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchProfileActivity extends AppCompatActivity
{

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
    private String userID;
    private int countfriends = 0, countpost = 0, countrequest = 0;
    private ImageButton mButton;
    private TextView textView, textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);
        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        userID = getIntent().getStringExtra("visit_user_id");
        profileuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentuserID);
        allPostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Requestref = FirebaseDatabase.getInstance().getReference().child("Friendsrequests");
        currentuserID = mAuth.getCurrentUser().getUid();
        collefeName = findViewById(R.id.college_name);
        collegerollNum = findViewById(R.id.college_rollnumber);
        collegeDesignation = findViewById(R.id.college_designation);
        collegeEmaail = findViewById(R.id.college_email);
        usname = findViewById(R.id.display_username);
        textView1 = findViewById(R.id.hidephoto_textview);

        textView = findViewById(R.id.textEditProfile);
        textView.setText("Details");
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Intent loginIntent = new Intent(getApplicationContext(), FriendRequestActivity.class);
                loginIntent.putExtra("visit_user_id", userID);
                startActivity(loginIntent);
                }
        });

        mDisplayName = findViewById(R.id.display_name);
        mUsername = findViewById(R.id.custom_instagramprofile_name1);
        mDescription = findViewById(R.id.description);
        mProfilePhoto = findViewById(R.id.profile_photo);
        mPosts = findViewById(R.id.tvPosts);
        mFriends = findViewById(R.id.tvFollowers);
        mRequest = findViewById(R.id.tvFollowing);
        mEditprofile = findViewById(R.id.textEditProfile);
        gridView = findViewById(R.id.gridView);
        mFriendsText = findViewById(R.id.textFollowers);
        toolbar = findViewById(R.id.tttttt1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        mButton = findViewById(R.id.custun_insta_back);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                finish();
                }
        });

        friendsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                if (!snapshot.child(userID).exists())
                    {
                    textView1.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.INVISIBLE);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });


        initImageLoader();
        setProfileDetails();
        GridSetup();

        friendsRef.child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    countfriends = (int) dataSnapshot.getChildrenCount();
                    mFriends.setText(Integer.toString(countfriends));

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
        Requestref.child(userID).addValueEventListener(new ValueEventListener()
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

                        } else
                        {
                        mRequest.setText("0");
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });

        }

    private void initImageLoader()
        {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getApplicationContext());
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
                Glide.with(getApplicationContext()).load(s_myProfileImage).into(mProfilePhoto);
               // Picasso.get().load(s_myProfileImage).placeholder(R.drawable.profile).into(mProfilePhoto);
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
                .startAt(userID).endAt(userID + "\uf8ff");
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
                int gridwidth = getResources().getDisplayMetrics().widthPixels;
                int imagewidth = gridwidth / 3;
                gridView.setColumnWidth(imagewidth);

                final ArrayList<String> imageurls = new ArrayList<>();
                for (int i = 0; i < photos.size(); i++)
                    {
                    imageurls.add(photos.get(i).getPostImage());

                    }
                GridImageApapter apapter = new GridImageApapter(SearchProfileActivity.this, R.layout.layout_grid_imageview, "", imageurls);
                gridView.setAdapter(apapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                        //Posts posts=(Posts)parent.getItemAtPosition(position);

                        Intent intent = new Intent(SearchProfileActivity.this, ViewAllPostActivity.class);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
        {
        getMenuInflater().inflate(R.menu.friendsprofilemenu, menu);
        return super.onCreateOptionsMenu(menu);
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {

        int id = item.getItemId();


        if (id == R.id.action_unfriend)
            {

            new SweetAlertDialog(SearchProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Unfriend")

                    .setContentText("Are you sure?")
                    .setConfirmText("Yes")

                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                            {
                            sDialog.dismissWithAnimation();

                            }
                    })

                    .show();

            //startActivity(new Intent(getApplicationContext(), ChatUserActivity.class));
            }
        if (id == R.id.action_chat)
            {
            friendsRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                    if (snapshot.child(userID).exists())
                        {
                        Intent chatintent = new Intent(getApplicationContext(), ChatActivity.class);
                        chatintent.putExtra("visit_user_id", userID);
                        chatintent.putExtra("Username", mUsername.getText());
                        startActivity(chatintent);
                        } else
                        {
                        new SweetAlertDialog(SearchProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Not friends yet")
                                .setContentText("Add friend first to chat")

                                .show();

                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
            });
//            String m=friendsRef.child(currentuserID).child(userID).getKey();
//            if (m==userID)
//            {
//            Intent chatintent = new Intent(getApplicationContext(), ChatActivity.class);
//            chatintent.putExtra("visit_user_id", userID);
//            chatintent.putExtra("Username", mUsername.getText());
//            startActivity(chatintent);
//            }
            //startActivity(new Intent(getApplicationContext(), ChatUserActivity.class));

            }
        return super.onOptionsItemSelected(item);
        }


}

