package com.example.dada.View;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.example.dada.R;
import com.example.dada.Util.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class RequesterAddTaskLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final Double DEFAULT_LAT = 53.5273;
    private static final Double DEFAULT_LON = -113.5296;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_add_task_location);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get map permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        GPSTracker gps = new GPSTracker(RequesterAddTaskLocationActivity.this);
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude(); // returns latitude
            longitude = gps.getLongitude();

        } else {
            latitude = DEFAULT_LAT;
            longitude = DEFAULT_LON;
        }
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

        LatLng currLoc = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLoc, 12));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Toast.makeText(
                        RequesterAddTaskLocationActivity.this,
                        "Selected Lat : " + latLng.latitude + " , "
                                + "Lon : " + latLng.longitude,
                        Toast.LENGTH_LONG).show();

                String coordinates = latLng.latitude + " , " + latLng.longitude;

                Intent i = new Intent(RequesterAddTaskLocationActivity.this, RequesterAddTaskActivity.class);
                i.putExtra("coordinates", coordinates);
                setResult(RequesterAddTaskLocationActivity.RESULT_OK, i);
                finish();
            }
        });
    }
}

