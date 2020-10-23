package com.e.thedept20.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.thedept20.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendHolder extends RecyclerView.ViewHolder
{
    public CircleImageView all_profile;
    public TextView all_username, all_status;

    public FindFriendHolder(@NonNull View itemView)
        {
        super(itemView);
        all_profile = itemView.findViewById(R.id.all_user_display_layout_profileimage);
        all_username = itemView.findViewById(R.id.all_user_display_layout_profilename);
        all_status = itemView.findViewById(R.id.all_user_display_layout_status);
        }
}