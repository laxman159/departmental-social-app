package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e.thedept20.Chat.ChatActivity;
import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.LoginRegister.StartActivity;
import com.e.thedept20.Notes.util.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingProfileActivity extends AppCompatActivity
{
    final static int Gallery_Pick = 1;
    Toolbar toolbar;
    FirebaseUser firebaseUser;
    private CircleImageView circleImageView;
    private TextView name, about, phonenum, email, address;
    private String title;
    private LinearLayout linearLayout, about_linearlayout, phone_linearlyot, email_ll, address_ll;
    private BottomSheetDialog bsDialogEditName;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference settingref;
    private FirebaseAuth mAuth;
    private String currentuserID, currentdateString, genderselect;
    private StorageReference userProfileImageRef;
    private SweetAlertDialog pDialog;
    private String i_name, i_profile, _i_status, i_number, i_email, i_address;
    private Button button;

    public SettingProfileActivity()
        {
        }

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        button = findViewById(R.id.btn_log_out);
        circleImageView = findViewById(R.id.image_profile);
        name = findViewById(R.id.tv_username);
        about = findViewById(R.id.tv_about);
        phonenum = findViewById(R.id.tv_phone);
        email = findViewById(R.id.tv_email);
        address = findViewById(R.id.tv_address);
        floatingActionButton = findViewById(R.id.fab_camera);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        linearLayout = findViewById(R.id.ln_edit_name);
        about_linearlayout = findViewById(R.id.ln_edit_status);
        phone_linearlyot = findViewById(R.id.ln_edit_phone);
        email_ll = findViewById(R.id.ln_edit_email);
        address_ll = findViewById(R.id.ln_edit_address);


        //Intent intent=new Intent();


        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        settingref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                mAuth.signOut();
                checkUserStatus();
                }
        });

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
                    i_number = snapshot.child("Contact number").getValue().toString();
                    i_email = snapshot.child("Email").getValue().toString();
                    i_address = snapshot.child("Address").getValue().toString();

                    }
                name.setText(i_name);
                phonenum.setText(i_number);
                about.setText(_i_status);
                email.setText(i_email);
                address.setText(i_address);
                Glide.with(getApplicationContext()).load(i_profile).into(circleImageView);
                //Picasso.get().load(i_profile).placeholder(R.drawable.profile).into(circleImageView);

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {

                Intent gallaryintent = new Intent();
                gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryintent.setType("image/*");
                startActivityForResult(gallaryintent, Gallery_Pick);
                }


        });


        linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {

                title = "Name";
                Aleart(title);


                }
        });
        about_linearlayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                title = "About";
                Aleart(title);
                }
        });
        phone_linearlyot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                title = "Phone number";
                Aleart(title);
                }
        });
        email_ll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                title = "Email address";
                Aleart(title);
                }
        });
        address_ll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                title = "Address";
                Aleart(title);
                }
        });


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
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            }
        }

    private void Aleart(final String title)
        {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        final EditText inputfield = new EditText(SettingProfileActivity.this);
        //inputfield.setText("");
        pDialog.setTitleText(title);
        pDialog.setCustomView(inputfield);
        pDialog.setConfirmText("Update");
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {

            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
                {
                if (title.equals("Name"))
                    {
                    UpdateStudentName(inputfield.getText().toString().trim());
                    } else if (title.equals("About"))
                    {
                    UpdateStudentStatus(inputfield.getText().toString().trim());
                    } else if (title.equals("Phone number"))
                    {

                    UpdateStudentPhone(inputfield.getText().toString().trim());
                    } else if (title.equals("Email address"))
                    {

                    UpdateStudentEmail(inputfield.getText().toString().trim());
                    } else if (title.equals("Address"))
                    {

                    UpdateStudentAddress(inputfield.getText().toString().trim());
                    }

                }
        });
        pDialog.show();

        }


    private void UpdateStudentName(String dscp)
        {
        settingref.child("Fullname").setValue(dscp);
        pDialog.dismiss();

        }

    private void UpdateStudentStatus(String titl)
        {
        settingref.child("Status").setValue(titl);
        pDialog.dismiss();
        }

    private void UpdateStudentPhone(String titl)
        {
        settingref.child("Contact number").setValue(titl);
        pDialog.dismiss();
        }

    private void UpdateStudentEmail(String titl)
        {
        settingref.child("Email").setValue(titl);
        pDialog.dismiss();
        }

    private void UpdateStudentAddress(String titl)
        {
        settingref.child("Address").setValue(titl);
        pDialog.dismiss();
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
                // Intent SetupIntent = new Intent(SettingProfileActivity.this, SettingProfileActivity.class);
                //startActivity(SetupIntent);


                Uri resultUri = result.getUri();
                Intent SetupIntent = new Intent(SettingProfileActivity.this, SettingProfileActivity.class);
                //SetupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(SetupIntent);

                final StorageReference filepath = userProfileImageRef.child(currentuserID + ".jpg");
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
                                settingref.child("profileImage").setValue(downloadURL)
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
                                                    Toast.makeText(SettingProfileActivity.this, "Error occured" + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Error occured:image cant be cropped try again", Toast.LENGTH_SHORT).show();

                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
        {
        getMenuInflater().inflate(R.menu.del_menu, menu);
        return super.onCreateOptionsMenu(menu);

        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {

        int id = item.getItemId();


        if (id == R.id.delete)
            {

            new SweetAlertDialog(SettingProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("DELETE ACCOUNT")

                    .setContentText("Are you sure?")
                    .setConfirmText("Yes")

                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sDialog)
                            {
                            mAuth.signOut();
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();


                            }
                    }).show();

            //startActivity(new Intent(getApplicationContext(), ChatUserActivity.class));
            }

        return super.onOptionsItemSelected(item);
        }

}