package com.shotspot.model;

public class Person {
    int idUser;
    String username;
    String email;
    String password;
    String imageURL;

    public Person() {
    }

    public Person(int idUser, String username, String email, String password, String imageURL) {
        this.idUser = idUser;
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
