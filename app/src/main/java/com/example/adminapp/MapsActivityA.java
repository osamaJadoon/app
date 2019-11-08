package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class MapsActivityA extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;

    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;
    Marker currentMarker;

    LatLng driverLocation123;
    LatLng driverLocation789;

    LatLng currentLocation;

    double driverLatitude;
    double driverLongitude;

    double currentLatitude;
    double currentLongitude;

    DatabaseReference adminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        mMap.setMyLocationEnabled(true);

        double latitude = 34.2010552;
        double longitude = 73.1622682;
        LatLng latLng  = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.register_students:
                Intent intent = new Intent(MapsActivityA.this, StudentRegisterActivityA.class);
                startActivity(intent);
                return true;
            case R.id.register_drivers:
                Intent intent1 = new Intent(MapsActivityA.this, DriverRegisterActivityA.class);
                startActivity(intent1);
                return true;

            case R.id.track_bus:
                Toast.makeText(this, "You are already tracking bus", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.bus_info:
                Intent intent2 = new Intent(MapsActivityA.this,BusInformationActivity.class);
                startActivity(intent2);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //back press function start here

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivityA.this,AdminPanelActivityA.class);
        startActivity(intent);
        CustomIntent.customType(MapsActivityA.this,"right-to-left");
//        new AlertDialog.Builder(this)
//                .setTitle("EXIT?")
//                .setIcon(R.drawable.exit)
//                .setMessage("Are u sure you want to exit")
//                .setCancelable(false)
//                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finishAffinity();
//                    }
//                })
//                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .create().show();
    }

    //back press ends.....

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        currentLocation = new LatLng(currentLatitude,currentLongitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        mMap.clear();
        markerOptions.title("You");
        markerOptions.snippet("Admin Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentMarker = mMap.addMarker(markerOptions);

       // retrieveDriverLocation();
        retrieve123DriverLocation();
        retrieve789DriverLocation();

    }
    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    public void DriverLocationBtn(View view) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLocation789));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        mMap.animateCamera(CameraUpdateFactory.scrollBy(10,0));
    }

    private void retrieve123DriverLocation(){
        adminRef = FirebaseDatabase.getInstance().getReference().child("123");
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                driverLatitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("latitude").getValue()).toString().trim());

                driverLongitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("longitude").getValue()).toString().trim());


                driverLocation123 = new LatLng(driverLatitude,driverLongitude);
                if (driverLatitude==0 | driverLongitude==0)
                {
                    Toasty.info(MapsActivityA.this, "This bus doesn't operate now", Toast.LENGTH_SHORT).show();
                }
                else {
                    // mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(driverLocation123);

                    markerOptions.title("Bus Number: 123 ");

                     markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                   // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.driver));


                    float[] results = new float[10];
                    Location.distanceBetween(currentLatitude,currentLongitude,driverLatitude,driverLongitude,results);
                    if (results[0]>=1000.0)
                    {
                        double km = results[0]*0.001;
                        markerOptions.snippet("Distance= "+km + " Km");

                    }else
                    {
                        markerOptions.snippet("Distance= "+results[0] + " m");
                    }

                    mMap.addMarker(markerOptions).setFlat(true);

                    mMap.addPolyline(new PolylineOptions()
                            .add(currentLocation)
                            .add(driverLocation123)
                            .width(8f)
                            .color(Color.RED)
                            .clickable(true)
                    );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivityA.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void retrieve789DriverLocation(){
        adminRef = FirebaseDatabase.getInstance().getReference().child("789");
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                driverLatitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("latitude").getValue()).toString().trim());

                driverLongitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("longitude").getValue()).toString().trim());


                driverLocation789 = new LatLng(driverLatitude,driverLongitude);
                if (driverLatitude==0 | driverLongitude==0)
                {
                    Toasty.info(MapsActivityA.this, "This bus doesn't operate now", Toast.LENGTH_SHORT).show();
                }
                else {
                    // mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(driverLocation789);

                    markerOptions.title("Bus Number: 789 ");

                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    // markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.driver));


                    float[] results = new float[10];
                    Location.distanceBetween(currentLatitude,currentLongitude,driverLatitude,driverLongitude,results);
                    if (results[0]>=1000.0)
                    {
                        double km = results[0]*0.001;
                        markerOptions.snippet("Distance= "+km + " Km");

                    }else
                    {
                        markerOptions.snippet("Distance= "+results[0] + " m");

                    }

                    mMap.addMarker(markerOptions).setFlat(true);

                    mMap.addPolyline(new PolylineOptions()
                            .add(currentLocation)
                            .add(driverLocation789)
                            .width(8f)
                            .color(Color.RED)
                            .clickable(true)
                    );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivityA.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void BusNumber123Location(View view) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLocation123));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        mMap.animateCamera(CameraUpdateFactory.scrollBy(10,0));
    }
}
