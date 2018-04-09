/*
 * ProviderShowTasks5kmOnMap
 *
 *
 * April 9, 2018
 *
 * Copyright (c) 2018 Team 12. CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */
package com.example.dada.View;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.dada.Controller.TaskController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.Task.Task;
import com.example.dada.R;
import com.example.dada.Util.GPSTracker;
import com.example.dada.Util.TaskUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * show request with 5 km
 */
public class ProviderShowTasks5kmOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<Task> tasks = new ArrayList<>();
    private Double latitude;
    private Double longitude;
    private static final Double DEFAULT_LAT = 53.5273;
    private static final Double DEFAULT_LON = -113.5296;
    private HashMap<Marker, Task> mHashMap = new HashMap<Marker, Task>();

    private TaskController mapTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            LatLng currLoc = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 12));


            tasks = (ArrayList<Task>) o;

            for ( Task task : tasks){
                String status = task.getStatus();
                String title = task.getTitle();
                if (status.equals("requested") || status.equals("bidded")) {
                    LatLng position = new LatLng(task.getCoordinates().get(1), task.getCoordinates().get(0));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(title));

                    mHashMap.put(marker, task);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_show_tasks5km_on_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get map permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        GPSTracker gps = new GPSTracker(ProviderShowTasks5kmOnMap.this);
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude();

        } else {
            latitude = DEFAULT_LAT;
            longitude = DEFAULT_LON;
        }

        List<Double> co = new ArrayList<>();
        co.add(longitude);
        co.add(latitude);
        mapTaskController.searchTaskByGeoLocation(co);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Task clickedTask = mHashMap.get(marker);
            Intent intent = new Intent(getApplicationContext(), providerDetailActivity.class);
            intent.putExtra("Task", TaskUtil.serializer(clickedTask));
            startActivity(intent);
        }
    });
    }}




