package com.perinidev.lamp_test;
import android.util.Log;
import android.widget.Button;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class UDPThreadConfig extends Thread {

    private static final String TAG = "UDPThreadConfig";

    String lampIDString, hexId;

    private Button saveLampButton, saveAPButton;
    private MulticastSocket socket; // Use MulticastSocket for multicast communication
    private DatagramSocket socket_receive;
    private static final int RECEIVE_TIMEOUT = 500; // Timeout in milliseconds
    private static final int MAX_RECEIVE_ATTEMPTS = 5;
    private int receiveAttempts = 0;
    private InetAddress serverAddress, group;
    private int serverPort, multicastPort, destinationPort, sourcePort;
    private boolean running = false;
    private boolean online = false;

    private config activity;
    public UDPThreadConfig(config activity) {
        this.activity = activity;
    }


    public void run() {

        try {
            serverAddress = InetAddress.getByName("255.255.255.255");
            multicastPort = 30798;
            serverPort = 30978;
            sourcePort = 30978;
            destinationPort = 30977;

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try {
            group = serverAddress;
            socket = new MulticastSocket(sourcePort);
            //socket.joinGroup(group); // Join the multicast group
            socket.setSoTimeout(RECEIVE_TIMEOUT);
            running = true;

        } catch (IOException e) {
            e.printStackTrace();
            running = false;
        }

        //cria pacote para recepção do status da lampada.
        byte[] rbuf = new byte[100];
        DatagramPacket rPacket = new DatagramPacket(rbuf, 100, group, 30978);

        //faz requisição unica enviando pergunta para lampada
        requestStatus();



        // faz a recepção do pacote

        try {
            while (running) {
                try {
                    socket.receive(rPacket);
                    Log.d(TAG, "Received packet content: " + Arrays.toString(rPacket.getData()));

                    if (rPacket.getLength() >= 70) {
                        rbuf = rPacket.getData();  // Get the byte array from the received packet

                        if (rbuf[0] == -6 && rbuf[1] == -22) {
                            byte[] idBytes = new byte[4];
                            System.arraycopy(rbuf, 2, idBytes, 0, 4);
                            int id = (idBytes[0] & 0xFF) | ((idBytes[1] & 0xFF) << 8) | ((idBytes[2] & 0xFF) << 16) | ((idBytes[3] & 0xFF) << 24);
                            hexId = String.format("%06X", id); // Formata como um valor hexadecimal com 6 dígitos

                            byte[] ssidBytes = new byte[32];
                            System.arraycopy(rbuf, 6, ssidBytes, 0, 32);
                            int ssidLength = 0;
                            while (ssidLength < 32 && ssidBytes[ssidLength] != 0) {
                                ssidLength++;
                            }
                            String ssid = new String(ssidBytes, 0, ssidLength);

                            byte[] apBytes = new byte[32];
                            System.arraycopy(rbuf, 38, apBytes, 0, 32);
                            int apLength = 0;
                            while (apLength < 32 && apBytes[apLength] != 0) {
                                apLength++;
                            }
                            String ap = new String(apBytes, 0, apLength);

                            byte version = rbuf[72];

                            online = true;
                            Log.d(TAG, "Received packet from lamp. ID: " + hexId + ", Version: " + version + ", SSID: " + ssid + ", AP: " + ap );

                            //update UI, send messages, etc.
                            //break;

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //information from lamp
                                    activity.StatusTextLamp.setText(ssid);
                                    activity.StatusTextWifi.setText(ap);
                                    activity.IdLamp.setText(String.valueOf(hexId));
                                    activity.VersionLamp.setText(String.valueOf(version));

                                    //hint text to ssid lamp and ap
                                    activity.lampNameText.setText(ssid);
                                    activity.TextAPSSID.setText(ap);



                                }
                            });


                        }
                    }
                } catch (IOException e) {
                    // Socket timeout or other IO exception occurred.
                    // This is normal behavior when no response is received within the timeout.
                    receiveAttempts++;
                    if ((receiveAttempts >= MAX_RECEIVE_ATTEMPTS ) & !online ) {
                        // Maximum attempts reached
                        Log.d(TAG, "Time out" );
                        //break;
                    }
                }
            }
        } catch (Exception e) {
            // Handle any other unexpected exceptions here
            Log.e(TAG, "Unexpected exception: " + e.getMessage());
        }

    }


    public void sendMessage(byte[] message, String destinationIP, int destinationPort) {
        try {
            InetAddress destinationGroup = InetAddress.getByName(destinationIP);
            DatagramPacket packet = new DatagramPacket(message, message.length, destinationGroup, destinationPort);
            socket.send(packet);
            Log.d(TAG, "Sent message by UDPThreadConfig : " + Arrays.toString(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestStatus() {
        //request: fbe94169e10000
        byte[] request3 = { (byte) 0xFB, (byte) 0xE9, 0x41, 0x69, (byte) 0xE1, 0x00, 0x00 };
        sendMessage(request3, "255.255.255.255", 30977);
    }

    public void stopRunning() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public void APconfig(String newSSIDAP, String newPasswordAP,String newSSIDLamp, String newPasswordLamp ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //String newSSIDAP = "ARTHUR WIFI";
                //String newPasswordAP = "art123@#";
                String lampIDString = String.valueOf(hexId);
                //String lampIDString = "CFD4E9"; // lamp's ID in string format
                //String newSSIDLamp = "Sophia";
                //String newPasswordLamp = ""; // o password da lampada deve ser vazio, no passoword to connect

                int lampID = Integer.parseInt(lampIDString, 16); // Convert to numeric ID

                byte[] buff = new byte[134];
                buff[0] = -5;
                buff[1] = -31;

                // Copy the new SSID and password for the lamp
                byte[] ssidBytesLamp = newSSIDLamp.getBytes();
                byte[] passwordBytesLamp = newPasswordLamp.getBytes();
                System.arraycopy(ssidBytesLamp, 0, buff, 2, Math.min(ssidBytesLamp.length, 32));
                System.arraycopy(passwordBytesLamp, 0, buff, 34, Math.min(passwordBytesLamp.length, 32));

                // Copy the new SSID and password for the AP
                byte[] ssidBytesAP = newSSIDAP.getBytes();
                byte[] passwordBytesAP = newPasswordAP.getBytes();
                System.arraycopy(ssidBytesAP, 0, buff, 66, Math.min(ssidBytesAP.length, 32));
                System.arraycopy(passwordBytesAP, 0, buff, 98, Math.min(passwordBytesAP.length, 32));


                // Set lamp ID
                buff[130] = (byte) (lampID & 0xFF);
                buff[131] = (byte) ((lampID >> 8) & 0xFF);
                buff[132] = (byte) ((lampID >> 16) & 0xFF);
                buff[133] = (byte) ((lampID >> 24) & 0xFF);

                // send the configPayload to the lamp
                sendMessage(buff, "255.255.255.255", 30977);
            }
        }).start();
    }








}
