package com.e.thedept20.Search;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.Friends;
import com.e.thedept20.R;
import com.e.thedept20.SearchProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFriendsFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ActionBar actionBar;
    private EditText search;
    private RecyclerView friendlistview;
    private DatabaseReference friendsRef, usserRef;
    private FirebaseAuth mAuth;
    private String onlineUser_id;
    private Context context;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  FirebaseRecyclerAdapter<Friends, FriendsviewHolder> adapter;

    public FindFriendsFragment()
        {
        // Required empty public constructor
        }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFriendsFragment newInstance(String param1, String param2)
        {
        FindFriendsFragment fragment = new FindFriendsFragment();
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
        View view = inflater.inflate(R.layout.fragment_findfriends, container, false);

        //actionBar.hide();
        search = view.findViewById(R.id.findfriends_searchbox);
        friendlistview = view.findViewById(R.id.friendlist_view);
        friendlistview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        friendlistview.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        onlineUser_id = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUser_id);
        usserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        context=getContext();
        DisplayAllFriends();

        search.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    {

                    // Your piece of code on keyboard search click
                    Intent intent = new Intent(getContext(), FindFriendsActivity.class);
                    intent.putExtra("name", search.getText().toString());
                    startActivity(intent);

                    return true;
                    }
                return false;
                }
        });


        return view;
        }


    private void DisplayAllFriends()
        {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsRef, Friends.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendsviewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsviewHolder holder, int position, @NonNull final Friends model)
                {

                holder.all_profile.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
                //holder.linearLayout.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));

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
                            Glide.with(context).load(profileImage).into(holder.all_profile);
                            //Picasso.get().load(profileImage).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE).into(holder.all_profile);

                            holder.itemView.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                    {


                                    Intent profileintent = new Intent(getContext(), SearchProfileActivity.class);
                                    profileintent.putExtra("visit_user_id", usersIDs);
                                    startActivity(profileintent);

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
            public FriendsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(context).inflate(R.layout.all_users_display_layout, parent, false);
                return new FriendsviewHolder(view);
                }
        };
        friendlistview.setAdapter(adapter);
        adapter.startListening();
        }


    public class FriendsviewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView all_profile;
        public TextView all_username, friendsdate;
        private LinearLayout linearLayout;

        public FriendsviewHolder(@NonNull View itemView)
            {

            super(itemView);
            friendlistview = itemView.findViewById(R.id.friendlist_view);
            all_profile = itemView.findViewById(R.id.all_user_display_layout_profileimage);

            all_username = itemView.findViewById(R.id.all_user_display_layout_profilename);
            friendsdate = itemView.findViewById(R.id.all_user_display_layout_status);
            linearLayout=itemView.findViewById(R.id.ll5);

            }
    }

    @Override
    public void onStop()
        {
      // DisplayAllFriends();
        super.onStop();
        }
}