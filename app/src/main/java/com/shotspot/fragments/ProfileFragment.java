package com.shotspot.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shotspot.R;
import com.shotspot.database.Person_CRUD;
import com.shotspot.database.SpotImage_CRUD;
import com.shotspot.model.Person;
import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView profileImageView;
    TextView usernameTV;

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
        //TODO Change to use current user
        Person user = Person_CRUD.getPerson(4);
        usernameTV = v.findViewById(R.id.profileUsernameTextView);
        profileImageView = v.findViewById(R.id.profileUserCircleImage);
        usernameTV.setText(user.getUsername());
        try {
            profileImageView.setImageBitmap(user.getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Fragment fragment = new MySpotsFragment();
        replaceFragment(fragment);
        return v;
    }

    public void replaceFragment(Fragment f){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.profileSpots, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }

}