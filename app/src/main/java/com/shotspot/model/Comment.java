package com.shotspot.model;

public class Comment {
    int idComment;
    int idSpot;
    int idUser;
    String comment;

    public Comment() {
    }

    public Comment(int idComment, int idSpot, int idUser, String comment) {
        this.idComment = idComment;
        this.idSpot = idSpot;
        this.idUser = idUser;
        this.comment = comment;
    }

    public int getIdComment() {
        return idComment;
    }

    public void setIdComment(int idComment) {
        this.idComment = idComment;
    }

    public int getIdSpot() {
        return idSpot;
    }

    public void setIdSpot(int idSpot) {
        this.idSpot = idSpot;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "idComment=" + idComment +
                ", idSpot=" + idSpot +
                ", idUser=" + idUser +
                ", comment=" + comment +
                '}';
    }
}
