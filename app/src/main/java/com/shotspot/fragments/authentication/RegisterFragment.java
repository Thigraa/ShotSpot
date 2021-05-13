package com.shotspot.fragments.authentication;

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
import com.shotspot.database.connection.DatabaseConnection;
import com.shotspot.database.crud.Person_CRUD;
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_register, container, false);
       //Connect layout to class
       username = v.findViewById(R.id.usernameEdittextLogin);
       email = v.findViewById(R.id.emailEdittextRegister);
       password = v.findViewById(R.id.passwordEdittextLogin);
       repeatPassword = v.findViewById(R.id.repeatpasswordEdittextRegister);
       checkBox = v.findViewById(R.id.materialCheckBox);
       checkBoxText = v.findViewById(R.id.termsAndConditions);
       register = v.findViewById(R.id.register_register);
       progressBar = v.findViewById(R.id.progressBar);
        //Set on click listener
       register.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.KITKAT)
           @Override
           public void onClick(View v) {
               //Manages the register
                registerManager();
           }
       });
        return v;
    }

    //Method that manages the register process.
    // Checks:
        // all the fields are filled
        // the password is 8 characters long
        // the both password match
        // checkbox is checked
        // the email is in a valid form and not in use
        // the username is not in use
    //Then creates a person in the database (Insert)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void registerManager(){
        boolean usernameRepeat = false;
        boolean emailRepeat= false;
        if(!username.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !repeatPassword.getText().toString().isEmpty()){
            // all the fields are filled
            if(password.getText().toString().length() >= 8){
                // the password is 8 characters long
                if(password.getText().toString().equals(repeatPassword.getText().toString())){
                    // the both password match
                    if(checkBox.isChecked()){
                        // checkbox is checked
                        if(email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){
                            // the email is in a valid form
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
                                // the email is not in use
                                if(!emailRepeat){
                                    // the username is not in use
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
    //Method to replace fragments
    public void replaceFragment(Fragment f) {
        getFragmentManager().beginTransaction()
                .replace(R.id.navHost, f, f.getClass().getSimpleName()).addToBackStack(null)
                .commit();
    }
}