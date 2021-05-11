package com.shotspot.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shotspot.storage.ImageManager;

import java.io.ByteArrayOutputStream;

public class Person {
    int idUser;
    String username;
    String email;
    String password;
    String imageURL;
    String token;


    public Person() {
    }

    public Person(String username, String email, String password, String imageURL) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return "Person{" +
                "id_user=" + idUser +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }

}
