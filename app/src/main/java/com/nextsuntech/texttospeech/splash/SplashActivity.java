package com.nextsuntech.texttospeech.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nextsuntech.texttospeech.MainActivity;
import com.nextsuntech.texttospeech.R;
import com.nextsuntech.texttospeech.dashboard.DashboardActivity;
import com.nextsuntech.texttospeech.test.TestActivity;
import com.nextsuntech.texttospeech.textToSpeech.TextToSpeechActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, TextToSpeechActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}