package com.e.thedept20.Chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.thedept20.Adapters.Messages;
import com.e.thedept20.Adapters.MessagesApapter;
import com.e.thedept20.Adapters.User;
import com.e.thedept20.Notifications.APIService;
import com.e.thedept20.Notifications.Client;
import com.e.thedept20.Notifications.Data;
import com.e.thedept20.Notifications.Response;
import com.e.thedept20.Notifications.Sender;
import com.e.thedept20.Notifications.Token;
import com.e.thedept20.R;
import com.e.thedept20.Utils.OnlineOfflineStatus;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity
{
    private final List<Messages> messagesList = new ArrayList<>();
    APIService apiService;
    boolean notify = false;
    private Toolbar chattoolbar;
    private ImageView sendimagefilebutton, backbtn;
    private ImageButton sendmessagebutton;
    private EditText usermessageinput;
    private RecyclerView usermessegeslist;
    private String messageReceiverID, messageReceiverName, messagesenderID, SaveCurrentDate, SaveCurrentTime, checker = null, myUrl = "null";
    private TextView recievername, userlastseen;
    private CircleImageView receiverprofileimage;
    private DatabaseReference rootRef, usersRef;
    private FirebaseAuth mAuth;
    private LinearLayoutManager linearLayoutManager;
    private MessagesApapter messagesApapter;
    private ProgressDialog loadingbar;
    private OnlineOfflineStatus onlineOfflineStatus = new OnlineOfflineStatus();
    private Uri fileUri;
    private StorageTask uploadTask;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        // messageReceiverName = getIntent().getExtras().get("Username").toString();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        messagesenderID = mAuth.getCurrentUser().getUid();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Sending");
        pDialog.setCancelable(false);


        Initilizefields();

        DisplayReceiverinfo();

        backbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                finish();
                }
        });


        sendmessagebutton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
                {
                notify = true;
                Sendmessage();
                usermessageinput.setText("");
                }
        });
        FetchMessages();
        sendimagefilebutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                CharSequence[] options = new CharSequence[]
                        {
                                "Images",
                                "PDF's",
                                "MS Words Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select File");


                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                        {
                        if (which == 0)
                            {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "select image"), 438);

                            }
                        if (which == 1)
                            {
                            checker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(intent, "select pdf"), 438);


                            }
                        if (which == 2)
                            {
                            checker = "docx";
                            checker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(Intent.createChooser(intent, "select word file "), 438);


                            }
                        }
                });
                builder.show();
                }
        });
        }

    private void FetchMessages()
        {
        rootRef.child("Messages").child(messagesenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {
                        if (dataSnapshot.exists())
                            {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesApapter.notifyDataSetChanged();
                            usermessegeslist.scrollToPosition(messagesList.size() - 1);

                            }

                        }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {

                        }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                        {

                        }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                        {

                        }
                });
        }

    private void Sendmessage()
        {
        final String messagetext = usermessageinput.getText().toString().trim();
        if (TextUtils.isEmpty(messagetext))
            {
            Toast.makeText(this, "Type a message first", Toast.LENGTH_SHORT).show();
            } else
            {
            String messege_sender_ref = "Messages/" + messagesenderID + "/" + messageReceiverID;
            String messager_receiver_ref = "Messages/" + messageReceiverID + "/" + messagesenderID;
            DatabaseReference usersmessage_key = rootRef.child("Messages").child(messagesenderID).child(messageReceiverID).push();
            String message_push_id = usersmessage_key.getKey();

            Calendar callforDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            SaveCurrentDate = currentDate.format(callforDate.getTime());


            Calendar callforTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("h:mm aa ");
           // HH:mm aa
            SaveCurrentTime = currentTime.format(callforTime.getTime());

            Map messegebody = new HashMap();
            messegebody.put("Message", messagetext);
            messegebody.put("Time", SaveCurrentTime);
            messegebody.put("Date", SaveCurrentDate);
            messegebody.put("Type", "text");
            messegebody.put("From", messagesenderID);

            Map messagebodydetail = new HashMap();
            messagebodydetail.put(messege_sender_ref + "/" + message_push_id, messegebody);
            messagebodydetail.put(messager_receiver_ref + "/" + message_push_id, messegebody);

            rootRef.updateChildren(messagebodydetail).addOnSuccessListener(new OnSuccessListener()
            {
                @Override
                public void onSuccess(Object o)
                    {
                    //Toast.makeText(ChatActivity.this, "Message send ", Toast.LENGTH_SHORT).show();
                    usermessageinput.setText("");

                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(messagesenderID);
                    database.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                            User user = snapshot.getValue(User.class);

                            if (notify)
                                {
                                sendNotification(messageReceiverID, user.getUsername(), messagetext);
                                }
                            notify = false;
                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                    });
                    }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                    {
                    Toast.makeText(ChatActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    usermessageinput.setText("");
                    }
            });
            }
        }

    private void sendNotification(final String messageReceiverID, final String username, final String messagetext)
        {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(messageReceiverID);
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                for (DataSnapshot ds : snapshot.getChildren())
                    {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(messagesenderID, username + ":" + messagetext, "New Message", messageReceiverID, R.drawable.applogo);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotifications(sender)
                            .enqueue(new Callback<Response>()
                            {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                                    {
                                    // Toast.makeText(ChatActivity.this, "Laxman"+response.message(), Toast.LENGTH_SHORT).show();
                                    }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t)
                                    {

                                    }
                            });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });
        }

    private void DisplayLastSeen()
        {
        rootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.child("Userstate").hasChild("state"))
                    {

                    String state = dataSnapshot.child("Userstate").child("state").getValue().toString();
                    String date = dataSnapshot.child("Userstate").child("date").getValue().toString();
                    String time = dataSnapshot.child("Userstate").child("time").getValue().toString();

                    if (state.equals("online") )
                        {
                        userlastseen.setText(R.string.onlinestatus);

                        }  else if (state.equals("offline") )
                        {
                        userlastseen.setText("Last seen: " + date + " " + time);
                        }
                    } else
                    {
                    userlastseen.setText("offline");
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
//            loadingbar.setTitle("Sending");
//            loadingbar.setMessage("please wait");
//            loadingbar.setCanceledOnTouchOutside(true);
//            loadingbar.show();

            pDialog.show();


            fileUri = data.getData();

            if (!checker.equals("image"))
                {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference().child("Chat Documents Files");

                final String messege_sender_ref = "Messages/" + messagesenderID + "/" + messageReceiverID;
                final String messager_receiver_ref = "Messages/" + messageReceiverID + "/" + messagesenderID;
                DatabaseReference usersmessage_key = rootRef.child("Messages").child(messagesenderID).child(messageReceiverID).push();
                final String message_push_id = usersmessage_key.getKey();

                final StorageReference filepath = storageReference.child(message_push_id + "." + checker);

                filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {
                        if (task.isSuccessful())
                            {
                            filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task)
                                    {
                                    String downloadurl = task.getResult().toString();
                                    Map messegebody = new HashMap();
                                    messegebody.put("Message", downloadurl);
                                    messegebody.put("name", fileUri.getLastPathSegment());
                                    messegebody.put("Time", SaveCurrentTime);
                                    messegebody.put("Date", SaveCurrentDate);
                                    messegebody.put("Type", checker);
                                    messegebody.put("From", messagesenderID);

                                    Map messagebodydetail = new HashMap();
                                    messagebodydetail.put(messege_sender_ref + "/" + message_push_id, messegebody);
                                    messagebodydetail.put(messager_receiver_ref + "/" + message_push_id, messegebody);

                                    rootRef.updateChildren(messagebodydetail);
                                    //loadingbar.dismiss();
                                    pDialog.dismiss();
                                    }

                            });


                            }
                        }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                        {
                        pDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
                        {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pDialog.setContentText((int) progress + " % uploaded...");
                        }
                });

                } else if (checker.equals("image"))
                {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReference().child("Chat Image Files");

                final String messege_sender_ref = "Messages/" + messagesenderID + "/" + messageReceiverID;
                final String messager_receiver_ref = "Messages/" + messageReceiverID + "/" + messagesenderID;
                DatabaseReference usersmessage_key = rootRef.child("Messages").child(messagesenderID).child(messageReceiverID).push();
                final String message_push_id = usersmessage_key.getKey();

                final StorageReference filepath = storageReference.child(message_push_id + "." + "jpg");
                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation()
                {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                        {
                        if (!task.isSuccessful())
                            {
                            throw task.getException();
                            }
                        return filepath.getDownloadUrl();
                        }
                }).addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                        {
                        if (task.isSuccessful())
                            {
                            Uri downloadurl = task.getResult();
                            myUrl = downloadurl.toString();

                            Map messegebody = new HashMap();
                            messegebody.put("Message", myUrl);
                            messegebody.put("name", fileUri.getLastPathSegment());
                            messegebody.put("Time", SaveCurrentTime);
                            messegebody.put("Date", SaveCurrentDate);
                            messegebody.put("Type", checker);
                            messegebody.put("From", messagesenderID);

                            Map messagebodydetail = new HashMap();
                            messagebodydetail.put(messege_sender_ref + "/" + message_push_id, messegebody);
                            messagebodydetail.put(messager_receiver_ref + "/" + message_push_id, messegebody);

                            rootRef.updateChildren(messagebodydetail).addOnSuccessListener(new OnSuccessListener()
                            {
                                @Override
                                public void onSuccess(Object o)
                                    {
                                    pDialog.dismiss();
                                    //Toast.makeText(ChatActivity.this, "Message send ", Toast.LENGTH_SHORT).show();
                                    usermessageinput.setText("");
                                    }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                    {
                                    pDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    usermessageinput.setText("");
                                    }
                            });


                            }
                        }
                });

                } else
                {
                pDialog.dismiss();
                Toast.makeText(onlineOfflineStatus, "Nothing selected", Toast.LENGTH_SHORT).show();
                }
            }

        }

    private void DisplayReceiverinfo()
        {


        rootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                if (dataSnapshot.exists())
                    {
                    final String profileimagelink = dataSnapshot.child("profileImage").getValue().toString();
                    messageReceiverName = dataSnapshot.child("Username").getValue().toString();
                    Glide.with(getApplicationContext()).load(profileimagelink).into(receiverprofileimage);
                   // Picasso.get().load(profileimagelink).into(receiverprofileimage);
                    recievername.setText(messageReceiverName);
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
        });
        }

    private void Initilizefields()
        {
        chattoolbar = findViewById(R.id.chat_appbarlayout);
        setSupportActionBar(chattoolbar);
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);
//        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
//        actionBar.setCustomView(action_bar_view);


        sendmessagebutton = findViewById(R.id.chat_send_message_button);
        sendimagefilebutton = findViewById(R.id.chat_send_image_button);
        usermessageinput = findViewById(R.id.chat_input_message);
        backbtn = findViewById(R.id.back_button_imgview);


        recievername = findViewById(R.id.custom_profile_name);
        receiverprofileimage = findViewById(R.id.custom_profile_image);
        messagesApapter = new MessagesApapter(messagesList);
        usermessegeslist = findViewById(R.id.chat_message_list_user);
        linearLayoutManager = new LinearLayoutManager(this);
        usermessegeslist.setLayoutManager(linearLayoutManager);
        usermessegeslist.setAdapter(messagesApapter);
        usermessegeslist.getRecycledViewPool().setMaxRecycledViews(0, 0);

        loadingbar = new ProgressDialog(this);

        userlastseen = findViewById(R.id.custom_lastseen);

        }

    @Override
    protected void onStart()
        {
        super.onStart();
        onlineOfflineStatus.UpdateUserStatus("online");
        DisplayLastSeen();
        }

    @Override
    protected void onStop()
        {
        super.onStop();
        onlineOfflineStatus.UpdateUserStatus("offline");
        }

    @Override
    protected void onDestroy()
        {
        super.onDestroy();
        onlineOfflineStatus.UpdateUserStatus("offline");
        }
}
