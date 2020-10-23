package com.e.thedept20.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Register3Activity extends AppCompatActivity
{

    private EditText collgName, rollnum, designation,  semester;
    private Spinner spinner;
    private Toolbar toolbar;
    private Button button;
    private SweetAlertDialog pDialog;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        collgName = findViewById(R.id.register3_collegename);
        rollnum = findViewById(R.id.register3_rllnumber);
        designation = findViewById(R.id.register3_designation);
        spinner=findViewById(R.id.register3_department);
        ArrayAdapter<String> myAdapter =new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.names)


        );
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        //department = findViewById(R.id.register3_department);
        semester = findViewById(R.id.register3_semester);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        toolbar = findViewById(R.id.register3_toolbar);
        toolbar.setTitle("Academics");
        setSupportActionBar(toolbar);

        button = findViewById(R.id.register3_creat_account1);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                SaveAccountSetupInformationStudent();


                }
        });

        }

    private void SendUserToNextActivity()
        {
        Intent intent = new Intent(getApplicationContext(), ProfilePicSetupActivity.class);
        startActivity(intent);
        }


    private void SaveAccountSetupInformationStudent()
        {

        String desig = designation.getText().toString().trim();
        String collname = collgName.getText().toString().trim();
       // String dept = department.getText().toString().trim();
        String colrolnum = rollnum.getText().toString().trim();
        String sem = semester.getText().toString().trim();

        if (TextUtils.isEmpty(collname))
           //{
            //Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
           // } else if (TextUtils.isEmpty(dept))
            {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(colrolnum))
            {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(sem))
            {
            Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            } else
            {

            pDialog = new SweetAlertDialog(Register3Activity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("Saving");
            pDialog.setCancelable(false);
            SendUserToNextActivity();
            HashMap userMap = new HashMap();
            userMap.put("Designation", desig);
            userMap.put("College name", collname);
            userMap.put("College roll number", colrolnum);
            userMap.put("Department", "Computer Science");
            userMap.put("Semester", sem);
            userMap.put("Routine", "empty");
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                    {
                    if (task.isSuccessful())
                        {
                        pDialog.dismiss();
                        } else
                        {
                        String message = task.getException().getMessage();
                        Toast.makeText(Register3Activity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        pDialog.dismissWithAnimation();
                        }
                    }
            });
            }
        }
}