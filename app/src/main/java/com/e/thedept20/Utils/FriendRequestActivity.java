package com.e.thedept20.Utils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.thedept20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestActivity extends AppCompatActivity
{
    private TextView username, fullname, status, designation, gender, rollno, unirolllno, dob, college, semester, email, department, clubs, hod;
    private CircleImageView profileImage;
    private Button sendfirendrequestbutton, declinefriendrequestbutton;
    private DatabaseReference friendrequestRef, userRef, friendsref;
    private FirebaseAuth mAuth;
    private String senderuserID, reciveruserID, current_state, SaveCurrentDate;
    private Toolbar mToolbar;
    private ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        mAuth = FirebaseAuth.getInstance();
        reciveruserID = getIntent().getExtras().get("visit_user_id").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        senderuserID = mAuth.getCurrentUser().getUid();
        friendrequestRef = FirebaseDatabase.getInstance().getReference().child("Friendsrequests");
        friendsref = FirebaseDatabase.getInstance().getReference().child("Friends");
        mToolbar = findViewById(R.id.app_b);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Details");
        InitilizeFields();

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                finish();
                }
        });
        userRef.child(reciveruserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {

                    String s_dob = dataSnapshot.child("Date of birth").getValue().toString();
                    String s_designation = dataSnapshot.child("Designation").getValue().toString();
                    String s_profileStatus = dataSnapshot.child("Status").getValue().toString();
                    String s_fullname = dataSnapshot.child("Fullname").getValue().toString();
                    String s_gender = dataSnapshot.child("Gender").getValue().toString();
                    String s_rollno = dataSnapshot.child("College roll number").getValue().toString();
                    String s_unirollno = dataSnapshot.child("University roll number").getValue().toString();
                    String s_username = dataSnapshot.child("Username").getValue().toString();
                    String s_myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String s_college = dataSnapshot.child("College name").getValue().toString();
                    String s_semsestem = dataSnapshot.child("Semester").getValue().toString();
                    String s_dept = dataSnapshot.child("Department").getValue().toString();
                    String s_email = dataSnapshot.child("Email").getValue().toString();
                    Picasso.get().load(s_myProfileImage).placeholder(R.drawable.profile).into(profileImage);

                    username.setText("@ " + s_username);
                    clubs.setVisibility(View.INVISIBLE);
                    hod.setVisibility(View.INVISIBLE);

                    fullname.setText(s_fullname);
                    department.setText(s_dept);
                    rollno.setText("College roll no: " + s_rollno);
                    gender.setText("Gender:  " + s_gender);
                    unirolllno.setText("university roll no: " + s_unirollno);
                    semester.setText("Semsester: " + s_semsestem);
                    MaintananceofButtons();


                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });

        declinefriendrequestbutton.setVisibility(View.INVISIBLE);
        declinefriendrequestbutton.setEnabled(false);

        if (!senderuserID.equals(reciveruserID))
            {
            sendfirendrequestbutton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                    sendfirendrequestbutton.setEnabled(false);
                    if (current_state.equals("Not Friends"))
                        {
                        SendFriendRequest();
                        }
                    if (current_state.equals("Request_send"))
                        {
                        CancelFriendRequest();
                        }
                    if (current_state.equals("Request_received"))
                        {
                        AcceptFriendRequest();
                        }
                    if (current_state.equals("Friends"))
                        {
                        UnfriendExistingfriends();
                        }
                    }
            });

            } else
            {
            declinefriendrequestbutton.setVisibility(View.INVISIBLE);
            sendfirendrequestbutton.setVisibility(View.INVISIBLE);
            }
        }

    private void UnfriendExistingfriends()
        {
        friendsref.child(senderuserID).child(reciveruserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
                {
                if (task.isSuccessful())
                    {
                    friendsref.child(reciveruserID).child(senderuserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                            {
                            if (task.isSuccessful())
                                {
                                sendfirendrequestbutton.setEnabled(true);
                                current_state = "Not Friends";
                                sendfirendrequestbutton.setText("Send Friend Request");

                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                declinefriendrequestbutton.setEnabled(false);
                                }

                            }
                    });
                    }

                }
        });

        }

    private void AcceptFriendRequest()
        {
        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        SaveCurrentDate = currentDate.format(callforDate.getTime());

        friendsref.child(senderuserID).child(reciveruserID).child("date").setValue(SaveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                        {
                        if (task.isSuccessful())
                            {
                            friendsref.child(reciveruserID).child(senderuserID).child("date").setValue(SaveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                            {
                                            if (task.isSuccessful())
                                                {
                                                friendrequestRef.child(senderuserID).child(reciveruserID)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                        if (task.isSuccessful())
                                                            {
                                                            friendrequestRef.child(reciveruserID).child(senderuserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                            if (task.isSuccessful())
                                                                                {
                                                                                sendfirendrequestbutton.setEnabled(true);
                                                                                current_state = "Friends";
                                                                                sendfirendrequestbutton.setText("Unfriend");

                                                                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                                                                declinefriendrequestbutton.setEnabled(false);
                                                                                }

                                                                            }
                                                                    });
                                                            }

                                                        }
                                                });
                                                }
                                            }
                                    });

                            }
                        }
                });

        }

    private void CancelFriendRequest()
        {
        friendrequestRef.child(senderuserID).child(reciveruserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
                {
                if (task.isSuccessful())
                    {
                    friendrequestRef.child(reciveruserID).child(senderuserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                            {
                            if (task.isSuccessful())
                                {
                                sendfirendrequestbutton.setEnabled(true);
                                current_state = "Not Friends";
                                sendfirendrequestbutton.setText("Send Friend Request");

                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                declinefriendrequestbutton.setEnabled(false);
                                }

                            }
                    });
                    }

                }
        });

        }

    private void MaintananceofButtons()
        {
        friendrequestRef.child(senderuserID)
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                        if (dataSnapshot.hasChild(reciveruserID))
                            {
                            String request_type = dataSnapshot.child(reciveruserID).child("Request_type").getValue().toString();
                            if (request_type.equals("Send"))
                                {
                                current_state = "Request_send";
                                sendfirendrequestbutton.setText("Cancel Friend Request");
                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                declinefriendrequestbutton.setEnabled(false);
                                } else if (request_type.equals("Received"))
                                {
                                current_state = "Request_received";
                                sendfirendrequestbutton.setText("Accept Friend Request");
                                declinefriendrequestbutton.setVisibility(View.VISIBLE);
                                declinefriendrequestbutton.setEnabled(true);
                                declinefriendrequestbutton.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                        {
                                        CancelFriendRequest();
                                        }
                                });
                                }

                            } else
                            friendsref.child(senderuserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                            if (dataSnapshot.hasChild(reciveruserID))
                                                {
                                                current_state = "Friends";
                                                sendfirendrequestbutton.setText("Unfriend");
                                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                                declinefriendrequestbutton.setEnabled(false);
                                                }

                                            }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError)
                                            {

                                            }
                                    });

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                });
        }

    private void SendFriendRequest()
        {
        friendrequestRef.child(senderuserID).child(reciveruserID)
                .child("Request_type").setValue("Send").addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
                {
                if (task.isSuccessful())
                    {
                    friendrequestRef.child(reciveruserID).child(senderuserID)
                            .child("Request_type").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                            {
                            if (task.isSuccessful())
                                {
                                sendfirendrequestbutton.setEnabled(true);
                                current_state = "Request_send";
                                sendfirendrequestbutton.setText("Cancel Friend Request");

                                declinefriendrequestbutton.setVisibility(View.INVISIBLE);
                                declinefriendrequestbutton.setEnabled(false);
                                }

                            }
                    });
                    }

                }
        });


        }


    private void InitilizeFields()
        {
        username = findViewById(R.id.request_profile_username);
        fullname = findViewById(R.id.request_profile_name);

        gender = findViewById(R.id.request_profile_gender);
        rollno = findViewById(R.id.request_profile_rollno);

        profileImage = findViewById(R.id.request_profilepic);

        semester = findViewById(R.id.request_profile_semester);

        department = findViewById(R.id.request_profile_department);
        clubs = findViewById(R.id.request_profile_clubs);

        hod = findViewById(R.id.request_profile_hod);
        unirolllno = findViewById(R.id.request_profile_unirollno);

        sendfirendrequestbutton = findViewById(R.id.request_send);
        declinefriendrequestbutton = findViewById(R.id.request_accept);
        back = findViewById(R.id.share_back);

        current_state = "Not Friends";


        }
}

