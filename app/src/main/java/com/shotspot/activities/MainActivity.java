package com.shotspot.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shotspot.R;
import com.shotspot.database.connection.DatabaseConnection;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.fragments.navigation.HomeFragment;
import com.shotspot.fragments.navigation.ProfileFragment;
import com.shotspot.fragments.WelcomeFragment;
import com.shotspot.model.Person;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    private Connection connection;
    public static Person currentUser;
    String logToken;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_ShotSpot);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectLayout();
        connection = DatabaseConnection.connect();
        getToken();
        checkLogin(savedInstanceState);
        setBottomNavigationViewClicks();

    }

    //Method to replace fragments
    public void replaceFragment(Fragment f) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }
    //Method to connect the layout to the class
    public void connectLayout(){
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);
    }
    //Method to get the saved token
    public void getToken(){
        Context context=this.getApplicationContext();
        SharedPreferences settings = context.getSharedPreferences("PREFERENCES", 0);
        logToken = settings.getString("token", null);
    }
    //Method to chek if the user have been logged before --> if logged goes to Home Fragment, else goes to Welcome Fragment
    public void checkLogin(Bundle savedInstanceState){
        Fragment fragment;
        if(logToken != null){

            //LOGGED
            currentUser = Person_CRUD.getPerson(logToken);
            System.out.println(currentUser);
            if (savedInstanceState != null) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, "lastFragment");
                replaceFragment(fragment);
            } else {
                fragment = new HomeFragment();
                replaceFragment(fragment);

            }
        }else{
            //NOT LOGGED
            if (savedInstanceState != null) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, "lastFragment");
                replaceFragment(fragment);
            } else {
                fragment = new WelcomeFragment();
                replaceFragment(fragment);

            }
        }
    }

    public void setBottomNavigationViewClicks(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeButton:
                        replaceFragment(new HomeFragment());
                        return true;

                    case R.id.discover:
                        return true;
                    case R.id.addSpot:
                        return true;
                    case R.id.profile:
                        replaceFragment(new ProfileFragment());
                        return true;

                }
                return true;
            }
        });
    }


    //Method to save the fragment when changing the default theme. DARK TO LIGHT | LIGHT TO DARK without
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            if (getSupportFragmentManager().getFragments().get(i).isVisible()) {
                Fragment f = getSupportFragmentManager().getFragments().get(i);
                getSupportFragmentManager().putFragment(savedInstanceState, "lastFragment", f);
                break;
            }
        }
    }
}