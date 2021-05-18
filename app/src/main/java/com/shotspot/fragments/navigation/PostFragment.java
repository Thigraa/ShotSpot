package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.shotspot.database.crud.SpotImage_CRUD;
import com.shotspot.database.crud.Spot_CRUD;
import com.shotspot.database.storage.ImageManager;
import com.shotspot.model.Spot;
import com.shotspot.model.SpotImage;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.shotspot.activities.MainActivity.bottomNavigationView;
import static com.shotspot.activities.MainActivity.currentUser;


public class PostFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    EditText descriptionEdittext, tagsEdittext;
    public static RecyclerView recyclerViewTags;
    public static ArrayList <String> myTags;
    private Button addSpot;
    private ImageView image1, image2, image3;
    RecyclerAdapterTags adapterTags;
    LinearLayoutManager manager;
    private MapView mapView;
    private GoogleMap gMap;
    private MarkerOptions marker;
    private LatLng postLocation;
    private CameraPosition camera;
    private TextView mapTheme;
    String nombreImagen;
    byte[] thumb_byte;
    File url;

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
        image1 = v.findViewById(R.id.imageButton1);
        postLocation = null;
        image1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                image1.setImageDrawable(defaultImage);
                return true;
            }

        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                if(image1.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        image2 = v.findViewById(R.id.imageButton2);
        image2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                image2.setImageDrawable(defaultImage);
                return true;
            }

        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                if(image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        image3 = v.findViewById(R.id.imageButton3);
        image3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                image3.setImageDrawable(defaultImage);
                return true;
            }

        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                if(image3.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        addSpot = v.findViewById(R.id.buttonAddPost);
        addSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postLocation != null){
                    Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                    if(!image1.getDrawable().getConstantState().equals(defaultImage.getConstantState()) ||
                            !image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())||
                            !image3.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                        String tags="";
                        for(String s : myTags){
                            tags+=s;
                        }
                        long millis=System.currentTimeMillis();
                        Spot spot = new Spot(currentUser.getIdUser(), postLocation.latitude, postLocation.longitude, descriptionEdittext.getText().toString(), tags, new Date(millis));
                        String imageName1="";
                        String imageName2="";
                        String imageName3="";
                        if(Spot_CRUD.insert(spot)){
                            int spotId = Spot_CRUD.getSpotId(currentUser.getIdUser());
                            if(!image1.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                                Bitmap bitmap1 = ImageManager.drawableToBitmap(image1.getDrawable());
                                InputStream inputStream = comprimirImagen(bitmap1);
                                try {
                                    imageName1 = ImageManager.uploadImage(inputStream, inputStream.available());
                                    SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName1 ));
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                            if(!image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                                Bitmap bitmap2 = ImageManager.drawableToBitmap(image2.getDrawable());
                                InputStream inputStream = comprimirImagen(bitmap2);
                                try {
                                    imageName2 = ImageManager.uploadImage(inputStream, inputStream.available());
                                    SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName2 ));
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                            if(!image3.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                                Bitmap bitmap3 = ImageManager.drawableToBitmap(image3.getDrawable());
                                InputStream inputStream = comprimirImagen(bitmap3);
                                try {
                                    imageName3 = ImageManager.uploadImage(inputStream, inputStream.available());
                                    SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName3 ));
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }else{
                            Toast.makeText(getContext(), "The upload failed, try again later", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        Toast.makeText(getContext(), "Please insert an image", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
            recortarImagen(imageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                url = new File(resultUri.getPath());
                Drawable defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
                if(image1.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                    Picasso.with(getContext()).load(url).into(image1);
                }
                else if(image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                    Picasso.with(getContext()).load(url).into(image2);
                }else{
                    Picasso.with(getContext()).load(url).into(image3);
                }
            }
        }
    }
    private void recortarImagen(Uri imagenUri){
        CropImage.activity(imagenUri).setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(1280, 720)
                .setAspectRatio(16,9).start(getContext(), PostFragment.this);
    }

    private InputStream comprimirImagen(Bitmap thumb_bitmap){
        try {
            thumb_bitmap = new Compressor(getContext())
                    .setMaxHeight(720)
                    .setMaxWidth(1280)
                    .setQuality(90)
                    .compressToBitmap(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        thumb_byte = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(thumb_byte);

    }
//
}