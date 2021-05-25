package com.shotspot.fragments.navigation;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.shotspot.R;
import com.shotspot.activities.MainActivity;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.fragments.navigation.profile.EditProfileFragment;
import com.shotspot.fragments.navigation.profile.LikedSpotsFragment;
import com.shotspot.fragments.navigation.profile.MySpotsFragment;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.shotspot.activities.MainActivity.bottomNavigationView;
import static com.shotspot.activities.MainActivity.currentUser;


public class ProfileFragment extends Fragment {

    CircleImageView profileImageView;
    TextView usernameTV;
    ImageButton logOutButton, editProfileButton;
    BottomNavigationView profileSpotsNavigation;
    EditText usernameET;
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
        profileSpotsNavigation = v.findViewById(R.id.profileSpotNavigation);
        usernameTV.setText(currentUser.getUsername());
        usernameET.setText(currentUser.getUsername());
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

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}