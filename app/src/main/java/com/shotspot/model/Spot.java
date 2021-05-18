package com.shotspot.model;

import java.sql.Date;

public class Spot {
    int idSpot;
    int idUser;
    double latitde;
    double longitude;
    String description;
    String tags;
    Date datePost;

    public Spot() {
    }

    public Spot(int idUser, double latitde, double longitude, String description, String tags, Date datePost) {
        this.idUser = idUser;
        this.latitde = latitde;
        this.longitude = longitude;
        this.description = description;
        this.tags = tags;
        this.datePost = datePost;
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

    public double getLatitde() {
        return latitde;
    }

    public void setLatitde(double latitde) {
        this.latitde = latitde;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Date getDatePost() {
        return datePost;
    }

    public void setDatePost(Date datePost) {
        this.datePost = datePost;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "idSpot=" + idSpot +
                ", idUser=" + idUser +
                ", latitde=" + latitde +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", tags='" + tags + '\'' +
                ", datePost=" + datePost +
                '}';
    }
}
