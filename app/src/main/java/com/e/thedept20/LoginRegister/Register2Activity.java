package com.e.thedept20.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.e.thedept20.DatePickerFragment;
import com.e.thedept20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Register2Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{

    private EditText fullname, username, dob, address;
    private Button next_btn;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference userRef;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentdateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dob.setText(currentdateString);

        }

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        // userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        fullname = findViewById(R.id.register2_Fullname);
        username = findViewById(R.id.register2_usernaame);
        dob = findViewById(R.id.register2_Dateofbirth);
        dob.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Toast.makeText(getApplicationContext(), "Click the TOP YEAR to change the year", Toast.LENGTH_LONG).show();
                DialogFragment datepicker1 = new DatePickerFragment();
                datepicker1.show(getSupportFragmentManager(), "date picker");

                }
        });
        address = findViewById(R.id.register2_Address);
        next_btn = findViewById(R.id.register2_creat_account1);

        toolbar = findViewById(R.id.register2_toolbar);
        toolbar.setTitle("Setup");
        setSupportActionBar(toolbar);

        next_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                SaveAccountSetupInformationStudent();

                }
        });
        }

    private void SendUserToRegister3Activiy()
        {
        Intent send = new Intent(getApplicationContext(), Register3Activity.class);
        startActivity(send);
        }

    private void SaveAccountSetupInformationStudent()
        {
        //boolean image1=getIntent().getBooleanExtra("pic",false);
        String displayname = username.getText().toString().trim();
        String Fullname = fullname.getText().toString().trim();
        String dateofbirth = dob.getText().toString().trim();
        String addres = address.getText().toString().trim();

        if (TextUtils.isEmpty(displayname))
            {
            Toast.makeText(this, "Enter a Username ", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(Fullname))
            {
            Toast.makeText(this, "Enter your fullname  ", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dateofbirth))
            {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(addres))
            {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            } else
            {
            SendUserToRegister3Activiy();
            HashMap userMap = new HashMap();
            userMap.put("Username", displayname);
            userMap.put("Fullname", Fullname);
            userMap.put("Designation", "Student");
            userMap.put("Status", "");
            userMap.put("Gender", "");
            userMap.put("Date of birth", dateofbirth);
            userMap.put("University roll number", "");
            userMap.put("Address", addres);
            userMap.put("Contact number", "");
            userMap.put("Blood Group", "");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                    {

                    }
            });
            }
        }
}