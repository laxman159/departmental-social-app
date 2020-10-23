package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Utils.OnlineOfflineStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{

    LinearLayout linearLayout, linearLayout2;
    Toolbar toolbar;
    ImageButton imageButton;
    private DatabaseReference settingref;
    private FirebaseAuth mAuth;
    private String currentuserID;
    private ProgressDialog loadingbar;
    private StorageReference userProfileImageRef;
    private CircleImageView circleImageView;
    private TextView fullname, statuss;
    private String i_name, i_profile, _i_status, i_number, i_email, i_address;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        linearLayout = findViewById(R.id.setting_ll1);
        imageButton = findViewById(R.id.setting_back_button);
        circleImageView = findViewById(R.id.setting_image_view);
        fullname = findViewById(R.id.setting_name_ll);
        statuss = findViewById(R.id.setting_status_ll);
        linearLayout2 = findViewById(R.id.settingll2);
        linearLayout2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                startActivity(new Intent(getApplicationContext(), AccountsActivity.class));
                }
        });


        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                finish();
                }
        });


        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        settingref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        settingref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                if (snapshot.exists())
                    {
                    i_profile = snapshot.child("profileImage").getValue().toString();
                    i_name = snapshot.child("Fullname").getValue().toString();
                    _i_status = snapshot.child("Status").getValue().toString();
                    Glide.with(getApplicationContext()).load(i_profile).into(circleImageView);
                   // Picasso.get().load(i_profile).placeholder(R.drawable.profile).into(circleImageView);
                    fullname.setText(i_name);
                    statuss.setText(_i_status);

                    linearLayout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                            Intent intent = new Intent(getApplicationContext(), SettingProfileActivity.class);

                            startActivity(intent);

                            }
                    });

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });


        }

    @Override
    protected void onStart()
        {
        super.onStart();

        }
}