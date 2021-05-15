package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shotspot.R;
import com.shotspot.activities.MainActivity;
import com.shotspot.database.crud.Spot_CRUD;
import com.shotspot.model.Spot;

import org.osmdroid.config.Configuration;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    com.google.android.gms.maps.MapView gMapView;
    private GoogleMap gMap;
    private View rootView;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Spot> spotList = new ArrayList<>();
    Thread thread;
    LatLng myPosition ;
    CameraPosition cameraPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);

        checkReadStorage();


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startGMap();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                spotList =Spot_CRUD.getAll();
            }
        });
        thread.start();
    }

    private void startGMap()  {

        gMapView = rootView.findViewById(R.id.gMapView);
        if (gMapView!= null){
            gMapView.onCreate(null);
            gMapView.onResume();
            gMapView.getMapAsync(this);
        }

    }

//    private void startOpenMap(View v) {
//        MapView openMapView;
//        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
//        openMapView = v.findViewById(R.id.open_map_view);
//        mapController = (MapController) openMapView.getController();
//        mapController.setCenter(itb);
//        mapController.setZoom(17);
//        openMapView.setMultiTouchControls(true);
//        openMapView.setBuiltInZoomControls(false);
//
//        Marker marker = new Marker(openMapView,getContext());
//        marker.setPosition(itb);
//        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);
////        marker.setIcon(getContext().getResources().getDrawable(R.drawable.btn_moreinfo));
//        openMapView.getOverlays().add(marker);
//    }

    private void checkReadStorage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
    }

    private void pedirPermiso() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (gMap!=null){
                gMap.setMyLocationEnabled(true);

            }
        }
        else {
            pedirPermiso();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.dark_map_style));
        gMap.setMaxZoomPreference(18);
        gMap.setMinZoomPreference(1);
        enableMyLocation();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        añadirMarcadores();
        getPosition();




//        TODO personalize the window to show on the marker
//        gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Nullable
//            @Override
//            public View getInfoWindow(@NonNull Marker marker) {
//                return null;
//            }
//
//            @Nullable
//            @Override
//            public View getInfoContents(@NonNull Marker marker) {
//                return null;
//            }
//        });

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Bundle bundle = new Bundle();
                bundle.putDouble("longitud",latLng.longitude);
                bundle.putDouble("latitud",latLng.latitude);
//                PostFragment f = new PostFragment();
//                getPosition();
            }
        });
    }

    private void getPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            pedirPermiso();
        }
        enableMyLocation();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Toast.makeText(getContext(), "Latitude" + location.getLatitude(), Toast.LENGTH_SHORT).show();
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println(myPosition);
                   cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(15).bearing(0).tilt(30).build();
                   gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }else Toast.makeText(getContext(), "Location not fouded, please activated", Toast.LENGTH_SHORT).show();
            }
        });
//        TODO no cambia bien la posicion
    }

    private void añadirMarcadores() {
        for (Spot spot :
                spotList) {
            MarkerOptions marker = new MarkerOptions();
            LatLng latLng = new LatLng(spot.getLatitde(),spot.getLongitude());
            marker.position(latLng);
            marker.title(spot.getTags());
            marker.snippet(spot.getDescription());
            marker.draggable(false);
            gMap.addMarker(marker);
        }
    }
}