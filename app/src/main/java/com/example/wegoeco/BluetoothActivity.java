package com.example.wegoeco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;


public class BluetoothActivity extends AppCompatActivity {

    public BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public TextView textView;
    public ArrayList<String> deviseList = new ArrayList<>();
    public BluetoothDevice device;
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
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void connect(){
        String addressCANBUS1 = "00:04:3E:9E:66:35";
        String addressCANBUS2 = "00:04:3E:31:5B:53";
        device = bluetooth.getRemoteDevice(addressCANBUS1);
        device.createBond();
    }


    public void startDatastream(){
        BluetoothSocket socket = null;
        try {
            socket = connect(device);
            thread = new Datastream(socket);
            thread.start();

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
}