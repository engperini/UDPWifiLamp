package com.perinidev.lamp_test;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class config extends AppCompatActivity {
    private TextView lampStatusTextView;
    TextView StatusTextWifi;
    TextView StatusTextLamp;
    TextView IdLamp, VersionLamp;
    EditText lampNameText, lampPassText;
    EditText TextAPSSID, TextAPKEY;

    private Button saveAPButton;
    private MulticastSocket socket; // Use MulticastSocket for multicast communication
    private DatagramSocket socket_receive;
    private static final int RECEIVE_TIMEOUT = 500; // Timeout in milliseconds
    private InetAddress serverAddress, group;
    private int serverPort, multicastPort, destinationPort, sourcePort;
    private boolean running = false;
    private UDPThreadConfig udpThreadConfig;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);


        StatusTextLamp = findViewById(R.id.statustextlamp);

        IdLamp = findViewById(R.id.idlamp);
        VersionLamp= findViewById(R.id.versionlamp);
        StatusTextWifi = findViewById(R.id.statustextwifi);

        TextAPSSID = findViewById(R.id.textapssid);
        TextAPKEY = findViewById(R.id.textapkey);

        lampNameText = findViewById(R.id.lampnametext);
        lampPassText = findViewById(R.id.lamppasstext);

        saveAPButton = findViewById(R.id.saveapbutton);

        saveAPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ssid = TextAPSSID.getText().toString();
                String key = TextAPKEY.getText().toString();
                String lampssid = lampNameText.getText().toString();
                String lampkey = lampPassText.getText().toString();

                udpThreadConfig.APconfig(ssid,key,lampssid,lampkey);
                Log.d("UI_APconfig", "Sucesso" );
                Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();

            }
        });

        // Start the UDP thread
        udpThreadConfig = new UDPThreadConfig(this);

        //udpThreadConfig = new UDPThreadConfig();
        udpThreadConfig.start();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (udpThreadConfig != null) {
            udpThreadConfig.stopRunning();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        //udpThreadConfig.start();

    }


}
