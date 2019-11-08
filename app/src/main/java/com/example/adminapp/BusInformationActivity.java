package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class BusInformationActivity extends AppCompatActivity {

    TextView busNo,morningTime,eveningTime,route;
    DatabaseReference databaseReference;
    BusHelper busHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_information);

        busNo = findViewById(R.id.busNo);
        morningTime = findViewById(R.id.morningTime);
        eveningTime = findViewById(R.id.eveningTime);
        route = findViewById(R.id.busRoute);

        busHelper = new BusHelper();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addBtn(View view) {
        String busNo1 = busNo.getText().toString().trim();
        String morningTime1 = morningTime.getText().toString().trim();
        String eveningTime1 = eveningTime.getText().toString().trim();
        String route1 = route.getText().toString().trim();

        busHelper.setBUS_NO(busNo1);
        busHelper.setMORNING_TIME(morningTime1);
        busHelper.setEVENING_TIME(eveningTime1);
        busHelper.setROUTE(route1);

        //databaseReference.child("Bus 1").setValue(busHelper);
        databaseReference.push().setValue(busHelper);
        Toasty.success(this,"Success",Toasty.LENGTH_SHORT).show();
    }
}
