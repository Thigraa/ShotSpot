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


    //Get spot by id
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

    //Get spot id by id_user used to set images to last spot posted
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
    //Get all spots from newer to older
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

    //Get all spots by user id --> Used to get all the spots posted by an user
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

    //Get all spots liked by the user
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

    //Insert spot
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

    //Delete spot by spot --> Gets the spot id and calls the delete by ID
    public static boolean delete(Spot spot){
        return delete(spot.getIdSpot());
    }

    //Delete spot by ID
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

    //Check location of a potential spot
    // --> If the location is in use, you can't post it
    //      '--> We did this because of the zoom distance.
    //              '--> If there are more than one spot in the same latlng then the cluster keeps there and you can't see the spots
    public static boolean checkLocation(LatLng location){
        String sql ="SELECT * FROM Spot WHERE latitude = ? AND longitude = ?";
        boolean exists = false;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1,location.latitude);
            statement.setDouble(2,location.longitude);
            ResultSet resultSet = statement.executeQuery();
            int num=0;
            while (resultSet.next()){
                num++;
            }
            if (num>0) exists=true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return exists;
    }
    //Get a spot list which fits the # searched. It works like a contain method.
    public static List<Spot> searchByTags(String tags){
        String sql = "SELECT * FROM Spot WHERE tags LIKE '%"+tags+"%'";
        List<Spot> spots= new ArrayList<>();
        try{
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
}
