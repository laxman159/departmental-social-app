package com.e.thedept20.AddPost;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.Utils.AmazingAutofitEditText;
import com.google.android.gms.tasks.OnCompleteListener;
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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextShareFragment extends Fragment
{

    View view;
    private ImageView shareClose;
    private Spinner directorySpinner;
    private TextView next;
    private String Discription, SaveCurrentDate, SaveCurrentTime, PostRandonName, CurrentUSerID;
    private ProgressDialog loadingbar;
    private FloatingActionButton floatingActionButton;
    private StorageReference PostImagesReference;
    private DatabaseReference userRef, noticeRef;
    private long countpost = 0;
    private FirebaseAuth mAuth;
    private AmazingAutofitEditText amazingAutofitEditText;

    public TextShareFragment()
        {
        // Required empty public constructor
        }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
        view = inflater.inflate(R.layout.fragment_text_share_activity, container, false);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        directorySpinner.setVisibility(View.INVISIBLE);
        shareClose = view.findViewById(R.id.ivCloseShare);
        next = view.findViewById(R.id.tvNext);
        next.setVisibility(View.INVISIBLE);
        loadingbar = new ProgressDialog(getContext());

        noticeRef = FirebaseDatabase.getInstance().getReference().child("Notices");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostImagesReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CurrentUSerID = mAuth.getUid();

        amazingAutofitEditText = view.findViewById(R.id.textView_text);

        floatingActionButton = view.findViewById(R.id.fab_text);

        shareClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Intent intent = new Intent(getContext(), DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                }
        });
        SharePost();
        return view;
        }


    private void SharePost()
        {
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                ValidatePostInfo();
                }
        });
        }

    private void ValidatePostInfo()
        {
        Discription = amazingAutofitEditText.getText().toString();
        if (TextUtils.isEmpty(Discription))
            {
            Toast.makeText(getContext(), "Type something first", Toast.LENGTH_SHORT).show();
            } else
            {
            loadingbar.setTitle("Posting ");
            loadingbar.setMessage("Please Wait   ,");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            StoringImageTOFirebaseStorage();
            }
        }

    private void StoringImageTOFirebaseStorage()
        {
        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM yyyy");
        SaveCurrentDate = currentDate.format(callforDate.getTime());


        Calendar callforTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        SaveCurrentTime = currentTime.format(callforTime.getTime());

        PostRandonName = SaveCurrentDate + SaveCurrentTime;


        SavingInfoToFirebaseDb();
        }


    private void SavingInfoToFirebaseDb()
        {
        noticeRef.addValueEventListener(new ValueEventListener()
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
                    postMap.put("Description", Discription);
                    postMap.put("ProfileImage", userProfileImage);
                    postMap.put("fullname", userfullname);
                    postMap.put("Counter", countpost);
                    noticeRef.child(CurrentUSerID + PostRandonName).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener()
                    {
                        @Override
                        public void onComplete(@NonNull Task task)
                            {
                            if (task.isSuccessful())
                                {
                                SendUserToMainActivity();
                                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                getActivity().finish();
                                } else
                                {
                                Toast.makeText(getContext(), "Error,try again", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
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

    private void SendUserToMainActivity()
        {
        Intent mainInten = new Intent(getActivity(), DashboardActivity.class);
        startActivity(mainInten);
        }


}
