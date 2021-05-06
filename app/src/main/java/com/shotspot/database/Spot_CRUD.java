package com.shotspot.database;

import com.shotspot.model.Spot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Spot_CRUD {
    private  static Connection connection = DatabaseConnection.connect();

    public static Spot getSpot(int idSpot){
        Spot spot= new Spot();
        try{
            String sql = "SELECT * FROM dbo.Spot WHERE id_spot= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            ResultSet rs = statement.executeQuery();

            if (rs.first()){
                spot.setIdSpot(idSpot);
                spot.setIdUser(rs.getInt(2));
            }
            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spot;
    }
}
