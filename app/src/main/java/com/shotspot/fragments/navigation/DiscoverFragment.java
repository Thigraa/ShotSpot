package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shotspot.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

public class DiscoverFragment extends Fragment {

    MapView mapView;
    MapController mapController;
    GeoPoint itb = new GeoPoint(41.45331929755574, 2.186670573313186);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapView = v.findViewById(R.id.open_map_view);
        mapController = (MapController)mapView.getController();
        mapController.setCenter(itb);
        mapController.setZoom(17);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        Marker marker = new Marker(mapView,getContext());
        marker.setPosition(itb);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);
//        marker.setIcon(getContext().getResources().getDrawable(R.drawable.btn_moreinfo));
        mapView.getOverlays().add(marker);
        return v;
    }
}