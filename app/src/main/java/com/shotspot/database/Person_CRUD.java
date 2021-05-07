package com.shotspot.database;


import com.shotspot.model.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Person_CRUD {
    private  static Connection connection = DatabaseConnection.connect();

    public static Person getPerson(int idUser){
        Person person = new Person();
        try{
            String sql = "SELECT * FROM dbo.Person WHERE id_user= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idUser);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                person.setIdUser(idUser);
                person.setUsername(rs.getString(2));
                person.setImageURL(rs.getString(3));
                person.setEmail(rs.getString(4));
                person.setPassword(rs.getString(5));
                person.setToken(rs.getString(6));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return person;
    }

    public static boolean insert(Person person){
        boolean inserted = false;
        String sql ="INSERT INTO Person (username, image_url, email, pass, token) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,person.getUsername());
            statement.setString(2,person.getImageURL());
            statement.setString(3,person.getEmail());
            statement.setString(4,person.getPassword());
            statement.setString(5,person.getToken());
            int updeated = statement.executeUpdate();
            if (updeated>=1) inserted=true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inserted;
    }

    public static boolean delete(Person person){
        return delete(person.getIdUser());
    }


    public static boolean delete(int idUser){

        String sql ="DELETE from Person where id_user = ?";
        boolean isDeleted = false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idUser);
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }

}
