package com.idocalm.travelmate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.idocalm.travelmate.auth.Auth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 2100; // Duration until transition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView plane = findViewById(R.id.planeImage);

        // Animate: Fly from left to center
        TranslateAnimation flyIn = new TranslateAnimation(
                -500f, 1500f, // X: from far left to center point
                -1000f, 500f      // Y: no vertical movement
        );
        flyIn.setDuration(2000);
        flyIn.setFillAfter(true);
        flyIn.setInterpolator(new android.view.animation.AccelerateInterpolator());
        plane.startAnimation(flyIn);

        // After delay, launch main activity
        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.d("SplashActivity", "User logged in, redirecting to home screen.");
                Auth.attemptDejaVu(this);
            } else {
                Log.d("SplashActivity", "User not logged in, redirecting to login screen.");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, SPLASH_TIME);
    }
}