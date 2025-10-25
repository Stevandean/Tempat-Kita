package com.example.tempatkita.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempatkita.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // durasi total splash 2.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoImage);
        TextView appName = findViewById(R.id.appName);

        // Pastikan mulai dalam keadaan transparan
        logo.setAlpha(0f);
        appName.setAlpha(0f);

        // Fade-in logo
        logo.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(200)
                .start();

        // Fade-in teks setelah logo muncul
        appName.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(700)
                .start();

        // Setelah semua selesai, pindah ke MainActivity
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}
