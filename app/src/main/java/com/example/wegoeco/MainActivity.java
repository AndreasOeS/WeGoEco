package com.example.wegoeco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    //public static final String BLUETOOTH;
    //public static final String ACCESS_FINE_LOCATION;
    public static final int REQUEST_ENABLE_BT = 100;
    public static final int PERMISSION_CODE = 200;
    public static final int PERMISSION_REQUEST_CODE = 300;
    public BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public Button btn;
    public TextView textView;
    public ArrayList<String> deviseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = findViewById(R.id.text);
        btn = findViewById(R.id.btn_bluetooth);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setText("Tester 1234");
                if (bluetooth.isDiscovering()){
                    bluetooth.cancelDiscovery();
                    String devises = "";
                    for(int i = 0; i < deviseList.size(); i++){
                        System.out.println(deviseList.get(i));
                        devises = devises + deviseList.get(i) + "\n";
                    }
                    textView.setText(devises);
                }
                else{
                    textView.setText("Discovering...");
                    for(int i = 0; i < deviseList.size(); i++){
                        deviseList.remove(i);
                    }
                    tryTwo();
                }
            }
        });

    }

    public void tryTwo(){

        //BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        if(bluetooth != null)
        {
            // Continue with bluetooth setup.

            String status;
            String state = "" + bluetooth.getState();
            if (bluetooth.isEnabled()) {
                // Enabled. Work with Bluetooth.
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                //status = mydevicename + " : " + mydeviceaddress;
                status = mydevicename + " : " + mydeviceaddress + " : " + state;

                discover();
            }
            else
            {
                // Disabled. Do something else.
                status = "Bluetooth is not Enabled";
            }
            Toast.makeText(this, status, Toast.LENGTH_LONG).show();

        }

    }





    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("Test14_________________________");
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }







    public void discover(){

        bluetooth.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        System.out.println("Test11________________________");
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            System.out.println("Test10______________________________");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                deviseList.add("Name: " + deviceName + " " + "Address: " + deviceHardwareAddress);
                textView.setText("Found " + deviseList.size() + " devises");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }




    public void writeToDatabase(){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }


}