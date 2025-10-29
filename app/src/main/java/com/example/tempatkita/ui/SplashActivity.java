package com.example.tempatkita.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tempatkita.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoSplash);

        // 🔍 Deteksi mode tampilan (dark atau light)
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        int videoResId;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Jika mode gelap aktif
            videoResId = R.raw.splash_dark;
        } else {
            // Jika mode terang aktif (default)
            videoResId = R.raw.splash_light;
        }

        // 🔗 Arahkan ke video yang sesuai
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        videoView.setVideoURI(videoUri);

        // ▶️ Putar video
        videoView.start();

        // ⏭️ Saat video selesai, lanjut ke MainActivity
        videoView.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // ⚠️ Jika video gagal diputar, langsung lanjut
        videoView.setOnErrorListener((mp, what, extra) -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
            return true;
        });
    }
}
