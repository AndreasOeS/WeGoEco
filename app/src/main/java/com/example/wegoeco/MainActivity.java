package com.example.wegoeco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, World!");
//        Intent intent = new Intent(this, BluetoothActivity.class);
//        startActivity(intent);

        //Trip trip = new Trip();

//        trip.setStartTime((int) System.currentTimeMillis()/1000);
//        trip.setEndTime((int) System.currentTimeMillis()/1000);
//        trip.setStartODO(1);
//        trip.setEndODO(2);
//        trip.setStartSOC(900);
//        trip.setEndSOC(800);
//        trip.calKmperkw();



        //Trip uploadTrip = new Trip(trip.getStartTime(), trip.getEndTime(), trip.getKmperkw(), trip.getKmDif(), trip.getSOCDif());
        Trip uploadTrip = new Trip(1,2,3,4,5);


        Firebase firebase = new Firebase();
        //firebase.upload(uploadTrip);


    }
}