package com.shotspot.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.shotspot.R;
import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PruebaActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    private Uri imageUri;
    private static final int SELECT_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        imageView = findViewById(R.id.imagenGato);
        button = findViewById(R.id.botonGet);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long imageLength = 0;

                try {
                    ImageManager.GetImage("nutkgiitfc", imageStream, imageLength);

                    byte[] buffer = imageStream.toByteArray();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                    imageView.setImageBitmap(bitmap);


                } catch (Exception e) {
                    Toast.makeText(PruebaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

//        imageView = findViewById(R.id.imagen);
//        button = findViewById(R.id.botonAdd);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SelectImageFromGallery();
//            }
//        });
//
//        uploadImageButton =  findViewById(R.id.botonUpload);
//        uploadImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                UploadImage();
//                Intent intent = new Intent(MainActivity.this,PruebaActivity.class);
//                startActivity(intent);
//            }
//        });
//        uploadImageButton.setEnabled(false);

    }

    private void UploadImage()
    {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
            final int imageLength = imageStream.available();

            try {
                final String imageName = ImageManager.UploadImage(imageStream, imageLength);
                Toast.makeText(this, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();

            }
            catch(Exception ex) {
                final String exceptionMessage = ex.getMessage();
                Toast.makeText(this, exceptionMessage, Toast.LENGTH_SHORT).show();
                ex.printStackTrace();


            }
        }
        catch(Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    this.imageUri = imageReturnedIntent.getData();
                    this.imageView.setImageURI(this.imageUri);
//                    this.uploadImageButton.setEnabled(true);
                }
        }
    }
}