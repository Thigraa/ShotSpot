package com.shotspot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shotspot.database.DatabaseConnection;
import com.shotspot.fragments.HomeFragment;
import com.shotspot.fragments.LoginFragment;
import com.shotspot.fragments.RegisterFragment;
import com.shotspot.fragments.WelcomeFragment;
import com.shotspot.helper.Encryptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    public static Connection connection;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        connection = DatabaseConnection.connect();
        Fragment fragment;
        if(savedInstanceState != null){
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "lastFragment");
            replaceFragment(fragment);
        }
        else{
            fragment = new WelcomeFragment();
            replaceFragment(fragment);

        }

    }



    public void replaceFragment(Fragment f){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        for(int i = 0; i< getSupportFragmentManager().getFragments().size(); i++ ){
            if(getSupportFragmentManager().getFragments().get(i).isVisible()){
                Fragment f =  getSupportFragmentManager().getFragments().get(i);
                getSupportFragmentManager().putFragment(savedInstanceState, "lastFragment", f);
            }
        }

        imageView = findViewById(R.id.imagen);
        button = findViewById(R.id.botonAdd);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImageFromGallery();
            }
        });

        uploadImageButton =  findViewById(R.id.botonUpload);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
                Intent intent = new Intent(MainActivity.this,PruebaActivity.class);
                startActivity(intent);
            }
        });
        uploadImageButton.setEnabled(false);


    }

    private void SelectImageFromGallery()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private void UploadImage()
    {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.imageUri);
            final int imageLength = imageStream.available();

                    try {
                        final String imageName = ImageManager.UploadImage(imageStream, imageLength);
                                Toast.makeText(MainActivity.this, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();

                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        Toast.makeText(MainActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
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
                    this.uploadImageButton.setEnabled(true);
                }
        }
    }
}