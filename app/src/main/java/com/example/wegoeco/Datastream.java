package com.example.wegoeco;

import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.util.ArrayList;

public class Datastream extends Thread{

    private BluetoothSocket socket;
    private ArrayList<Integer> inputs = new ArrayList<>();
    private String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0"};
    private String input1 = "";
    private boolean isReading;
    private String PID = "208";



    public Datastream(BluetoothSocket socket){
        this.socket = socket;

    }


    public void run(){
        setUp();
        try {
            sendCommand("atcra " + PID);
            sendCommand("atma");
        } catch (IOException e) {
            e.printStackTrace();
        }


        byte[] buffer = new byte[20];
        String data;
        isReading = true;

        while (isReading){

            try {
                int readBytes = socket.getInputStream().read(buffer);
                System.out.println("Byte= " + readBytes);
                for (int i = 0;i < buffer.length;i++){
                    System.out.print(buffer[i] + " ");

                }
                //clearInput();



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }








    public void setUp(){
        try {
            for (int i = 0;i < commands.length;i++){
                socket.getOutputStream().write((commands[i] + "\r").getBytes());
                socket.getOutputStream().flush();
                //inputs.add(socket.getInputStream().read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendCommand(String command) throws IOException {
        socket.getOutputStream().write((command + "\r").getBytes());
        socket.getOutputStream().flush();
        clearInput();
    }



    public void clearInput(){
        byte[] buffer = new byte[8192];
        try {
            while (socket.getInputStream().available() > 0){
                int bytesRead = socket.getInputStream().read(buffer);
                buffer = new byte[8192];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
