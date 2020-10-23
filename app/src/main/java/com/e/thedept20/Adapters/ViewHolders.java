package com.e.thedept20.Adapters;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.thedept20.R;
import com.e.thedept20.ViewAllDetaillPostActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolders extends RecyclerView.ViewHolder
{
    public TextView fullname,  Date, Description, no_of_likes, userdescriptionName,post,totalComments,time1;
    public CircleImageView ProfileImage,comment_civs;
    public ImageView PostImage;
    public ImageButton like_post_button, comment_post_button;
    public LinearLayout layout;
    public EditText addComment;
    public ImageButton imageButton;
    int countLikes;
    String currentUserID;
    DatabaseReference LikesRef,UserRef;

    public ViewHolders(@NonNull View itemView)
        {
        super(itemView);
        fullname = itemView.findViewById(R.id.post_user_name);
        post=itemView.findViewById(R.id.comment_post);
        post.setVisibility(View.INVISIBLE);
        addComment=itemView.findViewById(R.id.comment_add);
        imageButton=itemView.findViewById(R.id.addcomment);
        imageButton.setVisibility(View.VISIBLE);

        comment_civs=itemView.findViewById(R.id.comment_civ);
        ProfileImage = itemView.findViewById(R.id.post_profile_image);
        time1=itemView.findViewById(R.id.post_time);
        totalComments=itemView.findViewById(R.id.post_totalcomments);
        Date = itemView.findViewById(R.id.post_date);
        Description = itemView.findViewById(R.id.post_descryption);
        PostImage = itemView.findViewById(R.id.post_images);
        like_post_button = itemView.findViewById(R.id.like_button);
        userdescriptionName = itemView.findViewById(R.id.post_discription_user_name);
        no_of_likes = itemView.findViewById(R.id.likes_number);
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);




        layout = itemView.findViewById(R.id.container);

        }

    public void setLikeButtonShade(final String postKey)
        {
        LikesRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.child(postKey).hasChild(currentUserID))
                    {
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    like_post_button.setImageResource(R.drawable.heart_filled);
                    no_of_likes.setText(countLikes + " Likes");
                    } else
                    {
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    like_post_button.setImageResource(R.drawable.heart_unfilled);
                    no_of_likes.setText(countLikes + " Likes");
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });

        }


}
