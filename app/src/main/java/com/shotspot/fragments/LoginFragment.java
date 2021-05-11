package com.shotspot.fragments;

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

import com.shotspot.R;
import com.shotspot.database.DatabaseConnection;
import com.shotspot.database.Person_CRUD;
import com.shotspot.helper.Encryptor;
import com.shotspot.model.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.shotspot.MainActivity.currentUser;


public class LoginFragment extends Fragment {
    private EditText usernameEdittext, passwordEdittext;
    private Button login;
    private ProgressBar progressBar;
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
        usernameEdittext = v.findViewById(R.id.usernameEdittextLogin);
        passwordEdittext = v.findViewById(R.id.passwordEdittextLogin);
        login = v.findViewById(R.id.buttonLogin);
        progressBar = v.findViewById(R.id.progressBar);

        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                loginManager();
            }
        });

        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loginManager(){
        if(!usernameEdittext.getText().toString().isEmpty() && !passwordEdittext.getText().toString().isEmpty()){
            login.setFocusable(false);
            login.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
            String passwordEncrypted = Encryptor.md5(passwordEdittext.getText().toString());
            try {
                PreparedStatement preparedStatement = DatabaseConnection.connect().prepareStatement("SELECT * FROM dbo.PERSON WHERE USERNAME = ? AND PASS = ?");
                preparedStatement.setString(1, usernameEdittext.getText().toString());
                preparedStatement.setString(2, passwordEncrypted);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    int userId = resultSet.getInt("id_user");
                    currentUser = Person_CRUD.getPerson(userId);
                    currentUser.setToken(generateToken());
                    saveToken();
                    System.out.println(currentUser);
                    login.setFocusable(true);
                    login.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    replaceFragment(new HomeFragment());
                }else{
                    Toast.makeText(getContext(), "Username or password are incorrect", Toast.LENGTH_SHORT).show();
                    login.setFocusable(true);
                    login.setClickable(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }catch (SQLException e){
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void replaceFragment(Fragment f) {
        getFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }
    public String generateToken() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
    public void saveToken(){
        Context context=this.getContext();
        SharedPreferences settings=context.getSharedPreferences("PREFERENCES", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", currentUser.getToken());
        System.out.println(currentUser.getToken());
        editor.commit();
    }
}