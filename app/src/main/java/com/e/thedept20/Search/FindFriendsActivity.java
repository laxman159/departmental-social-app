package com.e.thedept20.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.FindFriendHolder;
import com.e.thedept20.Adapters.FindFriends;
import com.e.thedept20.R;
import com.e.thedept20.SearchProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

//import android.widget.Toolbar;

public class FindFriendsActivity extends AppCompatActivity
{
    Toolbar toolbar;
    private Toolbar mToolbar;
    private ImageButton searchbutton;
    private EditText SearchInputText;
    private RecyclerView SearchResultList;
    private FirebaseRecyclerOptions<FindFriends> options;
    private FirebaseRecyclerAdapter<FindFriends, FindFriendHolder> adapter1;
    private DatabaseReference allusersdatabaseRef;
    private String search;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        SearchResultList = findViewById(R.id.findfriends_search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));
        allusersdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        searchbutton = findViewById(R.id.findfriends_search_button);
        SearchInputText = findViewById(R.id.findfriends_searchbox);
        toolbar = findViewById(R.id.abl_aff);
        search = getIntent().getExtras().get("name").toString();
        SearchForUsers(search);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                String searchboxinput = SearchInputText.getText().toString();
                SearchForUsers(searchboxinput);

                }
        });


        }


    private void SearchForUsers(String searchboxinput)
        {
        //Toast.makeText(this, "Searching ", Toast.LENGTH_LONG).show();
        Query searchUser = allusersdatabaseRef.orderByChild("Fullname")
                .startAt(searchboxinput.toUpperCase()).endAt(searchboxinput.toLowerCase() + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(searchUser, FindFriends.class)
                .build();
        adapter1 = new FirebaseRecyclerAdapter<FindFriends, FindFriendHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendHolder holder, final int position, @NonNull FindFriends model)
                {
                final String usersIDs = getRef(position).getKey();
                Glide.with(getApplicationContext()).load(model.getProfileImage()).into(holder.all_profile);
//                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile).networkPolicy(NetworkPolicy.OFFLINE, NetworkPolicy.NO_CACHE).into(holder.all_profile, new Callback()
//                {
//                    @Override
//                    public void onSuccess()
//                        {
//
//                        }
//
//                    @Override
//                    public void onError(Exception e)
//                        {
//                        Toast.makeText(FindFriendsActivity.this, "Could not get image " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                });

                holder.all_status.setText(model.getStatus());
                holder.all_username.setText(model.getFullname());
                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {


                        Intent profileintent = new Intent(getApplicationContext(), SearchProfileActivity.class);
                        profileintent.putExtra("visit_user_id", usersIDs);
                        startActivity(profileintent);

                        }


                });


                }

            @NonNull
            @Override
            public FindFriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                return new FindFriendHolder(view);
                }
        };

        SearchResultList.setAdapter(adapter1);
        adapter1.startListening();


        }

    @Override
    protected void onStart()
        {
        super.onStart();
        adapter1.startListening();
        }
}
