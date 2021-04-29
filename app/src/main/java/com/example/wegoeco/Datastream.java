package com.example.wegoeco;


import android.bluetooth.BluetoothSocket;
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


    ArrayList<String> ACSII = new ArrayList<>();


    public Datastream(BluetoothSocket socket) {
        this.socket = socket;

    }

    public void run() {

        isStartData = true;
        switchID = false;
        isReading = true;


        Trip trip = new Trip();
        try {
            setUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }

        Firebase firebase = new Firebase();
        firebase.upload(trip);
    }

    public void dataRead(int readBytes, Trip trip)  {

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
                    System.out.println("lol");
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
                }
                else{
                    trip.setEndODO(Integer.parseInt(decital));
                    System.out.println("lul");
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
            System.out.println("Hex: " + hex);
            returnInt = Integer.decode("0x" + hex);
        } catch (Exception e){
            e.printStackTrace();
        }
        return returnInt;
    }


    public void setUp() throws IOException {
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