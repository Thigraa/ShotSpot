package com.shotspot.fragments.navigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    Drawable defaultImage;
    byte[] thumb_byte;
    File url1, url2, url3;

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
        defaultImage = getResources().getDrawable(R.drawable.ic_launcher_foreground);
        image1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                image1.setImageDrawable(defaultImage);
                return true;
            }

        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(image1.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    checkReadStorage();
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        image2 = v.findViewById(R.id.imageButton2);
        image2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                image2.setImageDrawable(defaultImage);
                return true;

            }

        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    checkReadStorage();
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        image3 = v.findViewById(R.id.imageButton3);
        image3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                image3.setImageDrawable(defaultImage);
                return true;
            }

        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(image3.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                    checkReadStorage();
                    CropImage.startPickImageActivity(getContext(), PostFragment.this);
                }
            }
        });
        addSpot = v.findViewById(R.id.buttonAddPost);
        addSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postLocation != null){

                    if (!Spot_CRUD.checkLocation(postLocation)){

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
                                    InputStream inputStream1 = comprimirImagen(bitmap1,url1);
                                    try {
                                        imageName1 = ImageManager.uploadImage(inputStream1, inputStream1.available());
                                        SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName1 ));
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }
                                if(!image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                                    Bitmap bitmap2 = ImageManager.drawableToBitmap(image2.getDrawable());
                                    InputStream inputStream2 = comprimirImagen(bitmap2,url2);
                                    try {
                                        imageName2 = ImageManager.uploadImage(inputStream2, inputStream2.available());
                                        SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName2 ));
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }
                                if(!image3.getDrawable().getConstantState().equals(defaultImage.getConstantState())){
                                    Bitmap bitmap3 = ImageManager.drawableToBitmap(image3.getDrawable());
                                    InputStream inputStream3 = comprimirImagen(bitmap3,url3);
                                    try {
                                        imageName3 = ImageManager.uploadImage(inputStream3, inputStream3.available());
                                        SpotImage_CRUD.insert(new SpotImage(currentUser.getIdUser(), spotId,imageName3 ));
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }
                            }else{
                                Snackbar.make(v, "The upload failed, try again later", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }


                        }else{
                            Snackbar.make(v, "Please insert an image", BaseTransientBottomBar.LENGTH_SHORT).show();

                        }
                    }else {
                        Snackbar.make(v,"That Location already exits",BaseTransientBottomBar.LENGTH_SHORT)
                        .setAction("See", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Fragment f = new DiscoverFragment();
                                Bundle bundle = new Bundle();
                                bundle.putDouble("latitude",postLocation.latitude);
                                bundle.putDouble("longitude",postLocation.longitude);
                                f.setArguments(bundle);
                                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().addToBackStack(null)
                                        .replace(R.id.navHost, f)
                                        .commit();
                            }
                        }).setActionTextColor(R.attr.bgEdittext).show();



                        
                    }
                }else{
                    Snackbar.make(v, "Please select a location", BaseTransientBottomBar.LENGTH_SHORT).show();

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
                try {
                    if (image1.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                        Uri resultUri = result.getUri();
                        url1 = new File(resultUri.getPath());
                        Picasso.with(getContext()).load(url1).into(image1);
                    } else if (image2.getDrawable().getConstantState().equals(defaultImage.getConstantState())) {
                        Uri resultUri = result.getUri();
                        url2 = new File(resultUri.getPath());
                        Picasso.with(getContext()).load(url2).into(image2);
                    } else {
                        Uri resultUri = result.getUri();
                        url3 = new File(resultUri.getPath());
                        Picasso.with(getContext()).load(url3).into(image3);

                    }
                }catch (Exception e){

                }
            }
        }
    }
    private void recortarImagen(Uri imagenUri){
        CropImage.activity(imagenUri).setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(1280, 720)
                .setAspectRatio(16,9).start(getContext(), PostFragment.this);
    }

    private InputStream comprimirImagen(Bitmap thumb_bitmap,File file){
        try {
            thumb_bitmap = new Compressor(getContext())
                    .setMaxHeight(720)
                    .setMaxWidth(1280)
                    .setQuality(90)
                    .compressToBitmap(file);
        }catch (IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        thumb_byte = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(thumb_byte);

    }
    private void checkReadStorage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
    }
//
}