package com.shotspot.model;

public class Like {
    int id_spot;
    int id_user;

    public Like() {
    }

    public Like(int id_spot, int id_user) {
        this.id_spot = id_spot;
        this.id_user = id_user;
    }

    public int getId_spot() {
        return id_spot;
    }

    public void setId_spot(int id_spot) {
        this.id_spot = id_spot;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "Like{" +
                "id_spot=" + id_spot +
                ", id_user=" + id_user +
                '}';
    }
}
