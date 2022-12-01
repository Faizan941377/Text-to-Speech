package com.nextsuntech.texttospeech.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.nextsuntech.texttospeech.R;
import com.nextsuntech.texttospeech.takeImage.TakeImageActivity;
import com.nextsuntech.texttospeech.textToSpeech.TextToSpeechActivity;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton textToSpeechBT;
    AppCompatButton takeImageBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textToSpeechBT = findViewById(R.id.bt_dashboard_text_to_speech);
        takeImageBT = findViewById(R.id.bt_take_image);


        textToSpeechBT.setOnClickListener(this);
        takeImageBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_dashboard_text_to_speech:
                startActivity(new Intent(this, TextToSpeechActivity.class));
                break;

            case R.id.bt_take_image:
                startActivity(new Intent(this, TakeImageActivity.class));
                break;
        }
    }
}