package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.database.storage.ImageManager;
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
    final Handler handler = new Handler();
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
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        try {
                            handler.post(new Runnable() {
                                public void run() {
                                    profileImageView.setImageBitmap(currentUser.getBitmap());
                                }
                            });
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }});
                th.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setProfileSpotsNavigationViewClicks();
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
                logOutButton.setVisibility(View.VISIBLE);
                editProfileButton.setVisibility(View.VISIBLE);

            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkReadStorage();
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
                        Thread th = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            replaceFragment(new MySpotsFragment());
                                        }
                                    });
                                }
                                catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            }});
                        th.start();

                        return true;
                    case R.id.likedSpots:
                        Thread th2 = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            replaceFragment(new LikedSpotsFragment());
                                        }
                                    });
                                }
                                catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            }});
                        th2.start();
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
                logOutButton.setVisibility(View.GONE);
                editProfileButton.setVisibility(View.GONE);
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

    private void checkReadStorage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
    }
}