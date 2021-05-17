package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shotspot.R;
import com.shotspot.adapter.RecyclerAdapterTags;

import java.io.File;
import java.util.ArrayList;

import static com.shotspot.activities.MainActivity.bottomNavigationView;


public class PostFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    EditText descriptionEdittext, tagsEdittext;
    public static RecyclerView recyclerViewTags;
    public static ArrayList <String> myTags;
    RecyclerAdapterTags adapterTags;
    LinearLayoutManager manager;
    private MapView mapView;
    private GoogleMap gMap;
    private MarkerOptions marker;
    private LatLng postLocation;
    private CameraPosition camera;
    private TextView mapTheme;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        bottomNavigationView.setVisibility(View.VISIBLE);
        descriptionEdittext = v.findViewById(R.id.edittextDescription);
        tagsEdittext = v.findViewById(R.id.tagsEdittext);
        recyclerViewTags = v.findViewById(R.id.recyclerTags);
        mapView = v.findViewById(R.id.mapViewPost);
        mapTheme = v.findViewById(R.id.mapTheme);
        setUpRecyclerTags();
        setUpMap();



        return v;
    }

    private void setUpMap() {
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        if(mapView != null){
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    private void setUpRecyclerTags() {
        myTags = new ArrayList<>();
        adapterTags = new RecyclerAdapterTags(myTags);
        recyclerViewTags.setAdapter(adapterTags);
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewTags.setLayoutManager(manager);
        tagsEdittext.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_UP)
                {
                    Toast.makeText(getContext(), ""+keyCode, Toast.LENGTH_SHORT).show();
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_SPACE:
                        case KeyEvent.KEYCODE_ENTER:
                            if(!tagsEdittext.getText().toString().equals("") && !tagsEdittext.getText().toString().equals(" ")){
                                addTag();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void addTag(){
        if(recyclerViewTags.getVisibility() == View.GONE){
            recyclerViewTags.setVisibility(View.VISIBLE);
        }
        String tag =  "#"+tagsEdittext.getText().toString().trim().replaceAll(" ", "");
        myTags.add(tag);
        tagsEdittext.setText("");
        adapterTags.notifyDataSetChanged();
        recyclerViewTags.smoothScrollToPosition(myTags.size());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Bundle positionBundle = getArguments();
        int mapStyle = getResources().getIdentifier(mapTheme.getText().toString(),"raw",getContext().getPackageName());
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),mapStyle ));
            if (!success){
                System.out.println();
            }
        }catch (Resources.NotFoundException e){
            e.printStackTrace();
        }
        enableMyLocation();
        gMap.setMinZoomPreference(13);
        gMap.setMaxZoomPreference(18);
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            FusedLocationProviderClient fusedLocationProviderClient;
            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!= null){
                        gMap.clear();
                        postLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        camera =new CameraPosition.Builder()
                                .target(postLocation)
                                .zoom(15)
                                .bearing(0)
                                .tilt(10)
                                .build();
                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
                    }
                }
            });

        }

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    FusedLocationProviderClient fusedLocationProviderClient;
                    fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!= null){
                                gMap.clear();
                                postLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                marker = new MarkerOptions();
                                marker.position(postLocation);
//                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark2));
                                marker.draggable(false);
                                gMap.addMarker(marker);
                                camera =new CameraPosition.Builder()
                                        .target(postLocation)
                                        .zoom(15)
                                        .bearing(0)
                                        .tilt(10)
                                        .build();
                                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

                            }
                        }
                    });

                }
            }
        });
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                gMap.clear();
                postLocation = new LatLng(latLng.latitude, latLng.longitude);
                marker = new MarkerOptions();
                marker.position(postLocation);
//                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark2));
                marker.draggable(false);
                gMap.addMarker(marker);
                camera =new CameraPosition.Builder()
                        .target(postLocation)
                        .zoom(15)
                        .bearing(0)
                        .tilt(10)
                        .build();
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

            }
        });

    }
    private void enableMyLocation(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(gMap!=null){
                gMap.setMyLocationEnabled(true);
            }
        }
    }
//
}