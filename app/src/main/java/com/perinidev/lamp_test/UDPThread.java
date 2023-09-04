package com.perinidev.lamp_test;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
public class UDPThread implements Runnable {
    private static final String TAG = "UDPThread";
    private static final int RECEIVE_TIMEOUT = 500; // Timeout in milliseconds
    private MulticastSocket socket; // Use MulticastSocket for multicast communication
    private DatagramSocket socket_receive;
    private boolean running;
    private Handler handler;
    private InetAddress group, IPrec;
    private int multicastPort;
    private int sourcePort;
    boolean online = false;

    public UDPThread(String multicastGroupIP, int multicastPort, int sourcePort) {
        try {
            group = InetAddress.getByName(multicastGroupIP);
            this.multicastPort = multicastPort;
            this.sourcePort = sourcePort;
            socket = new MulticastSocket(sourcePort);
            //socket.joinGroup(group); // Join the multicast group
            socket.setSoTimeout(RECEIVE_TIMEOUT);
            running = true;

        } catch (IOException e) {
            e.printStackTrace();
            running = false;
        }
    }
    private static final int MAX_RECEIVE_ATTEMPTS = 5;
    private int receiveAttempts = 0;
    @Override
    public void run() {
        byte[] rbuf = new byte[100];
        DatagramPacket rPacket = new DatagramPacket(rbuf, 100, group, 30978);
        Log.d(TAG, "packet gerado: " + Arrays.toString(rPacket.getData()));

        // Envia uma solicitação de status ao iniciar o loop
        requestStatus();

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
                            String hexId = String.format("%06X", id); // Formata como um valor hexadecimal com 6 dígitos

                            byte[] ssidBytes = new byte[32];
                            System.arraycopy(rbuf, 6, ssidBytes, 0, 32);
                            int ssidLength = 0;
                            while (ssidLength < 32 && ssidBytes[ssidLength] != 0) {
                                ssidLength++;
                            }
                            String ssid = new String(ssidBytes, 0, ssidLength);

                            byte[] apPasswordBytes = new byte[32];
                            System.arraycopy(rbuf, 38, apPasswordBytes, 0, 32);
                            int apPasswordLength = 0;
                            while (apPasswordLength < 32 && apPasswordBytes[apPasswordLength] != 0) {
                                apPasswordLength++;
                            }
                            String apPassword = new String(apPasswordBytes, 0, apPasswordLength);

                            byte version = rbuf[72];

                            online = true;

                            Log.d(TAG, "Received packet from lamp. ID: " + hexId + ", Version: " + version + ", SSID: " + ssid + ", AP: " + apPassword );

                            //update UI, send messages, etc.
                            //break;

                        }
                    }
                } catch (IOException e) {
                    // Socket timeout or other IO exception occurred.
                    // This is normal behavior when no response is received within the timeout.
                    receiveAttempts++;
                    if ((receiveAttempts >= MAX_RECEIVE_ATTEMPTS) & !online) {
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

    public void requestStatus() {
        //request: fbe94169e10000
        byte[] request3 = { (byte) 0xFB, (byte) 0xE9, 0x41, 0x69, (byte) 0xE1, 0x00, 0x00 };
        sendMessage(request3, "255.255.255.255", 30977);
    }



    public void stop() {
        running = false;
        try {
            socket.leaveGroup(group); // Leave the multicast group before closing
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    // Método para enviar uma mensagem UDP para a lâmpada
    public void sendMessage(byte[] message, String destinationIP, int destinationPort) {
        try {
            InetAddress destinationGroup = InetAddress.getByName(destinationIP);
            DatagramPacket packet = new DatagramPacket(message, message.length, destinationGroup, destinationPort);
            socket.send(packet);
            Log.d(TAG, "Sent message: " + Arrays.toString(message));

            byte[] messagereq = { (byte) 0xFB, (byte) 0xE9, 0x41, 0x69, (byte) 0xE1, 0x00, 0x00 };
            DatagramPacket packetreq = new DatagramPacket(messagereq, messagereq.length, destinationGroup, destinationPort);
            socket.send(packetreq);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
