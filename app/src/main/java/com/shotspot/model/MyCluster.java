package com.shotspot.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class  MyCluster implements ClusterItem {

    private final LatLng position;
    private final String title;
    private final String snippet;
    private final  int idSpot;


    public MyCluster(double lat, double lng, String title, String snippet, int idSpot) {
        this.idSpot = idSpot;
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }
}
