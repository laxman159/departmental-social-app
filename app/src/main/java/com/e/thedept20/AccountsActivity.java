package com.e.thedept20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{


    private EditText dateofbirth, gen, collage_n, department, colgg_roll, uni_roll, blood, semester;
    private FirebaseAuth mAuth;
    private String CurrentUserID, currentdateString;
    private DatabaseReference userRef;
    private SweetAlertDialog pDialog;
    private Button button;
    private Toolbar toolbar;


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currentdateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dateofbirth.setText(currentdateString);
        }

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        toolbar = findViewById(R.id.abl_aa_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inits
        gen = findViewById(R.id.setup_student_gender);
        department = findViewById(R.id.setup_student_department);
        collage_n = findViewById(R.id.setup_student_collegename);
        uni_roll = findViewById(R.id.setup_student_unirollno);
        blood = findViewById(R.id.setup_student_bloodgroup);
        semester = findViewById(R.id.setup_student_semester);
        dateofbirth = findViewById(R.id.setup_student_dob);
        colgg_roll = findViewById(R.id.setup_student_collegerollno);
        button = findViewById(R.id.setup_imformation_butten);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                ValidateStudentAccountInfo();
                }
        });

        //firebase inits
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);

        //aleartdialog
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitleText("Updating...");
        pDialog.setCancelable(false);


        dateofbirth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                Toast.makeText(AccountsActivity.this, "Click the TOP YEAR to change the year", Toast.LENGTH_LONG).show();
                DialogFragment datepicker1 = new DatePickerFragment();

                datepicker1.show(getSupportFragmentManager(), "date picker");
                }
        });


        userRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                if (snapshot.exists())
                    {
                    String dob = snapshot.child("Date of birth").getValue().toString();
                    String bloodg = snapshot.child("Blood Group").getValue().toString();
                    String gend = snapshot.child("Gender").getValue().toString();
                    String collname = snapshot.child("College name").getValue().toString();
                    String dept = snapshot.child("Department").getValue().toString();
                    String collrollno = snapshot.child("College roll number").getValue().toString();
                    String unirollno = snapshot.child("University roll number").getValue().toString();
                    String semesters = snapshot.child("Semester").getValue().toString();

                    dateofbirth.setText(dob);
                    blood.setText(bloodg);
                    gen.setText(gend);
                    collage_n.setText(collname);
                    department.setText(dept);
                    colgg_roll.setText(collrollno);
                    uni_roll.setText(unirollno);
                    semester.setText(semesters);

                    }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
                {

                }
        });


        }

    private void ValidateStudentAccountInfo()
        {


        String dateofbirt = dateofbirth.getText().toString();
        String gener = gen.getText().toString();
        String collname = collage_n.getText().toString();
        String dept = department.getText().toString();
        String colrolnum = colgg_roll.getText().toString();
        String unirolnum = uni_roll.getText().toString();
        String bloo = blood.getText().toString();
        String sem = semester.getText().toString();

        pDialog.show();
        UpdateStudentAccountInfo(dateofbirt, gener, collname, dept, colrolnum, unirolnum, bloo, sem);


        }

    private void UpdateStudentAccountInfo(String dateofbir, String gener, String collname, String dept, String colrolnum, String unirolnum, String blood, String sem)
        {
        HashMap userMap = new HashMap();
        userMap.put("Designation", "Student");
        userMap.put("Gender", gener);
        userMap.put("Date of birth", dateofbir);
        userMap.put("College name", collname);
        userMap.put("College roll number", colrolnum);
        userMap.put("Department", dept);
        userMap.put("University roll number", unirolnum);
        userMap.put("Blood Group", blood);
        userMap.put("Semester", sem);
        userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
                {
                if (task.isSuccessful())
                    {

                    startActivity(new Intent(getApplicationContext(), AccountsActivity.class));

                    } else
                    {
                    String message = task.getException().getMessage();
                    Toast.makeText(AccountsActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                    }
                }
        });
        }

    @Override
    public void onBackPressed()
        {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        super.onBackPressed();
        }
}