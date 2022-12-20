package com.nextsuntech.texttospeech.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.nextsuntech.texttospeech.Constant;
import com.nextsuntech.texttospeech.R;
import com.nextsuntech.texttospeech.textToSpeech.TextToSpeechActivity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class TestActivity extends AppCompatActivity {

    TextView textView;
    EditText input;
    Button speakBT;
    Button downloadBT;
    TextToSpeech tts;
    private String mAudioFilename = "";
    private String mUtteranceID;
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView = findViewById(R.id.tv_text);
        input = findViewById(R.id.et_text);
        speakBT = findViewById(R.id.btn_speak_text);
        downloadBT = findViewById(R.id.btn_download_text);


        input.setText("In publishing and graphic design, Lorem ipsum is a placeholder" +
                " text commonly used to demonstrate the visual form of a document or a typeface ");


        downloadBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(TestActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.file_name_dialog, null);
                final EditText text = (EditText) view1.findViewById(R.id.et_file_name);
                Button cancelBT = (Button) view1.findViewById(R.id.bt_file_name_cancel);
                Button saveBT = (Button) view1.findViewById(R.id.bt_file_name_save);
                alert.setView(view1);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

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
                                saveToAudioFile(input.getText().toString().trim());
                            }
                        });

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
                });

                alertDialog.show();
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
        Toast.makeText(TestActivity.this, "Saved to " + mAudioFilename, Toast.LENGTH_LONG).show();
    }

}