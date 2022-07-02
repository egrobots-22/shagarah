package com.egrobots.shagarah.presentation;

import androidx.appcompat.app.AppCompatActivity;
import dagger.android.support.DaggerAppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.egrobots.shagarah.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends DaggerAppCompatActivity {

    private static final long SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, RequestsActivity.class));
            }
        }, SPLASH_TIME_OUT);
    }
}