package com.shotspot.model;

public class SpotImage {
    int idImage;
    int idUser;
    int idSpot;
    String imageURL;

    public SpotImage() {
    }

    public SpotImage(int idImage, int idUser, int idSpot, String imageURL) {
        this.idImage = idImage;
        this.idUser = idUser;
        this.idSpot = idSpot;
        this.imageURL = imageURL;
    }

    public int getIdImage() {
        return idImage;
    }

    public void setIdImage(int idImage) {
        this.idImage = idImage;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdSpot() {
        return idSpot;
    }

    public void setIdSpot(int idSpot) {
        this.idSpot = idSpot;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "SpotImage{" +
                "idImage=" + idImage +
                ", idUser=" + idUser +
                ", idSpot=" + idSpot +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }
}
