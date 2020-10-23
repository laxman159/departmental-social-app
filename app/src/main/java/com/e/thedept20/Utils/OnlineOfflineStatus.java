package com.e.thedept20.Utils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OnlineOfflineStatus extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String currentuser;


    @Override
    protected void onStart()
        {
        super.onStart();
        }


    public void UpdateUserStatus(String state)
        {
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference();
        currentuser = mAuth.getCurrentUser().getUid();
        String savecurrentime, savecurrentdate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MM dd,yyyy");
        savecurrentdate = currentdate.format(calendar.getTime());

        SimpleDateFormat currentime = new SimpleDateFormat("hh:mm a");
        savecurrentime = currentime.format(calendar.getTime());

        HashMap<String, Object> onlinestateMap = new HashMap<>();
        onlinestateMap.put("time", savecurrentime);
        onlinestateMap.put("date", savecurrentdate);
        onlinestateMap.put("state", state);


        userRef.child("Users").child(currentuser).child("Userstate")
                .updateChildren(onlinestateMap);
        }

//    public void UpdateUserStatusChat(String state)
//        {
//        mAuth = FirebaseAuth.getInstance();
//        userRef = FirebaseDatabase.getInstance().getReference();
//        currentuser = mAuth.getCurrentUser().getUid();
//        String savecurrentime, savecurrentdate;
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat currentdate = new SimpleDateFormat("MM dd,yyyy");
//        savecurrentdate = currentdate.format(calendar.getTime());
//
//        SimpleDateFormat currentime = new SimpleDateFormat("hh:mm a");
//        savecurrentime = currentime.format(calendar.getTime());
//
//        HashMap<String, Object> onlinestateMap = new HashMap<>();
//        onlinestateMap.put("time", savecurrentime);
//        onlinestateMap.put("date", savecurrentdate);
//        onlinestateMap.put("state", state);
//
//
//        userRef.child("Users").child(currentuser).child("UserstateChat")
//                .updateChildren(onlinestateMap);
//
//        }

}
