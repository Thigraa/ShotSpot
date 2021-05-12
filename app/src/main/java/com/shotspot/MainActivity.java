package com.shotspot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shotspot.database.DatabaseConnection;
import com.shotspot.database.Person_CRUD;
import com.shotspot.fragments.HomeFragment;
import com.shotspot.fragments.LoginFragment;
import com.shotspot.fragments.ProfileFragment;
import com.shotspot.fragments.RegisterFragment;
import com.shotspot.fragments.WelcomeFragment;
import com.shotspot.helper.Encryptor;
import com.shotspot.model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    private Connection connection;
    public static Person currentUser;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_ShotSpot);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        connection = DatabaseConnection.connect();
        Context context=this.getApplicationContext();
        SharedPreferences settings = context.getSharedPreferences("PREFERENCES", 0);
        String isLogged= settings.getString("token", null);
        System.out.println("is Logged: "+isLogged);
        Fragment fragment;
        if(isLogged != null){
            if (savedInstanceState != null) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, "lastFragment");
                replaceFragment(fragment);
            } else {
                fragment = new HomeFragment();
                replaceFragment(fragment);

            }
        }else{
            if (savedInstanceState != null) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, "lastFragment");
                replaceFragment(fragment);
            } else {
                fragment = new WelcomeFragment();
                replaceFragment(fragment);

            }
        }


    }


    public void replaceFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            if (getSupportFragmentManager().getFragments().get(i).isVisible()) {
                Fragment f = getSupportFragmentManager().getFragments().get(i);
                getSupportFragmentManager().putFragment(savedInstanceState, "lastFragment", f);
            }
        }
    }
}