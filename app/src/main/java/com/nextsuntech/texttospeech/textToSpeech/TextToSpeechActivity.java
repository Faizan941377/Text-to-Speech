package com.nextsuntech.texttospeech.textToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.nextsuntech.texttospeech.R;
import com.nextsuntech.texttospeech.takeImage.TakeImageActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TextToSpeechActivity extends AppCompatActivity {

    Spinner fromSP;
    Spinner toSP;
    EditText sourceText;
    ImageButton micIV;
    ImageView pauseBT;
    ImageView downloadBT;
    ImageView replayBT;
    ImageButton cameraBT;
    MaterialButton translationBT;
    TextView translationTV;
    ImageView speakTextIV;
    MaterialButton showLayoutBT;
    LinearLayout settingsLL;
    TextView ttsSpeedPercentageTV;
    TextView ttsPitchPercentageTV;
    SeekBar ttsPitchSB;
    SeekBar ttsSpeedSB;
    TextToSpeech tts;
    private String mAudioFilename = "";
    private String mUtteranceID;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 101;
    boolean doubleBackToExitPressedOnce = false;


    String[] fromLanguages = {"From", "English", "Arabic", "Urdu", "Hindi", "German", "Chinese", "Spanish"
            , "French", "Bengali", "Russian", "Portuguese"};
    String[] toLanguages = {"To", "English", "Arabic", "Urdu", "Hindi", "German", "Chinese", "Spanish"
            , "French", "Bengali", "Russian", "Portuguese"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int fromLanguageCode, toLanguageCode = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        fromSP = findViewById(R.id.idFromSpinner);
        toSP = findViewById(R.id.idToSpinner);
        sourceText = findViewById(R.id.idEditSource);
        micIV = findViewById(R.id.idIVMic);
        translationBT = findViewById(R.id.idBtnTranslation);
        translationTV = findViewById(R.id.idTranslatedTV);
        speakTextIV = findViewById(R.id.iv_tts);
        pauseBT = findViewById(R.id.bt_pause);
        downloadBT = findViewById(R.id.bt_download);
        replayBT = findViewById(R.id.bt_replay);
        cameraBT = findViewById(R.id.iv_tts_camera);
        showLayoutBT = findViewById(R.id.bt_tts_show_ll);
        settingsLL = findViewById(R.id.ll_tts_settings);
        ttsSpeedPercentageTV = findViewById(R.id.tv_tts_speed_percentage);
        ttsSpeedSB = findViewById(R.id.sb_tts_speed);
        ttsPitchSB = findViewById(R.id.sb_tts_pitch);
        ttsPitchPercentageTV = findViewById(R.id.tv_tts_pitch_percentage);


        //getting text from camera activity
        Intent intent = getIntent();
        String getText = intent.getStringExtra("text");
        sourceText.setText(getText);

        //select language spinners
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


        //seekBarSpeed control
        ttsSpeedSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ttsSpeedPercentageTV.setText(progress + "%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //seekBar pitch control
        ttsPitchSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                ttsPitchPercentageTV.setText("" + position + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //linearlayout setting visibility
        showLayoutBT.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_expend_more, 0, 0);
        showLayoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settingsLL.getVisibility() == View.GONE) {
                    showLayoutBT.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_expend_less, 0, 0);
                    settingsLL.setVisibility(View.VISIBLE);
                } else if (settingsLL.getVisibility() == View.VISIBLE) {
                    showLayoutBT.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_expend_more, 0, 0);
                    settingsLL.setVisibility(View.GONE);
                }
            }
        });

        //button for recode a voice
        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "EN");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something to translate");
                try {
                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(TextToSpeechActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TakeImageActivity.class));
                finish();
            }
        });


        //button for translate the text into different languages
        translationBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translationTV.setVisibility(view.VISIBLE);
                translationTV.setText("");
                downloadBT.setVisibility(View.VISIBLE);
                if (sourceText.getText().toString().isEmpty()) {
                    Toast.makeText(TextToSpeechActivity.this, "Please enter text to translate", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode == 0) {
                    Toast.makeText(TextToSpeechActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode == 0) {
                    Toast.makeText(TextToSpeechActivity.this, "Please select the language to translate", Toast.LENGTH_SHORT).show();
                } else {
                    translateText(fromLanguageCode, toLanguageCode, sourceText.getText().toString());
                }
            }
        });


        //pause the tts engine and text
        pauseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tts.isSpeaking()) {
                    tts.stop();
                    pauseBT.setVisibility(View.GONE);
                    replayBT.setVisibility(View.VISIBLE);
                }
            }


        });

        //download tts voice button
        downloadBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(TextToSpeechActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.file_name_dialog, null);
                final EditText text = (EditText) view1.findViewById(R.id.et_file_name);
                Button cancelBT = (Button) view1.findViewById(R.id.bt_file_name_cancel);
                Button saveBT = (Button) view1.findViewById(R.id.bt_file_name_save);
                ImageButton closeBT = (ImageButton) view1.findViewById(R.id.iv_file_name_dialog_close);
                alert.setView(view1);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                closeBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                cancelBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });


                saveBT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                saveToAudioFile(translationTV.getText().toString());
                            }
                        });

                        if (text.length() == 0) {
                            text.setError("Please enter file name");
                        } else {
                            mUtteranceID = text.getText().toString();
                            // Perform the dynamic permission request
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
                            }

                            // Create audio file location
                            File f = new File(Environment.getExternalStorageDirectory(), "tts");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                try {
                                    Files.createDirectory(Paths.get(f.getAbsolutePath()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                f.mkdir();
                                f.mkdirs();
                                Toast.makeText(getApplicationContext(), f.getPath(), Toast.LENGTH_LONG).show();
                            }
                            mAudioFilename = f.getAbsolutePath() + "/" + mUtteranceID + ".mp3";
                            alertDialog.dismiss();
                        }
                    }
                });

                alertDialog.show();
                downloadBT.setVisibility(View.GONE);
            }
        });

    }


    private void saveToAudioFile(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.synthesizeToFile(text, null, new File(mAudioFilename), mUtteranceID);
        } else {
            HashMap<String, String> hm = new HashMap();
            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mUtteranceID);
            tts.synthesizeToFile(text, hm, mAudioFilename);
        }
        Toast.makeText(TextToSpeechActivity.this, "Saved to " + mAudioFilename, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(TextToSpeechActivity.this, "Failed to translate, Please try agian", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TextToSpeechActivity.this, "Failed to download model, Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == REQUEST_PERMISSION_CODE) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceText.setText(result.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getLanguageCode(String language) {

        int languageCode = 0;
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
        } else if ("Chinese".equals(language)) {
            languageCode = FirebaseTranslateLanguage.ZH;
        } else if ("Spanish".equals(language)) {
            languageCode = FirebaseTranslateLanguage.ES;
        } else if ("French".equals(language)) {
            languageCode = FirebaseTranslateLanguage.FR;
        } else if ("Bengali".equals(language)) {
            languageCode = FirebaseTranslateLanguage.BN;
        } else if ("Russian".equals(language)) {
            languageCode = FirebaseTranslateLanguage.RU;
        } else if ("Portuguese".equals(language)) {
            languageCode = FirebaseTranslateLanguage.PT;
        }

        speakTextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        String text = translationTV.getText().toString();
                        if (text.isEmpty()) {
                            translationTV.setError("Please enter text to speech!");
                            pauseBT.setVisibility(View.GONE);
                        } else {

                            //here we set the tts pitch and speed
                            float speed = (float) ttsSpeedSB.getProgress() / 50;
                            if (speed < 0.1) speed = 0.1f;
                            tts.setSpeechRate(speed);

                            float pitch = (float) ttsPitchSB.getProgress() / 50;
                            if (pitch < 0.1) pitch = 0.1f;
                            tts.setPitch(pitch);

                            if (language.equals("English")) {
                                tts.setLanguage(new Locale("EN"));
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.speak(text, TextToSpeech.QUEUE_ADD, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Arabic")) {
                                tts.setLanguage(new Locale("ar-sa"));
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Urdu")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("UR"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Hindi")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("HI"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("German")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("DE"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Chinese")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("ZH"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Spanish")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("ES"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("French")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("AF"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Bengali")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("BN"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Russian")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("RU"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Portuguese")) {
                                pauseBT.setVisibility(View.VISIBLE);
                                tts.setLanguage(new Locale("PT"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            }

                        }
                    }
                });

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.i("TTS", "utterance started");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.i("TTS", "utterance done");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.i("TTS", "utterance error");
                    }


                    @Override
                    public void onRangeStart(String utteranceId, int start, int end, int frame) {
                        // onRangeStart (and all UtteranceProgressListener callbacks) do not run on main thread
                        // ... so we explicitly manipulate views on the main thread:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String translate = translationTV.getText().toString();
                                Spannable textWithHighlights = new SpannableString(translate);

                                textWithHighlights.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                translationTV.setText(textWithHighlights);
                            }
                        });
                    }
                });
            }

        });

        replayBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replayBT.setVisibility(View.GONE);
                pauseBT.setVisibility(View.VISIBLE);
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        String text = translationTV.getText().toString();
                        if (text.isEmpty()) {
                            translationTV.setError("Please enter text to speech!");
                        } else {
                            pauseBT.setVisibility(View.VISIBLE);
                            //here we set the tts pitch and speed
                            float speed = (float) ttsSpeedSB.getProgress() / 50;
                            if (speed < 0.1) speed = 0.1f;
                            tts.setSpeechRate(speed);

                            float pitch = (float) ttsPitchSB.getProgress() / 50;
                            if (pitch < 0.1) pitch = 0.1f;
                            tts.setPitch(pitch);


                            if (language.equals("English")) {
                                tts.setLanguage(new Locale("EN"));
                                tts.speak(text, TextToSpeech.QUEUE_ADD, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Arabic")) {
                                tts.setLanguage(new Locale("ar-sa"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Urdu")) {
                                tts.setLanguage(new Locale("UR"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Hindi")) {
                                tts.setLanguage(new Locale("HI"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("German")) {
                                tts.setLanguage(new Locale("DE"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Chinese")) {
                                tts.setLanguage(new Locale("ZH"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Spanish")) {
                                tts.setLanguage(new Locale("ES"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("French")) {
                                tts.setLanguage(new Locale("AF"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Bengali")) {
                                tts.setLanguage(new Locale("BN"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Russian")) {
                                tts.setLanguage(new Locale("RU"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            } else if (language.equals("Portuguese")) {
                                tts.setLanguage(new Locale("PT"));
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UNIQUE_UTTERANCE_ID");
                            }

                        }
                    }
                });

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Log.i("TTS", "utterance started");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.i("TTS", "utterance done");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.i("TTS", "utterance error");
                    }


                    @Override
                    public void onRangeStart(String utteranceId, int start, int end, int frame) {
                        // onRangeStart (and all UtteranceProgressListener callbacks) do not run on main thread
                        // ... so we explicitly manipulate views on the main thread:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String translate = translationTV.getText().toString();
                                Spannable textWithHighlights = new SpannableString(translate);

                                textWithHighlights.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                translationTV.setText(textWithHighlights);
                            }
                        });
                    }
                });
            }
        });
        return languageCode;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Double Click to Exit", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}