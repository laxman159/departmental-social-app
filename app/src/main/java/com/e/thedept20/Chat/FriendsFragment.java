package com.e.thedept20.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.Friends;
import com.e.thedept20.Adapters.OnItemClick;
import com.e.thedept20.Adapters.Posts;
import com.e.thedept20.Adapters.User;
import com.e.thedept20.Adapters.UserAdapter;
import com.e.thedept20.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment
{
    static OnItemClick onItemClick;
    EditText search_users;
    private View contactsview;
    private RecyclerView recyclerView;
    private RecyclerView myFriendslist;
    private DatabaseReference friendsRef, usserRef;
    private String onlineUser_id;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Friends, FriendsviewHolder> adapter;
    private List<User> mUsers;
    private UserAdapter userAdapter;


    public FriendsFragment()
        {
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        contactsview = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendslist = contactsview.findViewById(R.id.friendsfragment_friendlist_view);
        myFriendslist.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myFriendslist.getContext(), DividerItemDecoration.VERTICAL);
        myFriendslist.addItemDecoration(dividerItemDecoration);
        mAuth = FirebaseAuth.getInstance();
        onlineUser_id = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUser_id);
        usserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsers = new ArrayList<>();
        SetAdapter();


        search_users = contactsview.findViewById(R.id.search_users_fragments_friends);
        search_users.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {

                }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                searchUsers(charSequence.toString().toLowerCase());
                }

            @Override
            public void afterTextChanged(Editable editable)
                {

                }
        });

        return contactsview;
        }

    private void searchUsers(String s)
        {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("Username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    mUsers.add(user);

                    }

                userAdapter = new UserAdapter(getContext(), onItemClick, mUsers, false);
                //myFriendslist.setAdapter(userAdapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        }

    private void SetAdapter()
        {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsRef, Friends.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Friends, FriendsviewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsFragment.FriendsviewHolder holder, int position, @NonNull final Friends model)
                {
                holder.friendsdate.setText("friended on " + model.getDate());
                final String usersIDs = getRef(position).getKey();
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
                            Glide.with(getContext()).load(profileImage).into(holder.all_profile);
                           // Picasso.get().load(profileImage).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE).into(holder.all_profile);

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
            public FriendsFragment.FriendsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FriendsFragment.FriendsviewHolder(view);
                }

        };
        myFriendslist.setAdapter(adapter);
        adapter.startListening();
        }

    @Override
    public void onStart()
        {
        super.onStart();

        adapter.startListening();


        }

    public class FriendsviewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView all_profile;
        public TextView all_username, friendsdate;

        public FriendsviewHolder(@NonNull View itemView)
            {

            super(itemView);
            myFriendslist = itemView.findViewById(R.id.friendlist_view);
            all_profile = itemView.findViewById(R.id.all_user_display_layout_profileimage);

            all_username = itemView.findViewById(R.id.all_user_display_layout_profilename);
            friendsdate = itemView.findViewById(R.id.all_user_display_layout_status);

            }
    }
}

