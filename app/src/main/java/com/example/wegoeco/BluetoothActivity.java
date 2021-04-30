package com.example.wegoeco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity {

    public BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public TextView textView;
    public Button startKnap;
    public Button endKnap;
    public ArrayList<String> deviseList = new ArrayList<>();
    public BluetoothDevice device;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Datastream thread;
    public Datastream thread2;
    private Activity activity;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        textView = findViewById(R.id.text);
        startKnap = findViewById(R.id.btn_start);
        endKnap = findViewById(R.id.btn_end);


        startKnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect();
                startDatastream(true);
            }
        });

        endKnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatastream(false);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
//        connect();
//        startDatastream();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void connect(){
        String addressCANBUS1 = "00:04:3E:9E:66:35";
        String addressCANBUS2 = "00:04:3E:31:5B:53";
        device = bluetooth.getRemoteDevice(addressCANBUS1);
        device.createBond();
    }


    public void startDatastream(boolean isStartData){
        BluetoothSocket socket = null;
        try {
            if (isStartData){
                socket = connect(device);

            }
            thread = new Datastream(socket, activity);
            thread.start();
            textView.setText("Listening for data");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public static BluetoothSocket connect(BluetoothDevice dev) throws IOException {

        BluetoothSocket sock = null;
        BluetoothSocket sockFallback = null;

        System.out.println("Starting Bluetooth connection..");
        try {
            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
        } catch (Exception e1) {
            System.out.println("There was an error while establishing Bluetooth connection. Falling back..");
            Class<?> clazz = sock.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                sockFallback.connect();
                sock = sockFallback;
            } catch (Exception e2) {
                System.out.println("Couldn't fallback while establishing Bluetooth connection.");
                throw new IOException(e2.getMessage());
            }
        }
        return sock;
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
}