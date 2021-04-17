package com.example.wegoeco;

import android.bluetooth.BluetoothSocket;
import android.view.animation.AccelerateDecelerateInterpolator;
import java.io.IOException;
import java.util.ArrayList;

public class Datastream extends Thread {


    private BluetoothSocket socket;
    private ArrayList<Integer> inputs = new ArrayList<>();
    private String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0"};
    private String atStop = "z";
    private String input1 = "";
    private boolean isReading;
    private String PID = "418";
    boolean switchID = false;
    boolean isStartData;
    ArrayList<String> ACSII = new ArrayList<>();
    ArrayList<String> allFrames = new ArrayList<>();

    public Datastream(BluetoothSocket socket) {
        this.socket = socket;

    }

    public void run() {
        Trip trip = new Trip();
        setUp();
        byte[] buffer = new byte[20];
        isReading = true;
        isStartData = true;


        while (isReading) {

            String data = "";
            try {
                int readBytes = socket.getInputStream().read(buffer);
                System.out.println("Byte= " + readBytes);
                ACSII = ACSIITranslate(buffer);
                for (int i = 0; i < ACSII.size(); i++) {

                    data = data + ACSII.get(i) + " ";
                }
                String deciData = "";

                dataRead(readBytes, deciData, trip);


                allFrames.add(data);

                Firebase firebase = new Firebase();

                int decital = hexToDeci(deciData);
                if (decital != -1){
                    System.out.println("\n" + "Data: " + data);
                    firebase.upload(trip);
                    System.out.println("Decital: " + decital);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void dataRead(int readBytes, String deciData, Trip trip)  {

        if(readBytes == 20 && PID.equals("418")){
            deciData = gear(ACSII);
            if (deciData.equals("82") || deciData.equals("9")){
                System.out.println("Gear Data: " + deciData);
                PID = "2D5";
                try {
                    stopAndStartNew();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(deciData.equals("78")) {
                isReading = false;
            }
        }

        if (readBytes == 20 && PID.equals("2D5")){
            deciData = soc(ACSII);
            if (Integer.parseInt(deciData) > 0 && Integer.parseInt(deciData) <= 1000){
                System.out.println("Soc Data: " + deciData);
                PID = "412";
                if (isStartData){
                    trip.setStartSOC(Integer.parseInt(deciData));
                    trip.setStartTime((int) (System.currentTimeMillis()/1000));

                }
                else{
                    trip.setEndSOC(Integer.parseInt(deciData));
                    trip.setEndTime((int) (System.currentTimeMillis()/1000));
                }
                try {
                    stopAndStartNew();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(readBytes == 20 && PID.equals("412")){
            deciData = odo(ACSII);
            if (Integer.parseInt(deciData) >= 0){
                System.out.println("Odo Data: " + deciData);
                PID = "418";
                if (isStartData){
                    trip.setStartODO(Integer.parseInt(deciData));
                }
                else{
                    trip.setEndODO(Integer.parseInt(deciData));
                }
                isStartData = false;
                try {
                    stopAndStartNew();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopAndStartNew() throws IOException {
        sendCommand(atStop);
        sendCommand("atcra " + PID);
        sendCommand("atma");
        switchID = false;
    }



    //PID 2D5 State of Charge
    public String soc(ArrayList<String> buffer){
        String returnString = "";
        returnString = returnString + buffer.get(11);
        returnString = returnString + buffer.get(12);
        returnString = returnString + buffer.get(13);
        returnString = returnString + buffer.get(14);
        return returnString;
    }

    //PID 412 Odometer
    public String odo(ArrayList<String> buffer){
        String returnString = "";
        returnString = returnString + buffer.get(7);
        returnString = returnString + buffer.get(8);
        returnString = returnString + buffer.get(9);
        returnString = returnString + buffer.get(10);
        returnString = returnString + buffer.get(11);
        returnString = returnString + buffer.get(12);
        return returnString;
    }

    //PID 418 Gear
    public String gear(ArrayList<String> buffer){
        String returnString = "";
        returnString = returnString + buffer.get(3);
        returnString = returnString + buffer.get(4);
        return returnString;
    }

    public ArrayList<String> ACSIITranslate(byte[] buffer){

        String translatedNumber;
        ArrayList<String> translatedCommand = new ArrayList<>();

        for(int i: buffer){
            translatedNumber = Character.toString((char)i);
            translatedCommand.add(translatedNumber);
        }

        return translatedCommand;
    }

    public int hexToDeci(String hex){

        int returnInt = -1;
        try {
            if (hex.contains("O") || hex.contains("K") || hex.equals("") || hex.contains(">") || hex.contains("<")){
                System.out.println("Fejl i hex input");
            }
            else {
                System.out.println("Hex: " + hex);
                returnInt = Integer.decode("0x" + hex);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return returnInt;
    }


    public void setUp(){
        try {
            for (int i = 0;i < commands.length;i++){
                sendCommand(commands[i]);
            }

            sendCommand("atcra " + PID);
            sendCommand("atma");

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