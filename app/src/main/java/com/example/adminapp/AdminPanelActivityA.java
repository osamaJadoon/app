package com.example.adminapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import es.dmoral.toasty.Toasty;
import maes.tech.intentanim.CustomIntent;

public class AdminPanelActivityA extends AppCompatActivity {

    Button registerDriver;
    Button registerStudent;
    private Button trackBus;
    Animation mShakeAnimation;
    private int LOCATION_PERMISSION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panela);

        //blinkAnimation = AnimationUtils.loadAnimation(this,R.anim.blink);
        Animation blinkAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        Animation AblinkAnimation = AnimationUtils.loadAnimation(this,R.anim.ablink);
        mShakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);



        registerDriver = findViewById(R.id.register_driver_btn_id);
        registerDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPanelActivityA.this, DriverRegisterActivityA.class);
                startActivity(intent);
                CustomIntent.customType(AdminPanelActivityA.this,"left-to-right");
            }
        });
        registerStudent = findViewById(R.id.register_student_btn_id);
        registerStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPanelActivityA.this, StudentRegisterActivityA.class);
                startActivity(intent);
                CustomIntent.customType(AdminPanelActivityA.this,"left-to-right");
            }
        });
        trackBus = findViewById(R.id.track_bus_btn_id);
        trackBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gpsEnabled() || !isOnline())
                {
                    trackBus.startAnimation(mShakeAnimation);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(AdminPanelActivityA.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(AdminPanelActivityA.this, MapsActivityA.class);
                        startActivity(intent);
                        CustomIntent.customType(AdminPanelActivityA.this,"left-to-right");
                    }else {
                        requestLocationPermission();
                    }
                }

            }
        });

        registerDriver.startAnimation(blinkAnimation);
        registerStudent.startAnimation(AblinkAnimation);
        trackBus.startAnimation(blinkAnimation);

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setIcon(R.drawable.warning)
                    .setMessage("This Permission needed Otherwise you will not be able to use this app!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AdminPanelActivityA.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toasty.success(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminPanelActivityA.this, MapsActivityA.class);
                startActivity(intent);
                CustomIntent.customType(AdminPanelActivityA.this,"left-to-right");
            }else {
                Toasty.error(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    //back press function start here

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("EXIT?")
                .setIcon(R.drawable.exit)
                .setMessage("Are u sure you want to exit")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    //back press ends.....
    private boolean gpsEnabled(){
        //***********************GPS start*************
        this.setFinishOnTouchOutside(true);
        // Todo Location Already on  ... start
        final LocationManager manager = (LocationManager) AdminPanelActivityA.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(AdminPanelActivityA.this)) {
            //Toast.makeText(SignInActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            return true;
        }

        if(!hasGPSDevice(AdminPanelActivityA.this)){
            Toasty.info(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(AdminPanelActivityA.this)) {
            Toasty.info(this, "Please Enable your GPS", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            // Toast.makeText(SignInActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            return true;
        }
        //*************GPS ENDS******************
    }

    //*********code for on the gps if its off***************************
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            //Toast.makeText(this, "Yor are online", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toasty.warning(this, "You are Offline", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
