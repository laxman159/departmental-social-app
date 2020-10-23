package com.e.thedept20.LoginRegister;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.SettingProfileActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity
{

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    Typeface MR, MRR;
    //views
    EditText mEmailEt, mPasswordEt;
    TextView noAccountTv, mRecoverpass, message, login_tv;
    Button mLoginBtn;
    SignInButton mGoogleLoginBtn;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    SweetAlertDialog pDialog,pDialog2,pDialog3;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        MRR = Typeface.createFromAsset(getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        pDialog2 = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        pDialog2.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog2.setTitleText("Sign-in failed");
        pDialog2.setContentText("Try Again");
        pDialog2.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
                {
                    pDialog2.dismiss();
                }
        });


        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        //init
        mEmailEt = findViewById(R.id.login_email1);
        mPasswordEt = findViewById(R.id.login_password1);
        mLoginBtn = findViewById(R.id.login_button1);
        mRecoverpass = findViewById(R.id.register_forgotPassword1);
        mGoogleLoginBtn = findViewById(R.id.google_signin_button);
       message = findViewById(R.id.msg_tv);
        login_tv = findViewById(R.id.login_tv);

        message.setTypeface(MRR);
        login_tv.setTypeface(MR);
       mEmailEt.setTypeface(MRR);
        mPasswordEt.setTypeface(MRR);
        mLoginBtn.setTypeface(MRR);
        mRecoverpass.setTypeface(MRR);


        //login btn click

       mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override         public void onClick(View v)
                {
                //input data
        String email = mEmailEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length()>0)
            {
            //invalid email pattern
            loginUser(email, password);

            } else
            {
            // mEmailEt.setError("Invalid Email");
            mPasswordEt.setError("Enter Password");
            mPasswordEt.setFocusable(true);

            }

        }
});
        //not have account text view click

//        mRecoverpass.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//                {
//                sowRecoverPasswordDialog();
//                }
//        });
//
            mGoogleLoginBtn.setOnClickListener(new View.OnClickListener()
        {
           @Override
         public void onClick(View v)
              {
               pDialog.show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
             startActivityForResult(signInIntent, RC_SIGN_IN);
              }

       });
        }

    private void sowRecoverPasswordDialog()
        {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle("Recover Password");
//
//        LinearLayout linearLayout = new LinearLayout(this);

        final EditText emailEt = new EditText(this);
        emailEt.setHint("Enter Email address");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

         pDialog3= new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        pDialog3.setCustomImage(R.drawable.applogo);
        final EditText inputfield = new EditText(LoginActivity.this);
        inputfield.setText("");
        pDialog3.setTitleText("Recover Password");
        pDialog3.setCustomView(emailEt);
        pDialog3.setCancelable(true);
        pDialog3.setConfirmText("Recover");
        pDialog3.setConfirmButton("Recover", new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
                {

                String email = emailEt.getText().toString().trim();
                if (email.length() > 8 )
                    {

                    beginRecovery(email);

                    }
                else{
                Toast.makeText(LoginActivity.this, "Enter email first", Toast.LENGTH_SHORT).show();
                }

                }
        }).show();

//        linearLayout.addView(emailEt);
//        linearLayout.setPadding(20, 10, 10, 20);
//        builder.setView(linearLayout);
//
//        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//                {
//                String email = emailEt.getText().toString().trim();
//                beginRecovery(email);
//                }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//                {
//                dialog.dismiss();
//                }
//        });
//
//        builder.create().show();


        }

    private void beginRecovery(String email)
        {
       // progressDialog.setMessage("Sending Email");
        //progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
                {
                //progressDialog.dismiss();

                if (task.isSuccessful())
                    {
                    //Toast.makeText(LoginActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                    pDialog3.setContentText("Reset link has been send to the given email");
                    pDialog3.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener()
                    {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog)
                            {
                            pDialog3.dismiss();
                            }
                    });
                    } else
                    {

                    //Toast.makeText(LoginActivity.this, "Failed..", Toast.LENGTH_SHORT).show();
                    }

                }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
                {
                pDialog3.setTitleText("Failed");
                pDialog3.setContentText(e.getMessage());
                pDialog3.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener()
                {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                        {
                        pDialog3.dismiss();
                        }
                });
               // Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });
        }

    private void loginUser(String email, String password)
        {

        pDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                        {
                        if (task.isSuccessful())
                            {
                            // Sign in success,
                            //progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                            } else
                            {
                            // If sign in fails, display a message to the user.
                            //progressDialog.dismiss();
                            pDialog.dismiss();
                            pDialog2.setContentText(task.getException().getMessage() +" (Use GOOGLE SIGN-IN BUTTON if you have logged it previously through GOOGLE SIGN-IN)");
                            pDialog2.show();


                            }


                        }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
                {
                //error,get and show error
                //Toast.makeText(LoginActivity.this, "2" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });
        }

    @Override
    public boolean onSupportNavigateUp()
        {
        onBackPressed();
        return super.onSupportNavigateUp();
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
            {
            pDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
                {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e)
                {
                // Google Sign In failed, update UI appropriately
                //pDialog.dismiss();
                pDialog2.show();
                //Toast.makeText(this, "try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
                }
            }
        }


    private void firebaseAuthWithGoogle(String idToken)
        {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                        {
                        if (task.isSuccessful())
                            {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            //if user is signing in for the first time the get and show data from google account
                            if (task.getResult().getAdditionalUserInfo().isNewUser())
                                {
                                String email = user.getEmail();
                                String uid = user.getUid();
                                //when user is registered store user info in firebase db using hashmap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put info in hashmap
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                //firebase db instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store user data named "users"
                                DatabaseReference reference = database.getReference("Users");
                                //put data with hashmap in db
                                reference.child(uid).setValue(hashMap);
                                }

                            //Get user email and uid from auth

                            //Toast.makeText(LoginActivity.this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();

                            } else
                            {
                            // If sign in fails, display a message to the user.
                            pDialog.dismiss();
                            pDialog2.setContentText(task.getException().getMessage());
                            pDialog2.show();
                            //Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();


                            }

                        // ...
                        }
                }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
                {
               // Toast.makeText(LoginActivity.this, "1" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
        });
        }


}