package com.shotspot.database.crud;

import com.shotspot.database.connection.DatabaseConnection;
import com.shotspot.model.Like;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Like_CRUD {

    private  static Connection connection = DatabaseConnection.connect();

    public static Like getLike(int idSpot, int idUser){
        String sql = "SELECT * FROM Liked where id_spot = ? AND id_user =?";
        Like like = new Like();
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            statement.setInt(2,idUser);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                like.setId_user(rs.getInt(1));
                like.setId_spot(rs.getInt(2));
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return like;
    }
    public static int getNumLikes(int idSpot){
        String sql = "SELECT COUNT(*) FROM Liked WHERE id_spot = ?";
        int likes=0;
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                likes =rs.getInt(1);

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return likes;
    }

    public static boolean insert(Like like){
        boolean inserted = false;
        String sql ="INSERT INTO Liked (id_user, id_spot) VALUES (?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,like.getId_user());
            statement.setInt(2,like.getId_spot());
            int updeated = statement.executeUpdate();
            if (updeated>=1) inserted=true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inserted;
    }

    public  static boolean delete(Like like){
        String sql ="DELETE FROM Liked where id_spot = ? AND id_user = ?";
        boolean isDeleted = false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,like.getId_spot());
            statement.setInt(2,like.getId_user());
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }

    public static List<Like> getLikes(int idUser){
        String sql = "SELECT * FROM Liked where id_user =?";
        List<Like> likeList = new ArrayList<>();
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idUser);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                Like like = new Like();
                like.setId_user(rs.getInt(1));
                like.setId_spot(rs.getInt(2));
                likeList.add(like);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return likeList;
    }

}
