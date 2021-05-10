package com.shotspot.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.shotspot.R;

public class WelcomeFragment extends Fragment {
    Button register, login;
    public WelcomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        register = v.findViewById(R.id.register_welcome);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new RegisterFragment());
            }
        });
        login = v.findViewById(R.id.login_welcome);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LoginFragment());
            }
        });
        return v;
    }

    public void replaceFragment(Fragment f){
        getFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }
}