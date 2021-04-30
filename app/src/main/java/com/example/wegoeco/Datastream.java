package com.example.wegoeco;


import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Datastream extends Thread {


    private BluetoothSocket socket;
    private String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0"};
    private String atStop = "z";
    private boolean isReading;
    private String PID = "418";
    private String decital;
    boolean switchID;
    boolean isStartData;
    private Activity activity;


    ArrayList<String> ACSII = new ArrayList<>();


    public Datastream(BluetoothSocket socket, Activity activity) {
        this.socket = socket;
        this.activity = activity;

    }

    public void run() {

        Trip trip = new Trip();
        try {

            setUp();
        } catch (IOException e) {
            e.printStackTrace();
        }

        isStartData = true;
        switchID = false;
        isReading = true;

        byte[] buffer = new byte[20];
        String data = "";



        while (isReading) {
            System.out.println("Listening for data");

            data = "";
            try {

                int readBytes = socket.getInputStream().read(buffer);
                System.out.println("Byte= " + readBytes);
                ACSII = ACSIITranslate(buffer);

                for (int i = 0; i < ACSII.size(); i++) {
                    data = data + ACSII.get(i) + " ";
                }

                dataRead(readBytes, trip);
                System.out.println("\n" + "Data: " + data);
                System.out.println("Decital: " + decital);

            } catch (IOException e) {
                e.printStackTrace();
            }

            clearInput();
        }

        Firebase firebase = new Firebase();
        firebase.upload(trip);


    }

    public void dataRead(int readBytes, Trip trip) throws IOException {

        if(readBytes == 20 && PID.equals("418")){
            decital = gear(ACSII);
            decital = hexToDeci(decital) + "";
            if (decital.equals("82") || decital.equals("9") && isStartData){
                System.out.println("Gear Data: " + decital);
                PID = "2D5";
                try {
                    stopAndStartNew();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(decital.equals("80") && !isStartData) {
                isReading = false;
                sendCommand(atStop);
            }
        }

        if (readBytes == 20 && PID.equals("2D5")){
            decital = soc(ACSII);
            decital = hexToDeci(decital) + "";
            if (Integer.parseInt(decital) > 0 && Integer.parseInt(decital) <= 1000){
                System.out.println("Soc Data: " + decital);
                PID = "412";
                if (isStartData){
                    trip.setStartSOC(Integer.parseInt(decital));
                    trip.setStartTime((int) (System.currentTimeMillis()/1000));

                }
                else{
                    trip.setEndSOC(Integer.parseInt(decital));
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
            decital = odo(ACSII);
            decital = hexToDeci(decital) + "";
            if (Integer.parseInt(decital) >= 0 && Integer.parseInt(decital) < 1000000){
                System.out.println("Odo Data: " + decital);
                PID = "418";
                if (isStartData){
                    trip.setStartODO(Integer.parseInt(decital));
                    isStartData = false;
                }
                else{
                    trip.setEndODO(Integer.parseInt(decital));
                }

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

    public void toast(String message){
        //Toast.makeText(activity.getParent(), me, Toast.LENGTH_SHORT).show();
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
            System.out.println("Hex: " + hex);
            returnInt = Integer.decode("0x" + hex);
        } catch (Exception e){
            e.printStackTrace();
        }


        return returnInt;
    }

    boolean test2;
    boolean test3;
    ArrayList<String> listOfHex;

    public boolean tester(String hex){
        test2 = true;
        listOfHex = new ArrayList<>();
        listOfHex.add("0");
        listOfHex.add("1");
        listOfHex.add("2");
        listOfHex.add("3");
        listOfHex.add("4");
        listOfHex.add("5");
        listOfHex.add("6");
        listOfHex.add("7");
        listOfHex.add("8");
        listOfHex.add("9");
        listOfHex.add("A");
        listOfHex.add("B");
        listOfHex.add("C");
        listOfHex.add("D");
        listOfHex.add("E");
        listOfHex.add("F");
        listOfHex.add(" ");

        for (int i = 0; i < hex.length(); i++){
            test3 = false;
            System.out.println("hex is this value: " + hex.indexOf(i));
            for (String h : listOfHex) {
                System.out.println("This is what i ompare to " + h);
                if (h.equals(hex.indexOf(i)))
                    test3 = true;
            }
            if (!test3)
                test2 = false;
        }

        return test2;
    }


    public void setUp() throws IOException {
        //sendCommand("ATZ");
        sendCommand(atStop);

        for (int i = 0;i < commands.length;i++){
            sendCommand(commands[i]);
        }
        sendCommand("atcra " + PID);
        sendCommand("atma");
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