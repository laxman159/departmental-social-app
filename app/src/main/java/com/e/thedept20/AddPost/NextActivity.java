package com.e.thedept20.AddPost;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.StudentsProtalActivity;
import com.e.thedept20.Utils.FileCompressor;
import com.e.thedept20.Utils.FilePaths;
import com.e.thedept20.Utils.FirebaseMethods;
import com.e.thedept20.Utils.ImageManager;
import com.e.thedept20.Utils.RotateBitmap;
import com.e.thedept20.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by User on 7/24/2017.
 */

public class NextActivity extends AppCompatActivity
{

    private static final String TAG = "NextActivity";

    //firebase
    Activity mActivity = NextActivity.this;
    FileCompressor compressor = new FileCompressor(mActivity);
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String path, Discription, SaveCurrentDate, SaveCurrentTime, PostRandonName, CurrentUSerID, DownloadURL;
    private StorageReference PostImagesReference;
    private DatabaseReference userRef, postRef;
    private long countpost = 0;


    //widgets
    private EditText mCaption;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private Uri ImageUri;
    private Context mContext;
    private ZoomageView zoomageView;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = findViewById(R.id.caption);
        intent = getIntent();
        imgUrl = intent.getStringExtra(getString(R.string.selected_image));


        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostImagesReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CurrentUSerID = mAuth.getUid();
        mContext = getApplicationContext();
        zoomageView = findViewById(R.id.zome_img_next);
        Picasso.get().load(imgUrl).into(zoomageView);

        pDialog = new SweetAlertDialog(NextActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Uploading...");
        pDialog.setCancelable(false);


        setupFirebaseAuth();

        ImageView backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Log.d(TAG, "onClick: closing the activity");
                finish();
                }
        });


        TextView share = findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                //Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                // pDialog.show();
                String caption = mCaption.getText().toString();
                pDialog.show();
                if (intent.hasExtra(getString(R.string.selected_image)))
                    {
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));

                    //SendUserToMainActivity();
                    StoringImageTOFirebaseStorage();


                    // mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl,null);
                    } else if (intent.hasExtra(getString(R.string.selected_bitmap)))
                    {
                    bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));


                    uploadNewPhoto(caption, bitmap);
                    // mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null,bitmap);
                    }


                }
        });

        setImage();
        }

    public void uploadNewPhoto(final String caption,
                               Bitmap bm)
        {
        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        SaveCurrentDate = currentDate.format(callforDate.getTime());


        Calendar callforTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime = currentTime.format(callforTime.getTime());

        PostRandonName = SaveCurrentDate + SaveCurrentTime;
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        FilePaths filePaths = new FilePaths();

        Log.d(TAG, "uploadNewPhoto: uploading NEW photo.");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference storageReference = PostImagesReference.child("Post Images").child(PostRandonName + ".jpg");
        // .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));

        //convert image url to bitmap
        if (bm == null)
            {
            bm = ImageManager.getBitmap(imgUrl);
            }

        byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);


        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                        {
                        DownloadURL = uri.toString();


                        SavingInfoToFirebaseDb();
                        }
                });


//                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
//
//                    //add the new photo to 'photos' node and 'user_photos' node
//                    // addPhotoToDatabase(caption, firebaseUrl.toString());
//
//                    //navigate to the main feed so the user can see their photo
//                    Intent intent = new Intent(mContext, HomeActivity.class);
//                    mContext.startActivity(intent);
//                    }
//            }).addOnFailureListener(new OnFailureListener()
//            {
//                @Override
//                public void onFailure(@NonNull Exception e)
//                    {
//                    Log.d(TAG, "onFailure: Photo upload failed.");
//                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
//                    }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
//            {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
//                    {
//                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//
//                    if (progress - 15 > mPhotoUploadProgress)
//                        {
//                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
//                        mPhotoUploadProgress = progress;
//                        }

//                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
//                    }
//            });
//
                }
        });
        }


    private void StoringImageTOFirebaseStorage()
        {

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        SaveCurrentDate = currentDate.format(callforDate.getTime());
        ImageUri = Uri.fromFile(compressor.compressImage(imgUrl));


        Calendar callforTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime = currentTime.format(callforTime.getTime());

        PostRandonName = SaveCurrentDate + SaveCurrentTime;

        final StorageReference filepath = PostImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() + PostRandonName + ".jpg");


        filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {

            @Override

            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                //Toast.makeText(NextActivity.this, "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri uri)
                        {
                        DownloadURL = uri.toString();
                        SavingInfoToFirebaseDb();
                        }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                        {
                        Toast.makeText(NextActivity.this, "Error occured: " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                });

                }

        })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                        {
                        Toast.makeText(NextActivity.this, "image not uploaded successfully to Storage... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });

        }


    private void SavingInfoToFirebaseDb()
        {

        postRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    countpost = dataSnapshot.getChildrenCount();
                    } else
                    {
                    countpost = 0;
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        userRef.child(CurrentUSerID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    String userfullname = dataSnapshot.child("Username").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();

                    HashMap postMap = new HashMap();
                    postMap.put("Uid", CurrentUSerID);
                    postMap.put("Date", SaveCurrentDate);
                    postMap.put("Time", SaveCurrentTime);
                    postMap.put("Description", mCaption.getText().toString());
                    postMap.put("PostImage", DownloadURL);
                    postMap.put("ProfileImage", userProfileImage);
                    postMap.put("fullname", userfullname);
                    postMap.put("Counter", countpost);
                    postRef.child(CurrentUSerID + PostRandonName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                            {
                            if (task.isSuccessful())
                                {
//                                new SweetAlertDialog(NextActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                                        .setTitleText("Image Uploaded ")
//                                        .show();
                                pDialog.dismiss();
                                //
                                finish();
                                //SendUserToMainActivity();
                                //Toast.makeText(NextActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                // loadingbar.dismiss();
                                } else
                                {
                                Toast.makeText(NextActivity.this, "Error,try again", Toast.LENGTH_SHORT).show();
                                // loadingbar.dismiss();
                                }
                            }
                    });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });

        }

    private void someMethod()
        {
        /*
            Step 1)
            Create a data model for Photos

            Step 2)
            Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)

            Step 3)
            Count the number of photos that the user already has.

            Step 4)
            a) Upload the photo to Firebase Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node

         */

        }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage()
        {
        intent = getIntent();
        ImageView image = findViewById(R.id.imageShare);

        if (intent.hasExtra(getString(R.string.selected_image)))
            {
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
            UniversalImageLoader.setImage(imgUrl, zoomageView, null, mAppend);
            } else if (intent.hasExtra(getString(R.string.selected_bitmap)))
            {
            bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
            }
        }

     /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth()
        {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null)
                    {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    } else
                    {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                // ...
                }
        };


        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
                {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count: " + imageCount);

                }

            @Override
            public void onCancelled(DatabaseError databaseError)
                {

                }
        });
        }


    @Override
    public void onStart()
        {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        }

    @Override
    public void onStop()
        {
        super.onStop();
        if (mAuthListener != null)
            {
            mAuth.removeAuthStateListener(mAuthListener);
            }
        }

    private void SendUserToMainActivity()
        {
        Intent mainInten = new Intent(getApplicationContext(), DashboardActivity.class);
        //mainInten.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainInten);


        }
}
