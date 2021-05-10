package com.shotspot.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.shotspot.R;
import com.shotspot.database.Person_CRUD;
import com.shotspot.database.SpotImage_CRUD;
import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;
import java.util.List;

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

    public Bitmap getBitmap(){
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        long imageLength = 0;
        Bitmap bitmap = null;
        try {

            ImageManager.GetImage(getImageURL(), imageStream, imageLength);

            byte[] buffer = imageStream.toByteArray();

             bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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
