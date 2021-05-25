package com.shotspot.database.crud;

import com.shotspot.database.connection.DatabaseConnection;
import com.shotspot.model.SpotImage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpotImage_CRUD {

    private  static Connection connection = DatabaseConnection.connect();

    public static SpotImage getSpotImage(int idImage){
        SpotImage spotImage = new SpotImage();
        try{
            String sql = "SELECT * FROM dbo.Spot_Image WHERE id_image= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idImage);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                spotImage.setIdImage(idImage);
                spotImage.setIdUser(rs.getInt(2));
                spotImage.setIdSpot(rs.getInt(3));
                spotImage.setImageURL(rs.getString(4));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spotImage;
    }

    public static SpotImage getSpotImage(String imageURL){
        SpotImage spotImage = new SpotImage();
        try{
            String sql = "SELECT * FROM dbo.Spot_Image WHERE image_url= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,imageURL);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                spotImage.setIdImage(rs.getInt(1));
                spotImage.setIdUser(rs.getInt(2));
                spotImage.setIdSpot(rs.getInt(3));
                spotImage.setImageURL(rs.getString(4));

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spotImage;
    }

    public static boolean insert(SpotImage spotImage){
        boolean inserted = false;
        String sql ="INSERT INTO Spot_Image (id_user, id_spot, image_url) VALUES (?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,spotImage.getIdUser());
            statement.setInt(2,spotImage.getIdSpot());
            statement.setString(3,spotImage.getImageURL());
            int updeated = statement.executeUpdate();
            if (updeated>=1) inserted=true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inserted;
    }

    public static boolean delete(SpotImage spotImage){
        return delete(spotImage.getIdImage());
    }


    public static boolean delete(int idImage){

        String sql ="DELETE FROM Spot_Image WHERE id_image= ?";
        boolean isDeleted = false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idImage);
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }

    public static boolean delete(String imageURL){

        String sql ="DELETE FROM Spot_Image WHERE image_url= ?";
        boolean isDeleted = false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,imageURL);
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }

    public static List<SpotImage> getImagesBySpotId(int idSpot){
        List<SpotImage> spotImages = new ArrayList<>();
        try{
            String sql = "SELECT * FROM dbo.Spot_Image WHERE id_spot= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                SpotImage spotImage = new SpotImage();
                spotImage.setIdImage(rs.getInt(1));
                spotImage.setIdUser(rs.getInt(2));
                spotImage.setIdSpot(rs.getInt(3));
                spotImage.setImageURL(rs.getString(4));
                spotImages.add(spotImage);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spotImages;
    }
}
