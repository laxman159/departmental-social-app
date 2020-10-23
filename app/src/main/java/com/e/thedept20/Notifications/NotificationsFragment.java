package com.e.thedept20.Notifications;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.thedept20.Adapters.FindFriends;
import com.e.thedept20.R;
import com.e.thedept20.RecyclerViewEmptySupport;
import com.e.thedept20.SearchProfileActivity;
import com.e.thedept20.Utils.FriendRequestActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerViewEmptySupport myRequestlist;
    private DatabaseReference Requestref, usersRef, friendsref;
    private FirebaseAuth mAuth;
    private String currentuserID, SaveCurrentDate, senderuserID, reciveruserID;
    private Toolbar toolbar;
    private SweetAlertDialog pDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationsFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2)
        {
        NotificationsFragment fragment = new NotificationsFragment();
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
        View vIew = inflater.inflate(R.layout.fragment_notifications, container, false);
        myRequestlist = vIew.findViewById(R.id.noti_recyclerView);
        myRequestlist.setEmptyView(vIew.findViewById(R.id.toDoEmptyView3));
        myRequestlist.setLayoutManager(new LinearLayoutManager(getContext()));
        Requestref = FirebaseDatabase.getInstance().getReference().child("Friendsrequests");
        friendsref = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        senderuserID = mAuth.getCurrentUser().getUid();

        //setSupportActionBar(toolbar);


        return vIew;
        }

    @Override
    public void onStart()
        {
        super.onStart();
        FirebaseRecyclerOptions<FindFriends> options = new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(Requestref.child(currentuserID), FindFriends.class)
                .build();

        FirebaseRecyclerAdapter<FindFriends, RequestViewHolder> adapter = new
                FirebaseRecyclerAdapter<FindFriends, RequestViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull FindFriends model)
                        {
                        holder.itemView.findViewById(R.id.nav_Request_notificatonsaccept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.nav_Request_notificatonsdecilne_button).setVisibility(View.VISIBLE);

                        reciveruserID = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("Request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {

                                if (dataSnapshot.exists())
                                    {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("Received"))
                                        {
                                        usersRef.child(reciveruserID).addValueEventListener(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                final String username = dataSnapshot.child("Fullname").getValue().toString();
                                                final String status = dataSnapshot.child("Status").getValue().toString();
                                                final String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                                                holder.username.setText(username);
                                                holder.status.setText(status);
                                                Picasso.get().load(profileImage).into(holder.profileImage);
                                                holder.AcceptButton.setOnClickListener(new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                        {
                                                        AcceptFriendRequest();
                                                        }
                                                });
                                                holder.DeclineButton.setOnClickListener(new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                        {
                                                        CancelFriendRequest();

                                                        }
                                                });
                                                holder.itemView.setOnClickListener(new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                        {
                                                        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                                                        pDialog.setTitleText(username + " friend request");
                                                        pDialog.setCancelable(true);
                                                        pDialog.setContentText("Profile");
                                                        pDialog.setConfirmButton("GOTO", new SweetAlertDialog.OnSweetClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog)
                                                                {
                                                                pDialog.dismiss();
                                                                Intent profileintent = new Intent(getContext(), SearchProfileActivity.class);
                                                                profileintent.putExtra("visit_user_id", reciveruserID);
                                                                startActivity(profileintent);
                                                                }
                                                        });
                                                        pDialog.show();


//                                                        CharSequence options[] = new CharSequence[]
//                                                                {
//                                                                        "Profile"
//                                                                };
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                                        builder.setTitle(username + " friend request");
//                                                        builder.setItems(options, new DialogInterface.OnClickListener()
//                                                        {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which)
//                                                                {
//
//                                                                if (which == 0)
//                                                                    {
//
//
//                                                                    Intent profileintent = new Intent(getContext(), SearchProfileActivity.class);
//                                                                    profileintent.putExtra("visit_user_id", reciveruserID);
//                                                                    startActivity(profileintent);
//                                                                    }
//
//                                                                }
//                                                        });
//                                                        builder.show();
                                                        }

                                                });


                                                }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError)
                                                {

                                                }
                                        });
                                        }
                                    }

                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                        });


                        }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                        {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_speenet, parent, false);
                        return new RequestViewHolder(view);
                        }
                };
        myRequestlist.setAdapter(adapter);
        adapter.startListening();
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
                                                Requestref.child(senderuserID).child(reciveruserID)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                        if (task.isSuccessful())
                                                            {
                                                            Requestref.child(reciveruserID).child(senderuserID)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                            if (task.isSuccessful())
                                                                                {
                                                                                Toast.makeText(getContext(), "Accepted", Toast.LENGTH_SHORT).show();
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
        Requestref.child(senderuserID).child(reciveruserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
                {
                if (task.isSuccessful())
                    {
                    Requestref.child(reciveruserID).child(senderuserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                            {
                            if (task.isSuccessful())
                                {
                                Toast.makeText(getContext(), "Declined", Toast.LENGTH_SHORT).show();
                                }

                            }
                    });
                    }

                }
        });

        }

    public class RequestViewHolder extends RecyclerView.ViewHolder
    {

        TextView username, status;
        CircleImageView profileImage;
        Button AcceptButton, DeclineButton;

        public RequestViewHolder(@NonNull View itemView)
            {
            super(itemView);
            username = itemView.findViewById(R.id.all_user_display_notificatonslayout_profilename);
            status = itemView.findViewById(R.id.all_user_display_notificatonslayout_status);
            profileImage = itemView.findViewById(R.id.all_user_display_notificatonslayout_profileimage);
            AcceptButton = itemView.findViewById(R.id.nav_Request_notificatonsaccept_button);
            DeclineButton = itemView.findViewById(R.id.nav_Request_notificatonsdecilne_button);

            }
    }
}