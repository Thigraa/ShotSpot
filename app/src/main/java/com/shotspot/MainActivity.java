package com.shotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.shotspot.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connection  connection = DatabaseConnection.connect();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM dbo.PERSON");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                System.out.println(rs.getString(2));
            }
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}