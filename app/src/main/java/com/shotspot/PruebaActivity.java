package com.shotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;

public class PruebaActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;

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
                    ImageManager.GetImage("chomunusuke.png", imageStream, imageLength);

                    byte[] buffer = imageStream.toByteArray();

                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                    imageView.setImageBitmap(bitmap);


                } catch (Exception e) {
                    Toast.makeText(PruebaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

    }
}