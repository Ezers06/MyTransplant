package com.example.mytransplant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logoImageView = findViewById(R.id.logoImageView);

        AnimationSet animationSet = new AnimationSet(true);
        Animation circularAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_motion);
        animationSet.addAnimation(circularAnimation);

        logoImageView.startAnimation(animationSet);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        }, 2500);
    }

    private void checkSession() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userRole = preferences.getString("userRole", "");
            navigateToHomePage(userRole);
        } else {
            Intent intent = new Intent(SplashScreen.this, getStarted.class);
            startActivity(intent);
            finish();
        }
    }

    private void navigateToHomePage(String userRole) {
        Intent homeIntent;
        if ("user".equals(userRole)) {
            homeIntent = new Intent(SplashScreen.this, Homepage.class);
        } else if ("doctor".equals(userRole)) {
            homeIntent = new Intent(SplashScreen.this, HomepageDoctor.class);
        } else {
            logMessage("Invalid user role");
            return;
        }

        startActivity(homeIntent);
        finish();
    }

    private void logMessage(String message) {

    }
}
