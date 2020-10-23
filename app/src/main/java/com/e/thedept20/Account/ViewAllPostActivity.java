package com.e.thedept20.Account;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e.thedept20.Adapters.Comments;
import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.ViewAllDetaillPostActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAllPostActivity extends AppCompatActivity
{
    public TextView fullname, fullname1, Time, Date, Description, no_of_likes;
    public CircleImageView ProfileImage;
    public ImageView PostImage;
    public ImageButton like_post_button, comment_post_button, back;
    private  FirebaseRecyclerAdapter<Comments, ViewAllPostActivity.CommentsViewHolder> apapter;
    int countLikes, countComments;
    DatabaseReference LikesRef, postRef, clickpostRef;
    Boolean LikeChecker = false;
    String userID;
    private EditText addcomment;

    private Toolbar toolbar;
    private String currentUserID;
    private String title, post, time, PostKey, description, profileImage, databaseuserID;
    private FirebaseAuth mAuth;
    private TextView username_comment, addpost, update_descp,header_username;
    private RecyclerView commentlist;
    private DatabaseReference usersRef;
    private CircleImageView headerCiv;
    private ImageButton goback;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_detaill_post);

        fullname = findViewById(R.id.post_user_name);
        //fullname1 = findViewById(R.id.post_discription_user_name);
        goback=findViewById(R.id.back);
        goback.setVisibility(View.VISIBLE);

        header_username=findViewById(R.id.username_details_tv);
        headerCiv=findViewById(R.id.civ_allpost_activity);
        ProfileImage = findViewById(R.id.comment_civ);
        Description = findViewById(R.id.alldetail_discription);
        PostImage = findViewById(R.id.alldetail_photo);
        addcomment = findViewById(R.id.comment_add);
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        title = getIntent().getStringExtra("username");
        toolbar = findViewById(R.id.alldetain_tool);
        update_descp = findViewById(R.id.comments_update_discription);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        header_username.setText(title);
        linearLayout=findViewById(R.id.llggeader);
        linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                finish();
                }
        });
        //back = findViewById(R.id.avadp_back);
//        back.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//                {
//                finish();
//                }
//        });

        addpost = findViewById(R.id.comment_post);

        mAuth = FirebaseAuth.getInstance();


        post = getIntent().getStringExtra("post");
        time = getIntent().getStringExtra("time");
        userID = getIntent().getStringExtra("uid");
        PostKey = getIntent().getStringExtra("postkey");
        description = getIntent().getStringExtra("description");
        profileImage = getIntent().getStringExtra("profileimage");

        Glide.with(getApplicationContext())
                .load(profileImage)
                .apply(RequestOptions.skipMemoryCacheOf(false))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(headerCiv);
       // Picasso.get().load(profileImage).into(headerCiv);


        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        clickpostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        Glide.with(getApplicationContext()).load(post).into(PostImage);

       // Glide.with(getApplicationContext()).load(profileImage).into(ProfileImage);
       // Picasso.get().load(profileImage).into(ProfileImage);

        Description.setText(description);
//        fullname1.setText(title);
//        setLikeButtonShade(PostKey);
        //countComments(PostKey);

        commentlist = findViewById(R.id.alldetail_rv);
        //commentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentlist.setLayoutManager(linearLayoutManager);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");
        SetComment();

        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    String dp = dataSnapshot.child("profileImage").getValue().toString();
                    // Picasso.get().load(dp).into(comment_civ);
                    Glide.with(getApplicationContext()).load(dp).into(ProfileImage);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });

        clickpostRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    final String description = dataSnapshot.child("Description").getValue().toString();
                    String image = dataSnapshot.child("PostImage").getValue().toString();
                    databaseuserID = dataSnapshot.child("Uid").getValue().toString();

                    Description.setText(description);


                    update_descp.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                            if (currentUserID.equals(databaseuserID))

                                {
                                EditPost(description);
                                } else if (currentUserID != (databaseuserID))
                                {
                                new SweetAlertDialog(ViewAllPostActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("This picture is not in your account")
                                        .show();
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
        addpost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {

                usersRef.child(currentUserID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                        if (dataSnapshot.exists())
                            {
                            String username = dataSnapshot.child("Username").getValue().toString();
                            ValidateComment(username);
                            addcomment.setText(null);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
        {
        if (!userID.isEmpty())
            {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.del_menu, menu);

            } else
            {
            // Toast.makeText(this, "nigga", Toast.LENGTH_LONG).show();
            }
        return true;

        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {
        switch (item.getItemId())
            {

            case R.id.delete:
                if (currentUserID.equals(databaseuserID))
                    {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Delete Post?")
                            .setConfirmText("Delete")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                            {
                                @Override
                                public void onClick(SweetAlertDialog sDialog)
                                    {

                                    clickpostRef.removeValue();
                                    finish();
                                    //startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                    }
                            }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                            {
                            sweetAlertDialog.dismissWithAnimation();
                            }
                    })
                            .show();


                    } else
                    {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("This picture is not in your account")
                            .show();

                    //Toast.makeText(this, "This is not in your account ", Toast.LENGTH_SHORT).show();
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);

            }

        }


    private void SetComment()
        {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(postRef, Comments.class)
                .build();


        apapter = new FirebaseRecyclerAdapter<Comments, ViewAllPostActivity.CommentsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull ViewAllPostActivity.CommentsViewHolder holder, int position, @NonNull Comments model)
                {
                holder.username.setText(model.getUsername());
                holder.time.setText(model.getTime());
                holder.comment.setText(" " + model.getComment());
                holder.date.setText(model.getDate());
               // Glide.with(getApplicationContext()).load(model.getProfileimage()).into(holder.comment_user_civ);
                Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.user).into(holder.comment_user_civ);

                }

            @NonNull
            @Override
            public ViewAllPostActivity.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comment_layout, parent, false);

                return new ViewAllPostActivity.CommentsViewHolder(view);
                }
        };

        commentlist.setAdapter(apapter);
        apapter.startListening();
        }

    @Override
    protected void onStart()
        {
        super.onStart();
        //SetComment();
        }

    private void ValidateComment(String username)
        {
        String commenttext = addcomment.getText().toString();
        if (TextUtils.isEmpty(commenttext))
            {
            Toast.makeText(this, "Write a comment first", Toast.LENGTH_SHORT).show();
            } else
            {
            Calendar callforDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
            final String SaveCurrentDate = currentDate.format(callforDate.getTime());

            Calendar callforTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat currentTimesec = new SimpleDateFormat("ss aa");
            final String SaveCurrentTime = currentTime.format(callforTime.getTime());
            final  String seconds=currentTimesec.format(callforTime.getTime());

            final String randonkey = currentUserID + SaveCurrentDate + SaveCurrentTime+seconds;

            HashMap commentsMap = new HashMap();
            commentsMap.put("Uid", currentUserID);
            commentsMap.put("Comment", commenttext);
            commentsMap.put("Date", SaveCurrentDate);
            commentsMap.put("Time", SaveCurrentTime);
            commentsMap.put("Username", username);
            commentsMap.put("profileimage",profileImage);
            postRef.child(randonkey).updateChildren(commentsMap)
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
                            Toast.makeText(getApplicationContext(), "Error,try again", Toast.LENGTH_SHORT).show();

                            }
                    });

            }
        }

    private void EditPost(String description)
        {
        final EditText inputfield = new EditText(ViewAllPostActivity.this);
        inputfield.setText(description);
        inputfield.setFocusable(true);

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("New Description?")
                .setCustomView(inputfield)
                .setConfirmText("Update")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sDialog)
                        {
                        clickpostRef.child("Description").setValue(inputfield.getText().toString());
                        sDialog.dismissWithAnimation();
                        }
                })
                .show();


        clickpostRef.child("Description").setValue(inputfield.getText().toString());
        //Toast.makeText(ViewAllDetaillPostActivity.this, "Post Updated ", Toast.LENGTH_SHORT).show();

        }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView username, date, time, comment;
        private CircleImageView comment_user_civ;

        public CommentsViewHolder(@NonNull View itemView)
            {
            super(itemView);
            username = itemView.findViewById(R.id.all_comment_username);
            date = itemView.findViewById(R.id.all_comment_date);
            time = itemView.findViewById(R.id.all_comment_time);
            comment = itemView.findViewById(R.id.all_comment_text);
            comment_user_civ=itemView.findViewById(R.id.commnet_user_civ);



            }
    }


}



