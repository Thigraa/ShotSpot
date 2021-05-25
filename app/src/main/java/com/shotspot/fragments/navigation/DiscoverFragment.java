package com.shotspot.fragments.navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.shotspot.R;
import com.shotspot.database.crud.Spot_CRUD;
import com.shotspot.model.MyCluster;
import com.shotspot.model.Spot;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.shotspot.activities.MainActivity.bottomNavigationView;

public class DiscoverFragment extends Fragment implements OnMapReadyCallback {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    MapView gMapView;
    private GoogleMap gMap;
    private View rootView;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Spot> spotList = new ArrayList<>();
    Thread thread;
    static LatLng myPosition;
    CameraPosition cameraPosition;
    private ClusterManager<MyCluster> clusterManager;
    SearchView searchView;

    TextView mapTheme;

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
        pedirPermiso();
        bottomNavigationView.setVisibility(View.VISIBLE);
        searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(getContext());
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(addressList.size()>=1) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }else{
                        Snackbar.make(rootView, "No results found", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startGMap();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                spotList = Spot_CRUD.getAll();
            }
        });
        thread.start();
    }

    private void startGMap() {

        gMapView = rootView.findViewById(R.id.gMapView);
        if (gMapView != null) {
            gMapView.onCreate(null);
            gMapView.onResume();
            gMapView.getMapAsync(this);
        }

    }



    private boolean pedirPermiso() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (gMap != null) {
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setCompassEnabled(false);
                gMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } else {
            pedirPermiso();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        mapTheme = rootView.findViewById(R.id.discoverMapTheme);
        int mapStyle = getResources().getIdentifier(mapTheme.getText().toString(),"raw",getContext().getPackageName());
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),mapStyle ));
        gMap.setMaxZoomPreference(18);
        gMap.setMinZoomPreference(3);
        enableMyLocation();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setUpClusterer();
        Bundle positionBundle = getArguments();
        if(positionBundle == null){
            getPosition();
        }
        else{
            LatLng target = new LatLng(positionBundle.getDouble("latitude"), positionBundle.getDouble("longitude"));
            cameraPosition = new CameraPosition.Builder()
                    .target(target)
                    .zoom(15)
                    .bearing(0)
                    .tilt(30)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

//        TODO personalize the window to show on the marker


        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Bundle bundle = new Bundle();
                bundle.putDouble("longitud", latLng.longitude);
                bundle.putDouble("latitud", latLng.latitude);
//                PostFragment f = new PostFragment();
                getPosition();
            }
        });
    }

    private void getPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            pedirPermiso();
        }
        enableMyLocation();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println(myPosition);
                    cameraPosition = new CameraPosition(myPosition, 15, 3, 1);
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    Snackbar.make(rootView, "Enable the location please", BaseTransientBottomBar.LENGTH_SHORT).show();
                }

            }
        });
        System.out.println(myPosition);
    }

    private void añadirMarcadores() {
        for (Spot spot :
                spotList) {
            MyCluster marker = new MyCluster(spot.getLatitde(),spot.getLongitude(),spot.getDescription(),spot.getTags(), spot.getIdSpot());
            clusterManager.addItem(marker);
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void setUpClusterer() {
        // Initialize the manager with the context and the gMap.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyCluster>(getContext(), gMap);

        // Point the gMap's listeners at the listeners implemented by the cluster
        // manager.
        gMap.setOnCameraIdleListener(clusterManager);
        gMap.setOnMarkerClickListener(clusterManager);

        //TODO Make a function when you click on a item/marker
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyCluster>() {
            @Override
            public boolean onClusterItemClick(MyCluster item) {
                return false;
            }
        });
        // Add cluster items (markers) to the cluster manager.
//        addItems();
        añadirMarcadores();

    }
//
    private void addItems() {
        // Position the map.
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.503186, 2.126446), 10));

        // Set some lat/lng coordinates to start with.
        double lat = 41.503186;
        double lng = 2.126446;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyCluster offsetItem = new MyCluster(lat, lng, "Title " + i, "Snippet " + i, 1);
            clusterManager.addItem(offsetItem);
        }
        clusterManager.setAnimation(true);
    }
}