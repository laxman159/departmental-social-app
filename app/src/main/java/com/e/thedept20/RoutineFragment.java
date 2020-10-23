package com.e.thedept20;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e.thedept20.Home.DashboardActivity;
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
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;
import static com.e.thedept20.SettingProfileActivity.Gallery_Pick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends Fragment
{

    private static final int GET_FROM_GALLERY = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // DatabaseHelper db;
    private TouchImageView imageView;
    private StorageReference userRoutineRef;
    private DatabaseReference settingref;
    private FirebaseAuth mAuth;
    private String currentuserID;
    private FloatingActionButton floatingActionButton;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RoutineFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoutineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoutineFragment newInstance(String param1, String param2)
        {
        RoutineFragment fragment = new RoutineFragment();
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
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        userRoutineRef = FirebaseStorage.getInstance().getReference().child("Routine");
        imageView = view.findViewById(R.id.imageView2);
        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        settingref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserID);


        floatingActionButton = view.findViewById(R.id.button4);

        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                //upload photo
                Intent gallaryintent = new Intent();
                gallaryintent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryintent.setType("image/*");
                startActivityForResult(gallaryintent, Gallery_Pick);
                }
        });


        settingref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {

                final String routine = snapshot.child("Routine").getValue().toString();
                //Glide.with(getActivity()).load(routine).into(imageView);
                if (!routine.equals("empty"))
                    {
                    Picasso.get().load(routine).into(imageView);
                    }


//                        {
//                            @Override
//                            public void onSuccess()
//                                {
//
//                                }
//
//                            @Override
//                            public void onError(Exception e)
//                                {
//                                Picasso.get()
//                                        .load(routine)
//                                        .networkPolicy(NetworkPolicy.OFFLINE)
//                                        .into(imageView);
//                                }
//                        });
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });
        return view;
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
        // super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) ;
        {


        Uri ImageUri = data.getData();
        CropImage.activity(ImageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(getActivity());
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
                {
                // Intent SetupIntent = new Intent(SettingProfileActivity.this, SettingProfileActivity.class);
                //startActivity(SetupIntent);


                Uri resultUri = result.getUri();
                Intent SetupIntent = new Intent(getActivity(), StudentsProtalActivity.class);
                //SetupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity(SetupIntent);

                final StorageReference filepath = userRoutineRef.child(currentuserID + ".jpg");
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
                                                    Toast.makeText(getContext(), "Error occured" + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error occured:image cant be cropped try again", Toast.LENGTH_SHORT).show();

                }
            }
        }


}