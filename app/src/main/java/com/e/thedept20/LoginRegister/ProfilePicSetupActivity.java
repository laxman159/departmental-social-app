package com.e.thedept20.LoginRegister;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePicSetupActivity extends AppCompatActivity
{

    final static int Gallery_Pick = 1;
    private CircleImageView dp;
    private EditText status;
    private FloatingActionButton fab;
    private StorageReference userProfileImageRef;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private Button button;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_setup);

        dp = findViewById(R.id.setup_profile);
        status = findViewById(R.id.setup_status);
        button = findViewById(R.id.register4_creat_account1);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                userRef.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                        if (dataSnapshot.exists())
                            {

                            if (dataSnapshot.hasChild("profileImage"))
                                {
                                SaveAccountSetupInformationStudent();
                                } else
                                {
                                Toast.makeText(ProfilePicSetupActivity.this, "Profile Pic is missing", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                });


                }
        });


        fab = findViewById(R.id.setup_fab_camera);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        userRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {

                    if (dataSnapshot.hasChild("profileImage"))
                        {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.user).into(dp);
                        } else
                        {

                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        dp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                opengallery();
                }
        });
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                opengallery();
                }
        });
        }

    private void opengallery()
        {

        Intent gallaryintent = new Intent();
        gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryintent.setType("image/*");
        startActivityForResult(gallaryintent, Gallery_Pick);
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
                .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
                {
                Intent SetupIntent = new Intent(ProfilePicSetupActivity.this, ProfilePicSetupActivity.class);
                startActivity(SetupIntent);
                //pDialog.show();
                Uri resultUri = result.getUri();

                final StorageReference filepath = userProfileImageRef.child(currentUserID + ".jpg");

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
                                String downloadURL = uri.toString();

                                userRef.child("profileImage").setValue(downloadURL)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                                {
                                                if (task.isSuccessful())
                                                    {


                                                   // Toast.makeText(ProfilePicSetupActivity.this, "Profile image stored", Toast.LENGTH_SHORT).show();
                                                    } else
                                                    {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(ProfilePicSetupActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
                                                    pDialog.dismiss();
                                                    }
                                                }

                                        });
                                }
                        });
                        }
                });

                } else
                {
                Toast.makeText(this, "Error occured:image cant be cropped try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private void SaveAccountSetupInformationStudent()
        {
        //boolean image1=getIntent().getBooleanExtra("pic",false);
        String Status = status.getText().toString().trim();


        if (TextUtils.isEmpty(Status))
            {
            Toast.makeText(this, "Enter status ", Toast.LENGTH_SHORT).show();
            } else
            {
            new SweetAlertDialog(ProfilePicSetupActivity.this,SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText("Creating your profile")
                    .show();

            HashMap userMap = new HashMap();
            userMap.put("Status", Status);
            userMap.put("Email", "");

            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                    {
                    SendUserToMainActivity();
                    }
            });
            }
        }

    private void SendUserToMainActivity()
        {
        Intent intent = new Intent(ProfilePicSetupActivity.this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        }
}
