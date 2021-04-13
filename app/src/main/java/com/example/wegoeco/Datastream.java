package com.example.wegoeco;

import android.bluetooth.BluetoothSocket;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.io.IOException;
import java.util.ArrayList;

public class Datastream extends Thread{

    private BluetoothSocket socket;
    private ArrayList<Integer> inputs = new ArrayList<>();
    private String[] commands = new String[]{"atsp6", "ate0", "ath1", "atcaf0", "atS0"};
    private String atStop = "z";
    private String input1 = "";
    private boolean isReading;
    private String PID = "2D5";
    ArrayList<String> ACSII = new ArrayList<>();
    ArrayList<String> allFrames = new ArrayList<>();



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
        //String data = "";
        isReading = true;
        boolean switchID = false;

        while (isReading){

            String data = "";
            try {
                int readBytes = socket.getInputStream().read(buffer);
                System.out.println("Byte= " + readBytes);
                ACSII = ACSIITranslate(buffer);
                for (int i = 0; i < ACSII.size(); i++){
                    //System.out.print("Buffer:" + buffer[i] + " ");
                    data = data + ACSII.get(i) + " ";
                }
                String deciData = "";
                if (readBytes == 20 && PID.equals("2D5")){
                    deciData = soc(ACSII);
                    if (switchID){
                        System.out.println("Soc Data: " + deciData);
                        sendCommand(atStop);
                        PID = "412";
                        sendCommand("atcra " + PID);
                        sendCommand("atma");
                        switchID = false;
                    }
                }
                if(readBytes == 20 && PID.equals("412")){
                    if (switchID){
                        deciData = odo(ACSII);
                        System.out.println("Odo Data: " + deciData);
                        sendCommand(atStop);
                        PID = "2D5";
                        sendCommand("atcra " + PID);
                        sendCommand("atma");
                        switchID = false;
                    }
                }

                allFrames.add(data);

                Firebase firebase = new Firebase();

                int decital = hexToDeci(deciData);
                if (decital != -1){
                    switchID = true;
                    System.out.println("\n" + "Data: " + data);
                    firebase.upload(decital + "");
                    System.out.println("Decital: " + decital);
                }
                //clearInput();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        //hex = hex.replaceAll(" ", "");

        //returnInt = Integer.parseInt(hex);
        // Eller


        return returnInt;
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
