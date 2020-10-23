package com.e.thedept20;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.thedept20.LoginRegister.StartActivity;


public class SplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashScreen.this, StartActivity.class));

        // close splash activity
        finish();

        }
}
