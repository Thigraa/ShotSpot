package com.shotspot.database;

import com.shotspot.model.Comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Comment_CRUD {
    private  static Connection connection = DatabaseConnection.connect();

    public static Comment getComment(int idComment){
        Comment comment = new Comment();
        try{
            String sql = "SELECT * FROM Comment WHERE id_comment = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idComment);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                comment.setIdComment(idComment);
                comment.setIdSpot(rs.getInt(2));
                comment.setIdUser(rs.getInt(3));
                comment.setComment(rs.getString(4));

            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return comment;
    }

    public static List<Comment> getCommentBySpotID(int idSpot){
        List<Comment> comments = new ArrayList<>();

        try{
            String sql = "SELECT * FROM Comment WHERE id_spot = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idSpot);
            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                Comment comment = new Comment();
                comment.setIdComment(rs.getInt(1));
                comment.setIdSpot(rs.getInt(2));
                comment.setIdUser(rs.getInt(3));
                comment.setComment(rs.getString(4));
                comments.add(comment);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return comments;
    }

    public static boolean insert(Comment comment){
        boolean inserted = false;
        String sql ="INSERT INTO Comment (id_spot, id_user, comment) VALUES (?,?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,comment.getIdSpot());
            statement.setInt(2,comment.getIdUser());
            statement.setString(3,comment.getComment());
            int updeated = statement.executeUpdate();
            if (updeated>=1) inserted=true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return inserted;
    }

    public static boolean delete(int idComment){
        String sql ="DELETE FROM Comment where id_comment = ?";
        boolean isDeleted = false;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,idComment);
            isDeleted= statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isDeleted;
    }
}
