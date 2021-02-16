package com.example.wegoeco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import com.google.android.gms.common.util.JsonUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    //public static final String BLUETOOTH;
    //public static final String ACCESS_FINE_LOCATION;
    public static final int REQUEST_ENABLE_BT = 100;
    public static final int PERMISSION_CODE = 200;
    public  BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBlue();
        discover();


    }


    public void discover(){

        bluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        System.out.println("Test11________________________");
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        //System.out.println("Test12_____________");
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("Test10______________________________");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                System.out.println(deviceName + "________________________________________");
                String deviceHardwareAddress = device.getAddress(); // MAC address
                System.out.println(deviceHardwareAddress + "________________________");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }






    public void connectBlue(){

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            System.out.println("Device doesn't support Bluetooth");
        }
        else{
            System.out.println("Test1______________________________--");
            if(!bluetoothAdapter.isEnabled()){
                System.out.println("Test2____________________________________");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
        }


        System.out.println("Test3______________");
        if (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_CODE);
            System.out.println("Test4______________________");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        }
    }

    public void startActivityForResult (Intent intent, int requestCode){
        System.out.println("Test5_____________");
        if (requestCode == PERMISSION_CODE){
            System.out.println("Test6____________________");
        }

    }


    public void onActivityResult (int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Test7___________________");
        if (requestCode == REQUEST_ENABLE_BT){

            if(resultCode == RESULT_OK){
                System.out.println("Test8_____________");

            }
            if (resultCode == RESULT_CANCELED){
                System.out.println("Test9___________________");
            }
        }


    }


    public void writeToDatabase(){
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }


}