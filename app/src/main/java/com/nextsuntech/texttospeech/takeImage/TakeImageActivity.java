package com.nextsuntech.texttospeech.takeImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.nextsuntech.texttospeech.R;
import com.nextsuntech.texttospeech.test.TestActivity;

import java.io.IOException;
import java.util.List;

public class TakeImageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView takeImageIV;
    AppCompatButton openCameraBT;
    EditText takeImageTextTV;
    FirebaseVisionImage image;
    ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_image);

        takeImageIV = findViewById(R.id.iv_take_Image);
        openCameraBT = findViewById(R.id.bt_take_image_open_camera);
        takeImageTextTV = findViewById(R.id.tv_text_to_speech);
        backIV = findViewById(R.id.iv_take_Image_back);


        openCameraBT.setOnClickListener(this);
        backIV.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_take_image_open_camera:
                /*ImagePicker.Companion.with(this).start(101);*/
                Intent i = new Intent();
                i.setType("image/");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "select Image"), 101);
                break;

            case R.id.iv_take_Image_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            Uri uri = data.getData();
            takeImageIV.setImageURI(uri);
            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();

                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                takeImageTextTV.setText(firebaseVisionText.getText());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TakeImageActivity.this, "AI is busy", Toast.LENGTH_SHORT).show();
                            }
                        });


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}