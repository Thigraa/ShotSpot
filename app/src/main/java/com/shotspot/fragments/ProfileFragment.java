package com.shotspot.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shotspot.MainActivity;
import com.shotspot.R;
import com.shotspot.database.Person_CRUD;
import com.shotspot.database.SpotImage_CRUD;
import com.shotspot.model.Person;
import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shotspot.MainActivity.bottomNavigationView;
import static com.shotspot.MainActivity.currentUser;


public class ProfileFragment extends Fragment {

    CircleImageView profileImageView;
    TextView usernameTV;
    ImageButton logOutButton, editProfileButton;
    BottomNavigationView profileSpotsNavigation;
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
        profileImageView = v.findViewById(R.id.profileUserCircleImage);
        logOutButton = v.findViewById(R.id.logOutProfileButton);
        editProfileButton = v.findViewById(R.id.editProfileButton);
        profileSpotsNavigation = v.findViewById(R.id.profileSpotNavigation);
        usernameTV.setText(currentUser.getUsername());
        try {
            if (currentUser.getBitmap()!= null){
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
}