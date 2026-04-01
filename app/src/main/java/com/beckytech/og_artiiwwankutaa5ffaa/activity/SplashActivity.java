package com.beckytech.og_artiiwwankutaa5ffaa.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.beckytech.og_artiiwwankutaa5ffaa.MainActivity;
import com.beckytech.og_artiiwwankutaa5ffaa.R;
import com.facebook.ads.AudienceNetworkAds;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen for a professional look
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        // HIGH REVENUE STEP: Initialize Ads as soon as the app starts
        // This ensures ads are preloaded by the time the user reaches MainActivity
        AudienceNetworkAds.initialize(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ handle splash screens differently,
            // but we ensure a smooth transition to MainActivity
            new Handler(Looper.getMainLooper()).postDelayed(this::startNextActivity, 500);
        } else {
            // Standard delay for older versions (1.5 seconds)
            new Handler(Looper.getMainLooper()).postDelayed(this::startNextActivity, 1500);
        }
    }

    private void startNextActivity() {
        if (!isFinishing()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Professional touch: Add a small fade-in transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}