package com.shotspot.database.crud;

import com.google.android.gms.maps.model.LatLng;
import com.shotspot.database.connection.DatabaseConnection;
import com.shotspot.model.Spot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Spot_CRUD {

    private  static Connection connection = DatabaseConnection.connect();

    public static Spot getSpot(int idSpot){
        Spot spot= new Spot();
        try{
            String sql = "SELECT * FROM dbo.Spot WHERE id_spot= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                spot.setIdSpot(idSpot);
                spot.setIdUser(rs.getInt(2));
                spot.setLatitde(rs.getDouble(3));
                spot.setLongitude(rs.getDouble(4));
                spot.setDescription(rs.getString(5));
                spot.setTags(rs.getString(6));

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spot;
    }

    public static int getSpotId(int idUser){
        Spot spot= new Spot();
        try{
            String sql = "SELECT Top 1 * FROM  dbo.Spot WHERE id_user = ? ORDER BY id_spot desc ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idUser);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                spot.setIdSpot(rs.getInt(1));

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spot.getIdSpot();
    }

    public static List<Spot> getAll(){
        List<Spot> spots= new ArrayList<>();
        try{
            String sql = "SELECT * FROM dbo.Spot ORDER BY id_spot DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                Spot spot= new Spot();
                spot.setIdSpot(rs.getInt(1));
                spot.setIdUser(rs.getInt(2));
                spot.setLatitde(rs.getDouble(3));
                spot.setLongitude(rs.getDouble(4));
                spot.setDescription(rs.getString(5));
                spot.setTags(rs.getString(6));
                spots.add(spot);

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spots;
    }
    public static List<Spot> getByUserId(int userId){
        List<Spot> spots= new ArrayList<>();
        try{
            String sql = "SELECT * FROM dbo.Spot WHERE id_user = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                Spot spot= new Spot();
                spot.setIdSpot(rs.getInt(1));
                spot.setIdUser(rs.getInt(2));
                spot.setLatitde(rs.getDouble(3));
                spot.setLongitude(rs.getDouble(4));
                spot.setDescription(rs.getString(5));
                spot.setTags(rs.getString(6));
                spots.add(spot);

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spots;
    }


    public static List<Spot> getLikeds(int userId){
        List<Spot> spots= new ArrayList<>();
        try{
            //TODO Chech if this query works
            String sql = "SELECT * FROM dbo.Spot WHERE id_spot IN (SELECT id_spot FROM dbo.Liked WHERE id_user = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()){
                Spot spot= new Spot();
                spot.setIdSpot(rs.getInt(1));
                spot.setIdUser(rs.getInt(2));
                spot.setLatitde(rs.getDouble(3));
                spot.setLongitude(rs.getDouble(4));
                spot.setDescription(rs.getString(5));
                spot.setTags(rs.getString(6));
                spots.add(spot);

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return spots;
    }

    public static boolean insert(Spot spot){
        boolean inserted = false;
        String sql ="INSERT INTO Spot (id_user, latitude, longitude, sp_description, tags, create_data) VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,spot.getIdUser());
            statement.setDouble(2,spot.getLatitde());
            statement.setDouble(3,spot.getLongitude());
            statement.setString(4,spot.getDescription());
            statement.setString(5,spot.getTags());
            statement.setDate(6,spot.getDatePost());
            int updeated = statement.executeUpdate();
            if (updeated>=1) inserted=true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inserted;
    }

    public static boolean delete(Spot spot){
        return delete(spot.getIdSpot());
    }

    public static boolean delete(int idSpot){
        String sql ="DELETE from Spot where id_spot = ?";
        boolean isDeleted = false;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }


    public static boolean checkLocation(LatLng location){
        String sql ="SELECT * FROM Spot WHERE latitude = ? AND longitude = ?";
        boolean exists = false;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1,location.latitude);
            statement.setDouble(2,location.longitude);
            exists= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exists;
    }
}
