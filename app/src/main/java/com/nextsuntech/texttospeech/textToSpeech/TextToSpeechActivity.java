package com.nextsuntech.texttospeech.textToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nextsuntech.texttospeech.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TextToSpeechActivity extends AppCompatActivity  {

    ImageView backBT;
    TextToSpeech textToSpeech;
    AppCompatButton speechBT;
    EditText enterTextET;
    Spinner selectLangSP;
    RadioGroup radioGroup;
    RadioButton radioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

      /*  backBT = findViewById(R.id.iv_text_to_speech_back);
        speechBT = findViewById(R.id.bt_text_to_speech);
        enterTextET = findViewById(R.id.et_text_to_speech);
        selectLangSP = findViewById(R.id.sp_select_lang);
        radioGroup = findViewById(R.id.radiogroup);
        radioButton = findViewById(R.id.rb_male);
        //femaleRB = findViewById(R.id.rb_female);

        radioButton.setChecked(true);


        backBT.setOnClickListener(this);
        speechBT.setOnClickListener(this);
        selectLangSP.setOnItemSelectedListener(this);


        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.Spinner_Items,
                R.layout.color_spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        selectLangSP.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        textToSpeech = new TextToSpeech(com.nextsuntech.texttospeech.textToSpeech.TextToSpeechActivity
                .this, this, "com.google.android.tts");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onInit(int i) {

        String text = selectLangSP.getSelectedItem().toString();
        Log.d("textcheck", text);
       *//* textToSpeech = new TextToSpeech(getApplicationContext(), i1 -> {
            if (i1 != TextToSpeech.ERROR) {
                if (text.equals("Select Language")) {
                    String textSp = "Please Select Your Language";
                    textToSpeech.speak(textSp, TextToSpeech.QUEUE_ADD, null);
                }*//*
        if (i == TextToSpeech.SUCCESS) {
            if (text.equals("Uk English")) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            } else if (text.equals("German")) {
                textToSpeech.setLanguage(Locale.GERMAN);
            } else if (text.equals("Chinese")) {
                textToSpeech.setLanguage(Locale.CHINA);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_text_to_speech_back:
                finish();
                break;
            case R.id.bt_text_to_speech:

                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);

                Toast.makeText(this, "" + radioButton.getText(), Toast.LENGTH_SHORT).show();


                textToSpeech = new TextToSpeech(getApplicationContext(), i -> {
                    if (i == TextToSpeech.SUCCESS) {
                        if (radioButton.getText().equals("Male Voice")) {
                            Set<String> a = new HashSet<>();
                            a.add("male");
                            Voice v = new Voice("en-us-x-sfg#male_2-local", new Locale("en", "UK"), 400, 200, true, a);
                            textToSpeech.setVoice(v);
                            textToSpeech.setSpeechRate(0.8f);
                            //selectLang();
                            String text = enterTextET.getText().toString();
                            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);


                        } else if (radioButton.getText().equals("Female Voice")) {
                            Set<String> a = new HashSet<>();
                            a.add("female");
                            Voice v = new Voice("en-us-x-sfg#female_2-local", new Locale("en", "US"), 400, 200, true, a);
                            textToSpeech.setVoice(v);
                            textToSpeech.setSpeechRate(0.8f);
                            selectLang();
                        }
                    }
                });
                break;
        }
    }

    private void selectLang() {
        String textSP = selectLangSP.getSelectedItem().toString();
        if (textSP.equals("English (UK)")) {
            textToSpeech.setLanguage(Locale.ENGLISH);
            String text = enterTextET.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else if (textSP.equals("German")) {
            textToSpeech.setLanguage(Locale.GERMAN);
            String text = enterTextET.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else if (textSP.equals("Chinese")) {
            textToSpeech.setLanguage(Locale.CHINA);
            String text = enterTextET.getText().toString();
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {
            Toast.makeText(this, "Language not supported!", Toast.LENGTH_SHORT).show();
        }*/
    }
}
