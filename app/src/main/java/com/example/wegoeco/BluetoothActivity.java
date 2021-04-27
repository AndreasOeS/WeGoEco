package com.example.wegoeco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class BluetoothActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 300;
    public BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public Button btn_blue;
    public Button btn_con;
    public Button btn_get;
    public TextView textView;
    public ArrayList<String> deviseList = new ArrayList<>();
    public BluetoothDevice device;
    private OutputStream outputStream;
    private InputStream inStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public Datastream thread;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        textView = findViewById(R.id.text);

        connect();

        startDatastream();



//        btn_blue = findViewById(R.id.btn_bluetooth);
//        btn_con = findViewById(R.id.btn_con);
//        btn_get = findViewById(R.id.btn_get);
//
//        //clickButtons();
//
//
//
//
//        btn_con.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onClick(View view) {
//                String addressLaptop = "00:DB:DF:C4:88:7A";
//                String addressHeadset = "88:D0:39:A4:22:49";
//                String addressCANBUS1 = "00:04:3E:9E:66:35";
//                String addressCANBUS2 = "00:04:3E:31:5B:53";
//                device = bluetooth.getRemoteDevice(addressCANBUS1);
//                device.createBond(); //ER IKKE EN FEJL
//                //int state = device.getBondState();
//                btn_con.setText("Connecting");
//
//            }
//        });
//
//
//
//
//
//
//
//        btn_get.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BluetoothSocket socket = null;
//                try {
//                    socket = connect(device);
//                    thread = new Datastream(socket);
//                    thread.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//
//
//
//
//        btn_blue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (bluetooth.isDiscovering()){
//                    bluetooth.cancelDiscovery();
//                    String devises = "";
//                    for(int i = 0; i < deviseList.size(); i++){
//                        System.out.println(deviseList.get(i));
//                        devises = devises + deviseList.get(i) + "\n";
//                    }
//                    textView.setText(devises);
//                }
//                else{
//                    textView.setText("Discovering...");
//                    for(int i = 0; i < deviseList.size(); i++){
//                        deviseList.remove(i);
//                    }
//                    tryTwo();
//                }
//            }
//        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void connect(){
        String addressLaptop = "00:DB:DF:C4:88:7A";
        String addressHeadset = "88:D0:39:A4:22:49";
        String addressCANBUS1 = "00:04:3E:9E:66:35";
        String addressCANBUS2 = "00:04:3E:31:5B:53";
        device = bluetooth.getRemoteDevice(addressCANBUS1);
        device.createBond(); //ER IKKE EN FEJL
        //int state = device.getBondState();
        //btn_con.setText("Connecting");
    }


    public void startDatastream(){
        BluetoothSocket socket = null;
        try {
            socket = connect(device);
            thread = new Datastream(socket);
            thread.start();
            while(true){
                if (thread.isInterrupted()){
                    //thread.stop();
                    //thread.start();
                    socket.close();
                    runAgain();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runAgain(){
        startDatastream();


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


    public void tryTwo(){

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

    public void clickButtons(){
        btn_con.performClick();
        btn_get.performClick();
    }
}