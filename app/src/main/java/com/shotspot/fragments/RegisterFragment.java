package com.shotspot.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shotspot.R;
import com.shotspot.database.DatabaseConnection;
import com.shotspot.database.Person_CRUD;
import com.shotspot.helper.Encryptor;
import com.shotspot.model.Person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterFragment extends Fragment {
    private EditText username, email, password, repeatPassword;
    private TextView checkBoxText;
    private CheckBox checkBox;
    private Button register;
    private ProgressBar progressBar;
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_register, container, false);
       username = v.findViewById(R.id.usernameEdittextLogin);
       email = v.findViewById(R.id.emailEdittextRegister);
       password = v.findViewById(R.id.passwordEdittextLogin);
       repeatPassword = v.findViewById(R.id.repeatpasswordEdittextRegister);
       checkBox = v.findViewById(R.id.materialCheckBox);
       checkBoxText = v.findViewById(R.id.termsAndConditions);
       register = v.findViewById(R.id.register_register);
       progressBar = v.findViewById(R.id.progressBar);

       register.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.KITKAT)
           @Override
           public void onClick(View v) {
              registerManager();
           }
       });
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void registerManager(){
        boolean usernameRepeat = false;
        boolean emailRepeat= false;
        if(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !repeatPassword.getText().toString().isEmpty()){
            if(password.getText().toString().length() >= 8){
                if(password.getText().toString().equals(repeatPassword.getText().toString())){
                    if(checkBox.isChecked()){
                        if(email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                            try {
                                progressBar.setVisibility(View.VISIBLE);
                                register.setClickable(false);
                                register.setFocusable(false);
                                PreparedStatement preparedStatement = DatabaseConnection.connect().prepareStatement("SELECT * FROM dbo.PERSON WHERE USERNAME = ?");
                                preparedStatement.setString(1, username.getText().toString());
                                ResultSet resultSet = preparedStatement.executeQuery();
                                if(resultSet.next()) {
                                    usernameRepeat = true;
                                }
                                PreparedStatement preparedStatement1 = DatabaseConnection.connect().prepareStatement("SELECT * FROM dbo.PERSON WHERE EMAIL = ?");
                                preparedStatement1.setString(1, email.getText().toString());
                                ResultSet resultSet1 = preparedStatement1.executeQuery();
                                if(resultSet1.next()){
                                    emailRepeat = true;
                                }
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            if(!usernameRepeat){
                                if(!emailRepeat){
                                    String passwordEncrypted = Encryptor.md5(password.getText().toString());
                                    Person person = new Person(username.getText().toString(), email.getText().toString(), passwordEncrypted, null);
                                    Person_CRUD.insert(person);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    register.setFocusable(true);
                                    register.setClickable(true);
                                    replaceFragment(new LoginFragment());

                                }else{
                                    Toast.makeText(getContext(), "Email in use", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    register.setFocusable(true);
                                    register.setClickable(true);
                                }
                            }else{
                                Toast.makeText(getContext(), "Username in use", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                                register.setFocusable(true);
                                register.setClickable(true);
                            }
                        }else{
                            Toast.makeText(getContext(), "Please type a valid format email", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getContext(), "Terms and Conditions must be accepted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(), "Password must contain minimum 8 characters", Toast.LENGTH_SHORT).show();
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
}