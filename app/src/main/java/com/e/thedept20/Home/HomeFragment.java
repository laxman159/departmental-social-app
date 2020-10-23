package com.e.thedept20.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e.thedept20.Adapters.Posts;
import com.e.thedept20.Adapters.ViewHolders;
import com.e.thedept20.Chat.ChatActivity;
import com.e.thedept20.Chat.ChatUserActivity;
import com.e.thedept20.Notes.ui.activity.AddNoteActivity;
import com.e.thedept20.Notes.ui.activity.NotesListActivity;
import com.e.thedept20.R;
import com.e.thedept20.RecyclerViewEmptySupport;
import com.e.thedept20.Search.FindFriendsActivity;
import com.e.thedept20.StudentsProtalActivity;
import com.e.thedept20.Utils.OnlineOfflineStatus;
import com.e.thedept20.ViewAllDetaillPostActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment
{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Boolean LikeChecker = false;
    private RecyclerView postList;
    private FirebaseAuth mAuth;
    private String currentuserID;
    private FirebaseRecyclerOptions<Posts> options;
    private FirebaseRecyclerAdapter<Posts, ViewHolders> adapter;
    private ImageButton imageButton, imageButton1;
    private Context mContext;
    private DatabaseReference UserRef, Postref, LikesRef, FriendsRef;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context context;
    private FloatingActionButton floatingActionButton;

    public HomeFragment()
        {
        // Required empty public constructor
        }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2)
        {
        HomeFragment fragment = new HomeFragment();
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
        // ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
        {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        floatingActionButton=view.findViewById(R.id.notes_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                startActivity(new Intent(getContext(), NotesListActivity.class));
                }
        });



        FriendsRef = FirebaseDatabase.getInstance().getReference().child(currentuserID);
        postList = view.findViewById(R.id.all_user_post_list);
        //postList.setEmptyView(view.findViewById(R.id.toDoEmptyView));
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        mContext = getContext();

        postList.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if (dy > 0)
                floatingActionButton.hide();
            else if (dy < 0)
                floatingActionButton.show();
            }
        });
        //setSupportActionBar(toolbar);

        DisplayAllUsersPosts();


        // ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        return view;
        }

    public void DisplayAllUsersPosts()
        {
        Query sortpostinDescendingorder = Postref.orderByChild("Counter");
        options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(sortpostinDescendingorder, Posts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Posts, ViewHolders>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolders holder, int position, @NonNull final Posts model)
                {

                holder.ProfileImage.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_transition_animation));
                holder.layout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

                holder.time1.setText(model.getTime());
                final String PostKey = getRef(position).getKey();




                Postref.child(PostKey).child("Comments").addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if (snapshot.hasChildren())
                        {
                            holder.totalComments.setText("View all "+snapshot.getChildrenCount()+" comments");
                        }
                        else {
                            holder.totalComments.setText(" ");
                            }
                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                });

                holder.Date.setText(model.getDate());
                holder.Description.setText(model.getDescription());
               Glide.with(mContext)
                       .load(model.getProfileImage())
                       .apply(RequestOptions.skipMemoryCacheOf(false))
                       .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                       .into(holder.ProfileImage);
                UserRef.child(currentuserID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                        if (snapshot.child("profileImage").exists())
                            {
                            String dp=snapshot.child("profileImage").getValue().toString();
                            Picasso.get().load(dp).into(holder.comment_civs);
                            }

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                });

                Glide.with(mContext).load(model.getPostImage()).into(holder.PostImage);

              //Picasso.get().load(model.getProfileImage()).into(holder.ProfileImage);

                holder.fullname.setText(model.getFullname());
                holder.userdescriptionName.setText(model.getFullname());
                holder.setLikeButtonShade(PostKey);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                        Intent clickPostIntent = new Intent(getContext(), ViewAllDetaillPostActivity.class);
                        // clickPostIntent.putExtra("dp",model.getProfileImage());
                        clickPostIntent.putExtra("username", model.getFullname());
                        clickPostIntent.putExtra("Postkey", PostKey);
                        startActivity(clickPostIntent);
                        }
                });

                holder.imageButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {

                        UserRef.child(currentuserID).addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                if (dataSnapshot.exists())
                                    {
                                    String username = dataSnapshot.child("Username").getValue().toString();

                                    String commenttext = holder.addComment.getText().toString();
                                    if (TextUtils.isEmpty(commenttext))
                                        {
                                       // Toast.makeText(getContext(), "Write a comment first", Toast.LENGTH_SHORT).show();
                                        } else
                                        {
                                        try {
                                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                                        } catch (Exception e) {
                                        // TODO: handle exception
                                        }
                                        Calendar callforDate = Calendar.getInstance();
                                        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
                                        final String SaveCurrentDate = currentDate.format(callforDate.getTime());

                                        Calendar callforTime = Calendar.getInstance();
                                        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                        final String SaveCurrentTime = currentTime.format(callforTime.getTime());

                                        final String randonkey = currentuserID + SaveCurrentDate + SaveCurrentTime;

                                        HashMap commentsMap = new HashMap();
                                        commentsMap.put("Uid", currentuserID);
                                        commentsMap.put("Comment", commenttext);
                                        commentsMap.put("Date", SaveCurrentDate);
                                        commentsMap.put("Time", SaveCurrentTime);
                                        commentsMap.put("Username", username);
                                        Postref.child(PostKey).child("Comments").child(randonkey).updateChildren(commentsMap)
                                                .addOnSuccessListener(new OnSuccessListener()
                                                {
                                                    @Override
                                                    public void onSuccess(Object o)
                                                        {
                                                        //Toast.makeText(CommentActivity.this, "Commented", Toast.LENGTH_SHORT).show();


                                                        }
                                                })
                                                .addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                        {
                                                        Toast.makeText(getContext(), "Error,try again", Toast.LENGTH_SHORT).show();

                                                        }
                                                });

                                        }
                                    holder.addComment.setText(null);
                                    }

                                }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                        });


                        }
                });


                holder.like_post_button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                        LikeChecker = true;

                        LikesRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                if (LikeChecker.equals(true))
                                    {
                                    if (dataSnapshot.child(PostKey).hasChild(currentuserID))
                                        {
                                        LikesRef.child(PostKey).child(currentuserID).removeValue();
                                        LikeChecker = false;
                                        } else
                                        {
                                        LikesRef.child(PostKey).child(currentuserID).setValue(true);
                                        LikeChecker = false;

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

                }

            @NonNull
            @Override
            public ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainfeed_style, parent, false);
                return new ViewHolders(view);
                }

        };

        postList.setAdapter(adapter);
        adapter.startListening();
        }

    @Override
    public void onStart()
        {
        //adapter.startListening();
        super.onStart();
        //new OnlineOfflineStatus().UpdateUserStatus("online");
        //DisplayAllUsersPosts();

        }

    @Override
    public void onStop()
        {
        postList.setAdapter(adapter);
        adapter.startListening();
        // ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
       // new OnlineOfflineStatus().UpdateUserStatus("offline");
        super.onStop();
        }

    @Override
    public void onDestroy()
        {

        super.onDestroy();

        }

}