package com.e.thedept20.LoginRegister;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.an.customfontview.CustomButton;
import com.e.thedept20.Home.DashboardActivity;
import com.e.thedept20.R;
import com.e.thedept20.Utils.AmazingAutofitEditText;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegisterActivity extends AppCompatActivity
{
    private static final int RC_SIGN_IN = 100;

    TextView msg_reg_tv, register_tv;
    Typeface MR, MRR;
    private EditText UserEmail, UserPassword, UserConfirmPassword;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private SweetAlertDialog pDialog;
   GoogleSignInButton mGoogleLoginBtn;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        MRR = Typeface.createFromAsset(getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getAssets(), "fonts/myriad.ttf");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       //mGoogleLoginBtn = findViewById(R.id.google_signin_button1);



        pDialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitle("New Account Created ");
        pDialog.setContentText("Check yout email to verify account before logging in.");
        pDialog.setCancelable(false);
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
        {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog)
                {

                SendEmailVerificationmessage();
                pDialog.dismissWithAnimation();
                }
        });

        UserEmail = findViewById(R.id.register_email1);
        UserPassword = findViewById(R.id.register_password1);
        UserConfirmPassword = findViewById(R.id.register_forgotPassword1);
        CreateAccountButton = findViewById(R.id.register_creat_account1);
        msg_reg_tv = findViewById(R.id.msg_reg_tv);
        register_tv = findViewById(R.id.register_tv);

        msg_reg_tv.setTypeface(MRR);
        UserEmail.setTypeface(MRR);
        UserPassword.setTypeface(MRR);
        UserConfirmPassword.setTypeface(MRR);
        CreateAccountButton.setTypeface(MR);
        register_tv.setTypeface(MR);

        CreateAccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
                {
                //pDialog.show();

                CreatenewAccount();
                }
        });

        }

    @Override
    protected void onStart()
        {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            {
            SendUserToMainActivity();
            }
        }

    private void SendUserToMainActivity()
        {
        Intent mainIntent = new Intent(RegisterActivity.this, DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
        }

    private void CreatenewAccount()
        {
        String email = UserEmail.getText().toString().trim();
        String password = UserPassword.getText().toString().trim();
        String confirmpassword = UserConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email))
            {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password))
            {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confirmpassword))
            {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmpassword))
            {
            Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            } else
            {

            final SweetAlertDialog p = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            p.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            p.setTitleText("Loading");
            p.setCancelable(true);
            p.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                            {
                            if (task.isSuccessful())
                                {

                                pDialog.show();
                                //pDialog.dismissWithAnimation();


                                } else
                                {

                                String message = task.getException().getMessage();

                                p.setTitleText("");
                                p.setContentText(message);
                                //Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                                }
                            }
                    });
            }

        }

    private void SendEmailVerificationmessage()
        {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            {

            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                    {

                    //Toast.makeText(RegisterActivity.this, "Registration successful,verify your account", Toast.LENGTH_SHORT).show();
                    SendUserToLoginActivity();
                    mAuth.signOut();

                    }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                    {
                    Toast.makeText(RegisterActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    }
            });
            }
        }

    private void SendUserToLoginActivity()
        {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);

        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);


        finish();
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
            {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
                {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e)
                {
                // Google Sign In failed, update UI appropriately
                pDialog.show();
                //pDialog2.show();
                //Toast.makeText(this, "try again " + e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
                }
            }
        }


    private void firebaseAuthWithGoogle(String idToken)
        {
        pDialog.show();
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
                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            finish();

                            } else
                            {
                            // If sign in fails, display a message to the user.
                            pDialog.dismiss();
                           // pDialog2.setContentText(task.getException().getMessage());
                           // pDialog2.show();
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
