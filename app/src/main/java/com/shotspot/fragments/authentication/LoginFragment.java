package com.shotspot.fragments.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shotspot.R;
import com.shotspot.database.crud.Person_CRUD;
import com.shotspot.fragments.navigation.HomeFragment;
import com.shotspot.helper.Encryptor;
import com.shotspot.model.Person;

import java.util.UUID;

import static com.shotspot.activities.MainActivity.currentUser;


public class LoginFragment extends Fragment {
    private EditText usernameEdittext, passwordEdittext;
    private Button login;
    private ProgressBar progressBar;
    private FloatingActionButton closeButton;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        //Connect layout to class
        usernameEdittext = v.findViewById(R.id.usernameEdittextLogin);
        passwordEdittext = v.findViewById(R.id.passwordEdittextLogin);
        login = v.findViewById(R.id.buttonLogin);
        progressBar = v.findViewById(R.id.progressBar);
        closeButton = v.findViewById(R.id.close_login);

        //Set on click listeners
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                loginManager();
            }
        });

        return v;
    }


    //Method used to manage all the login process
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loginManager(){
        if(!usernameEdittext.getText().toString().isEmpty() && !passwordEdittext.getText().toString().isEmpty()){
            //All the fields are filled

            //Prevent undesired actions
            login.setFocusable(false);
            login.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            //Encrypting the password
            String passwordEncrypted = Encryptor.md5(passwordEdittext.getText().toString());
            System.out.println("Encrypted");
            //Query to know if the username and password matches
            Person p = Person_CRUD.getPerson(usernameEdittext.getText().toString(), passwordEncrypted);
            System.out.println(p);
            //Result
            if(p.getIdUser() > 0){
                //MATCHES
                currentUser = p;
                System.out.println("settede");
                currentUser.setToken(generateToken());
                System.out.println("Generated");
                saveToken();
                System.out.println("Saved");


                login.setFocusable(true);
                login.setClickable(true);
                //CHANGES TO HOME FRAGMENT
                replaceFragment(new HomeFragment());
                progressBar.setVisibility(View.INVISIBLE);
            }else{
                //NOT MATCHES
                Toast.makeText(getContext(), "Username or password are incorrect", Toast.LENGTH_SHORT).show();
                login.setFocusable(true);
                login.setClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }else{
            //Not all the fields are filled
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }
    //Method to replace fragments
    public void replaceFragment(Fragment f) {
        getFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }

    //Method to generate a token that saves the user instance
    public String generateToken() {
        boolean notIdentical = false;
        String uuid = null;
        while(!notIdentical){
            uuid = UUID.randomUUID().toString();
            Person p = Person_CRUD.getPerson(uuid);
            if(p.getIdUser() == 0){
                notIdentical = true;
            }
        }
        return uuid;
    }

    //Method to save the token in the Shared Preferences in private mode (Only the app which created the token can access it)
    public void saveToken(){
        Context context=this.getContext();
        SharedPreferences settings=context.getSharedPreferences("PREFERENCES", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", currentUser.getToken());
        Person_CRUD.updateToken(currentUser);
        editor.apply();
    }
}