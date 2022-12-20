package com.nextsuntech.texttospeech.takeImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.nextsuntech.texttospeech.R;

import java.util.List;

public class TakeImageActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView takeImageIV;
    AppCompatButton openCameraBT;
    AppCompatButton getTextBT;
    Bitmap imageBitmap;
    EditText takeImageTextET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_image);


        takeImageIV = findViewById(R.id.iv_take_Image);
        openCameraBT = findViewById(R.id.bt_take_image_open_camera);
        getTextBT = findViewById(R.id.bt_take_img_get_text);
        takeImageTextET = findViewById(R.id.et_text_to_speech);


        openCameraBT.setOnClickListener(this);
        getTextBT.setOnClickListener(this);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  if (resultCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        Bundle extras = data.getExtras();
        imageBitmap = (Bitmap) extras.get("data");
        takeImageIV.setImageBitmap(imageBitmap);
        // }
    }

    private void getTextFromImage() {
      /*  FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TakeImageActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error", e.getMessage());
            }
        });*/
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
       /* List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size() == 0) {
            Toast.makeText(this, "No text Text Found in Image", Toast.LENGTH_SHORT).show();
        } else {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                String text = block.getText();
                takeImageTextET.setText(text);

            }
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_take_image_open_camera:
                dispatchTakePictureIntent();
                takeImageTextET.setText("");
                break;
            case R.id.bt_take_img_get_text:
                try {
                    getTextFromImage();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Please click picture first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}