package com.e.thedept20.Adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.thedept20.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoticeBoardViewHolder extends RecyclerView.ViewHolder
{
    public TextView fullname, Date, Description;
    public CircleImageView ProfileImage;
    public RelativeLayout layout;


    public NoticeBoardViewHolder(@NonNull View itemView)
        {
        super(itemView);
        fullname = itemView.findViewById(R.id.tv_title);
        ProfileImage = itemView.findViewById(R.id.img_user);
        Date = itemView.findViewById(R.id.tv_date);
        Description = itemView.findViewById(R.id.tv_description);
        layout=itemView.findViewById(R.id.containernoticeboard);


        }




}
