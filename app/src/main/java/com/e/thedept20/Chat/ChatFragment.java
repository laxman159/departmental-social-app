package com.e.thedept20.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.Friends;
import com.e.thedept20.Adapters.Messages;
import com.e.thedept20.R;
import com.e.thedept20.RecyclerViewEmptySupport;
import com.e.thedept20.Utils.OnlineOfflineStatus;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment
{
    final ArrayList<Messages> messages = new ArrayList<>();
    ArrayList<String> lastmessage = new ArrayList<>();
    private View contactsview;
    private RecyclerViewEmptySupport myChatlist;
    private DatabaseReference messageRef, usserRef, friendsRef, messageRef2;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private OnlineOfflineStatus onlineOfflineStatus = new OnlineOfflineStatus();
    private FirebaseRecyclerAdapter<Friends, FriendsviewHolder> adapter;
    private Context mContex;


    public ChatFragment()
        {

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        contactsview = inflater.inflate(R.layout.fragment_chat, container, false);
        myChatlist = contactsview.findViewById(R.id.Chatfragment_Chatlist_view);
        myChatlist.setLayoutManager(new LinearLayoutManager(getContext()));
        myChatlist.setEmptyView(contactsview.findViewById(R.id.toDoEmptyView2));
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);
        usserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUserID);
        mContex=getContext();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        myChatlist.setHasFixedSize(true);
        myChatlist.setLayoutManager(linearLayoutManager);
        //SetAdapter();
        //adapter.startListening();


        return contactsview;
        }

    private void SetAdapter()
        {
        messageRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                {
                messageRef2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID).child(dataSnapshot.getKey());
                messageRef2.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {
                        for (DataSnapshot singlesnapshot : dataSnapshot.getChildren())
                            {
                            messages.add(dataSnapshot.getValue(Messages.class));
                            for (int i = 0; i < messages.size(); i++)
                                {
                                lastmessage.add(messages.get(i).getMessage());
                                }
                            }


                        final FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friends>()
                                .setQuery(messageRef, Friends.class)
                                .build();
                        adapter = new FirebaseRecyclerAdapter<Friends, FriendsviewHolder>(options)
                        {
                            @Override
                            protected void onBindViewHolder(@NonNull final ChatFragment.FriendsviewHolder holder, final int position, @NonNull final Friends model)
                                {

                                final String usersIDs = getRef(position).getKey();
                                //final String usersIDs = imageurls.get(position);

                                holder.all_profile.setAnimation(AnimationUtils.loadAnimation(mContex, R.anim.fade_transition_animation));
                                holder.relativeLayout.setAnimation(AnimationUtils.loadAnimation(mContex, R.anim.fade_scale_animation));
                                usserRef.child(usersIDs).addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                        if (dataSnapshot.exists())
                                            {
                                            final String username = dataSnapshot.child("Username").getValue().toString();
                                            final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                            holder.all_username.setText(username);
                                            Glide.with(mContex).load(profileImage).into(holder.all_profile);
                                            //Picasso.get().load(profileImage).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE).into(holder.all_profile);
                                            if (dataSnapshot.child("Userstate").hasChild("state"))
                                                {
                                                //String state2 = dataSnapshot.child("UserstateChat").child("state").getValue().toString();
                                               String state = dataSnapshot.child("Userstate").child("state").getValue().toString();
                                                String date = dataSnapshot.child("Userstate").child("date").getValue().toString();
                                                String time = dataSnapshot.child("Userstate").child("time").getValue().toString();
                                                holder.time.setText(time);
                                                if (state.equals("online"))
                                                    {
                                                    holder.status.setText("online");
                                                    holder.status.setVisibility(View.VISIBLE);
                                                    holder.offline.setVisibility(View.INVISIBLE);
                                                    }
                                                    else if (state.equals("offline") )
                                                    {
                                                    holder.offline.setText("Last seen: " + date + " ");
                                                    holder.offline.setVisibility(View.VISIBLE);
                                                    holder.status.setVisibility(View.INVISIBLE);
                                                    }
                                                } else
                                                {
                                                holder.status.setText("offline");
                                                }

                                            holder.lastmessage.setText("message sent");


                                            holder.itemView.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                    {
                                                    Intent chatintent = new Intent(getContext(), ChatActivity.class);
                                                    chatintent.putExtra("visit_user_id", usersIDs);
                                                    chatintent.putExtra("Username", username);
                                                    startActivity(chatintent);
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

                            @NonNull
                            @Override
                            public ChatFragment.FriendsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                                {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_users, parent, false);
                                return new ChatFragment.FriendsviewHolder(view);

                                }

                        };

                        myChatlist.setAdapter(adapter);
                        adapter.startListening();
                        }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {

                        }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                        {

                        }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                });


                }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                {

                }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                {

                }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                {

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }

        });

        }

    @Override
    public void onStart()
        {
        super.onStart();
        SetAdapter();
        //adapter.startListening();
        // onlineOfflineStatus.UpdateUserStatus("online");


        }

    @Override
    public void onStop()
        {
        super.onStop();
       // onlineOfflineStatus.UpdateUserStatus("offline");
        // adapter.stopListening();
        }

    @Override
    public void onDestroy()
        {
        super.onDestroy();
        // onlineOfflineStatus.UpdateUserStatus("offline");
        }

    public static class FriendsviewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView all_profile;
        public RelativeLayout relativeLayout;
        public TextView all_username, lastmessage, status, offline,time;

        public FriendsviewHolder(@NonNull View itemView)
            {

            super(itemView);
            all_profile = itemView.findViewById(R.id.chatlist_display_layout_profileimage);
            all_username = itemView.findViewById(R.id.chatlist_display_layout_profilename);
            lastmessage = itemView.findViewById(R.id.chatlist_display_layout_lastmessage);
            status = itemView.findViewById(R.id.chatlist_display_layout_status);
            offline = itemView.findViewById(R.id.chatlist_display_layout_offlinestatus);
            time=itemView.findViewById(R.id.chatlist_display_layout_time);
            relativeLayout=itemView.findViewById(R.id.rl8);
            }
    }
}
