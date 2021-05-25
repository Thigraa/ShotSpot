package com.shotspot.fragments.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.shotspot.R;
import com.shotspot.activities.MainActivity;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.database.storage.ImageManager;
import com.shotspot.fragments.navigation.profile.EditProfileFragment;
import com.shotspot.fragments.navigation.profile.LikedSpotsFragment;
import com.shotspot.fragments.navigation.profile.MySpotsFragment;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.shotspot.activities.MainActivity.bottomNavigationView;
import static com.shotspot.activities.MainActivity.currentUser;


public class ProfileFragment extends Fragment {

    CircleImageView profileImageView;
    TextView usernameTV;
    ImageButton logOutButton, editProfileButton, uploadImageButton;
    BottomNavigationView profileSpotsNavigation;
    EditText usernameET;
    File url;
    Bitmap bitmap= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        usernameTV = v.findViewById(R.id.profileUsernameTextView);
        usernameET = v.findViewById(R.id.profileUsernameEditText);
        usernameET.setVisibility(View.GONE);
        profileImageView = v.findViewById(R.id.profileUserCircleImage);
        profileImageView.setClickable(false);
        logOutButton = v.findViewById(R.id.logOutProfileButton);
        editProfileButton = v.findViewById(R.id.editProfileButton);
        uploadImageButton = v.findViewById(R.id.uploadImageButton);
        profileSpotsNavigation = v.findViewById(R.id.profileSpotNavigation);
        usernameTV.setText(currentUser.getUsername());
        usernameET.setText(currentUser.getUsername());
        try {
            if (currentUser.getImageURL() != null){
                profileImageView.setImageBitmap(currentUser.getBitmap());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setProfileSpotsNavigationViewClicks();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomNavigationView.setVisibility(View.VISIBLE);
        replaceFragment(new MySpotsFragment());
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = null;
                Context context= getContext();
                SharedPreferences settings=context.getSharedPreferences("PREFERENCES", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("token");
                editor.apply();
                Objects.requireNonNull(getActivity()).finish();
                startActivity(getActivity().getIntent());
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You can change your Username", Toast.LENGTH_SHORT).show();
                usernameET.setVisibility(View.VISIBLE);
                usernameTV.setVisibility(View.GONE);
                editProfileButton.setClickable(false);
//                MainActivity mainActivity = (MainActivity)getActivity();
//                mainActivity.replaceFragment(new EditProfileFragment());
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                uploadImageButton.setVisibility(View.GONE);

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(getContext(), ProfileFragment.this);
            }
        });

        usernameET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String newName = usernameET.getText().toString();
                            if (newName!=null){
                                currentUser.setUsername(newName);
                                Person_CRUD.updateUsername(currentUser);
                                usernameET.setVisibility(View.GONE);
                                usernameTV.setText(newName);
                                usernameTV.setVisibility(View.VISIBLE);
                                editProfileButton.setClickable(true);
                            }
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void setProfileSpotsNavigationViewClicks(){
        profileSpotsNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mySpots:
                        replaceFragment(new MySpotsFragment());
                        return true;
                    case R.id.likedSpots:
                        replaceFragment(new LikedSpotsFragment());
                        return true;
                }
                return true;
            }
        });
    }

    public void replaceFragment(Fragment f){
                getFragmentManager().beginTransaction()
                .replace(R.id.profileSpots, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            recortarImagen(imageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                url = new File(resultUri.getPath());
                Picasso.with(getContext()).load(url).into(profileImageView);
                uploadImageButton.setVisibility(View.VISIBLE);
            }
        }
    }
    private void recortarImagen(Uri imagenUri){
        CropImage.activity(imagenUri).setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(1280, 720)
                .setAspectRatio(1,1).start(getContext(), ProfileFragment.this);
    }

    private InputStream comprimirImagen(Bitmap thumb_bitmap,File file){
        try {
            thumb_bitmap = new Compressor(getContext())
                    .setMaxHeight(720)
                    .setMaxWidth(1280)
                    .setQuality(90)
                    .compressToBitmap(file);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] thumb_byte = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(thumb_byte);

    }

    private void uploadImage(){
        try {
            bitmap = ImageManager.drawableToBitmap(profileImageView.getDrawable());
            if (bitmap != null){
                InputStream inputStream = comprimirImagen(bitmap,url);
                String newImage = ImageManager.uploadImage(inputStream,inputStream.available());
                currentUser.setImageURL(newImage);
                Person_CRUD.updateImage(newImage,currentUser.getIdUser());
                Snackbar.make(profileImageView.getRootView(),"Image Uploaded",BaseTransientBottomBar.LENGTH_SHORT).show();
            }else {
                System.out.println("===============================No se pudo"+ bitmap);
                Snackbar.make(profileImageView.getRootView(),"Couldn't upload your profile image, try it later", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}