package com.nextsuntech.texttospeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner fromSP;
    Spinner toSP;
    TextInputEditText sourceText;
    ImageView micIV;
    MaterialButton translationBT;
    TextView translationTV;
    ImageView speakTextIV;
    TextToSpeech textToSpeech;

    String[] fromLanguages = {"From", "English", "Arabic", "Urdu", "Hindi", "German"};
    String[] toLanguages = {"To", "English", "Arabic", "Urdu", "Hindi", "German"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromSP = findViewById(R.id.idFromSpinner);
        toSP = findViewById(R.id.idToSpinner);
        sourceText = findViewById(R.id.idEditSource);
        micIV = findViewById(R.id.idIVMic);
        translationBT = findViewById(R.id.idBtnTranslation);
        translationTV = findViewById(R.id.idTranslatedTV);
        speakTextIV = findViewById(R.id.iv_speak_text);


        fromSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSP.setAdapter(fromAdapter);


        toSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode = getLanguageCode(toLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSP.setAdapter(toAdapter);


        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to translate");
                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        translationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translationTV.setVisibility(view.VISIBLE);
                translationTV.setText("");
                if (sourceText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(MainActivity.this, "Please select the language to translate", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(fromLanguageCode, toLanguageCode, sourceText.getText().toString());
                }
            }
        });
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source) {
        translationTV.setText("Downloading mode, Please waite");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();

        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translationTV.setText("Translation...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translationTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to translate, Please try agian", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to download model, Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            sourceText.setText(result.get(0));
        }
    }

    //String[] fromLanguages = {"From","English","Arabic","Urdu","Hindi","German"};
    private int getLanguageCode(String language) {

        Integer languageCode = 0;
        if ("English".equals(language)) {
            languageCode = FirebaseTranslateLanguage.EN;
        } else if ("Arabic".equals(language)) {
            languageCode = FirebaseTranslateLanguage.AR;
        } else if ("Urdu".equals(language)) {
            languageCode = FirebaseTranslateLanguage.UR;
        } else if ("German".equals(language)) {
            languageCode = FirebaseTranslateLanguage.GA;
        } else if ("Hindi".equals(language)) {
            languageCode = FirebaseTranslateLanguage.HI;
        } else {
            languageCode = 0;
        }

        speakTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = translationTV.getText().toString();
                if (text.isEmpty()) {
                    translationTV.setError("Please enter text to speech!");
                } else {
                    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                if (language.equals("English")) {
                                    textToSpeech.setLanguage(new Locale("English"));
                                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                                } else if (language.equals("Arabic")) {
                                    textToSpeech.setLanguage(new Locale("ar-QA"));
                                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                                } else if (language.equals("Urdu")) {
                                    textToSpeech.setLanguage(new Locale("Urdu"));
                                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                                } else if (language.equals("Hindi")) {
                                    textToSpeech.setLanguage(new Locale("hin"));
                                    textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
                                }
                            }
                        }
                    });
                }
            }
        });

        return languageCode;


    }
}