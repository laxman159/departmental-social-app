package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.e.thedept20.Adapters.Comments;
import com.e.thedept20.Adapters.Posts;
import com.e.thedept20.Chat.ChatUserActivity;
import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.Notes.util.AppUtils;
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
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewAllDetaillPostActivity extends AppCompatActivity
{
    FirebaseRecyclerAdapter<Comments, CommentsViewHolder> apapter;
    Toolbar toolbar;
    private ZoomageView imageView;
    private TextView textView, addpost, update_descp,header,desc;
    private ImageButton imageButton;
    private String PostKey, post_username, currentuserID, databaseuserID, dp,dp2;
    private DatabaseReference clickpostRef;
    private FirebaseAuth mAuth;
    private RecyclerView commentlist;
    private DatabaseReference usersRef, postRef;
    private CircleImageView comment_civ,headerCiv;
    private EditText addcomment;
    private Context mContex;
     private FirebaseRecyclerOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_detaill_post);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        post_username = getIntent().getExtras().get("username").toString();
        header=findViewById(R.id.username_details_tv);
        header.setText(post_username);

        desc=findViewById(R.id.alldetail_discription1);
        desc.setText(post_username+" :");
        toolbar = findViewById(R.id.alldetain_tool);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.alldetail_discription);
        imageView = findViewById(R.id.alldetail_photo);

        headerCiv=findViewById(R.id.civ_allpost_activity);
        comment_civ = findViewById(R.id.comment_civ);
        addcomment = findViewById(R.id.comment_add);
        addpost = findViewById(R.id.comment_post);
        update_descp = findViewById(R.id.comments_update_discription);



        mAuth = FirebaseAuth.getInstance();
        currentuserID = mAuth.getCurrentUser().getUid();
        //dp=getIntent().getExtras().get("dp").toString();


        clickpostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);


        mContex=getApplicationContext();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");



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
                    dp2 = dataSnapshot.child("ProfileImage").getValue().toString();

                   Glide.with(getApplicationContext())
                           .load(dp2)
                           .apply(RequestOptions.skipMemoryCacheOf(false))
                           .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                           .into(headerCiv);
                    //Picasso.get().load(dp2).into(headerCiv);


                    textView.setText(description);
                    Glide.with(getApplicationContext()).load(image).into(imageView);


                    update_descp.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                            {
                            if (currentuserID.equals(databaseuserID))

                                {
                                EditPost(description);
                                } else if (currentuserID != (databaseuserID))
                                {
                                new SweetAlertDialog(ViewAllDetaillPostActivity.this, SweetAlertDialog.WARNING_TYPE)
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
        usersRef.child(currentuserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    dp = dataSnapshot.child("profileImage").getValue().toString();
                   // Picasso.get().load(dp).into(comment_civ);
                    Glide.with(getApplicationContext()).load(dp).into(comment_civ);
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

                usersRef.child(currentuserID).addValueEventListener(new ValueEventListener()
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
        SetComment();

        }

    private void  SetComment()
        {

        commentlist = findViewById(R.id.alldetail_rv);
        //commentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentlist.setLayoutManager(linearLayoutManager);
         options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(postRef, Comments.class)
                .build();


        apapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model)
                {
                holder.username.setText(model.getUsername());
                holder.time.setText( model.getTime());
                holder.comment.setText(" " + model.getComment());
                holder.date.setText(" "+model.getDate());

               // Glide.with(getApplicationContext()).load(model.getProfileimage()).into(holder.comment_user_civ);
                Picasso.get().load(model.getProfileimage()).placeholder(R.drawable.profile_img).into(holder.comment_user_civ);

                }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comment_layout, parent, false);

                return new CommentsViewHolder(view);
                }
        };

        commentlist.setAdapter(apapter);
        apapter.notifyDataSetChanged();
        apapter.startListening();
        }

    private void ValidateComment(String username)
        {
        String commenttext = addcomment.getText().toString();
        if (TextUtils.isEmpty(commenttext))
            {
            //Toast.makeText(this, "Write a comment first", Toast.LENGTH_SHORT).show();
            } else
            {
            AppUtils.hideKeyboard(getCurrentFocus());
            Calendar callforDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
            final String SaveCurrentDate = currentDate.format(callforDate.getTime());

            Calendar callforTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
            SimpleDateFormat currentTimesec = new SimpleDateFormat("ss aa");
            final String SaveCurrentTime = currentTime.format(callforTime.getTime());
            final  String seconds=currentTimesec.format(callforTime.getTime());



            final String randonkey = currentuserID + SaveCurrentDate + SaveCurrentTime+ seconds ;

            HashMap commentsMap = new HashMap();
            commentsMap.put("Uid", currentuserID);
            commentsMap.put("Comment", commenttext);
            commentsMap.put("Date", SaveCurrentDate);
            commentsMap.put("Time", SaveCurrentTime);
            commentsMap.put("Username", username);
            commentsMap.put("profileimage",dp);
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
                            Toast.makeText(ViewAllDetaillPostActivity.this, "Error,try again", Toast.LENGTH_SHORT).show();

                            }
                    });

            }
        }

    private void EditPost(String description)
        {

        final EditText inputfield = new EditText(ViewAllDetaillPostActivity.this);
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


        //  AlertDialog.Builder builder = new AlertDialog.Builder(ViewAllDetaillPostActivity.this);
        // builder.setTitle("Description");


        //inputfield.setFocusable(true);

        // builder.setView(inputfield);
        //builder.setPositiveButton("Update", new DialogInterface.OnClickListener()
        //  {
        //  @Override
        // public void onClick(DialogInterface dialog, int which)
        {
        clickpostRef.child("Description").setValue(inputfield.getText().toString());
        //Toast.makeText(ViewAllDetaillPostActivity.this, "Post Updated ", Toast.LENGTH_SHORT).show();

        }

        }

    public boolean onCreateOptionsMenu(Menu menu)
        {
        getMenuInflater().inflate(R.menu.del_menu, menu);
        return super.onCreateOptionsMenu(menu);
        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
        {

        int id = item.getItemId();
        if (id == R.id.delete)
            {

            if (currentuserID.equals(databaseuserID))
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
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
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

            }


        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStart()
        {
        super.onStart();
        //apapter.startListening();
        }
}