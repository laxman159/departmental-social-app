package com.e.thedept20.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesApapter extends RecyclerView.Adapter<MessagesApapter.MessageViewHolder>
{


    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userdatabaseRef;

    public MessagesApapter(List<Messages> userMessagesList)
        {
        this.userMessagesList = userMessagesList;
        }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
        View V = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(V);
        }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
        {
        String messegeSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromuserID = messages.getFrom();
        String fromMessageType = messages.getType();

        userdatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromuserID);
        userdatabaseRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    String image = dataSnapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.recieverProfileImage);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        if (fromMessageType.equals("text"))
            {
            // holder.recieverMessageText.setVisibility(View.INVISIBLE);
            // holder.recieverProfileImage.setVisibility(View.INVISIBLE);
            holder.senderimage.setVisibility(View.GONE);
            holder.receiverimage.setVisibility(View.GONE);
            if (fromuserID.equals(messegeSenderID))
                {
                holder.recieverMessageText.setVisibility(View.INVISIBLE);
                holder.recieverProfileImage.setVisibility(View.INVISIBLE);
                // holder.sendermessageText.setB(R.layout.chat_item_left);
                holder.sendermessageText.setTextColor(Color.WHITE);
                holder.sendermessageText.setGravity(Gravity.LEFT);
                holder.sendermessageText.setText(messages.getMessage());
                holder.dateRight.setText(messages.getTime());
                holder.l2.setVisibility(View.INVISIBLE);
                } else
                {
                holder.sendermessageText.setVisibility(View.INVISIBLE);

                //  holder.recieverMessageText.setVisibility(View.VISIBLE);
                //  holder.recieverProfileImage.setVisibility(View.VISIBLE);

                holder.recieverMessageText.setBackgroundResource(R.drawable.receiver_message_textbackground);
                holder.recieverMessageText.setTextColor(Color.WHITE);
                holder.recieverMessageText.setGravity(Gravity.LEFT);
                holder.recieverMessageText.setText(messages.getMessage());

                holder.dateLeft.setText(messages.getTime());
                holder.l1.setVisibility(View.INVISIBLE);

                }
            } else if (fromMessageType.equals("image"))
            {


            if (fromuserID.equals(messegeSenderID))
                {
                holder.sendermessageText.setVisibility(View.GONE);
                holder.recieverMessageText.setVisibility(View.GONE);
                holder.recieverProfileImage.setVisibility(View.INVISIBLE);
                holder.senderimage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.senderimage);
                } else
                {
                holder.sendermessageText.setVisibility(View.GONE);
                holder.recieverMessageText.setVisibility(View.GONE);
                holder.recieverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverimage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.receiverimage);
                }
            } else
            {

            if (fromuserID.equals(messegeSenderID))
                {
                holder.sendermessageText.setVisibility(View.GONE);
                holder.recieverMessageText.setVisibility(View.GONE);
                holder.recieverProfileImage.setVisibility(View.INVISIBLE);
                holder.senderimage.setVisibility(View.VISIBLE);
                holder.senderimage.setBackgroundResource(R.drawable.docx);
                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                        }
                });

                } else
                {
                holder.sendermessageText.setVisibility(View.GONE);
                holder.recieverMessageText.setVisibility(View.GONE);
                holder.recieverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverimage.setVisibility(View.VISIBLE);
                holder.receiverimage.setBackgroundResource(R.drawable.docx);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                        {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                        }
                });

                }
            }


        }

    @Override
    public int getItemCount()
        {

        return userMessagesList.size();
        }

    static class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView sendermessageText, recieverMessageText,dateLeft,dateRight;
        CircleImageView recieverProfileImage;
        ZoomageView senderimage, receiverimage;
        LinearLayout l1,l2;

        public MessageViewHolder(@NonNull View itemView)
            {
            super(itemView);
            sendermessageText = itemView.findViewById(R.id.message_sender_message_text);
            recieverMessageText = itemView.findViewById(R.id.message_reciver_message_text);
            recieverProfileImage = itemView.findViewById(R.id.message_profile_image);
            senderimage = itemView.findViewById(R.id.message_sender_message_image);
            receiverimage = itemView.findViewById(R.id.message_reciver_message_image);
            dateLeft=itemView.findViewById(R.id.date_chat_left);
            dateRight=itemView.findViewById(R.id.date_chat_right);
            l1=itemView.findViewById(R.id.ll6right);
            l2=itemView.findViewById(R.id.ll6left);

            }
    }
}
