package com.perinidev.lamp_test;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private UDPThread udpThread;
    private Handler udpHandler;
    Button startButton, ButtonCor1, ButtonCor2,ButtonCor3,ButtonCor4,ButtonCor5,ButtonCor6,ButtonCor7,ButtonCor8  ;
    CheckBox checkBoxSono, checkBoxNoite;

    ImageButton ConfigButton;
    ImageView imageView;
    TextView LampStatus;
    SeekBar brightnessSeekBar, whitenessSeekBar;
    private int currentBrightness = 0;
    private int currentWhiteness = 0;

    private int limitbright = 255;
    private int limitwhite = 20;
    private int currentColor = 1; //initial color

    private static final int COMMAND_TURN_ON = 1;
    private static final int COMMAND_TURN_OFF = 2;
    private static final int COMMAND_SET_BRIGHTNESS = 3;
    private static final int COMMAND_WHITE = 4;
    private static final int COMMAND_COLOR =5;

    private boolean on = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startUDPThread();

        LampStatus = findViewById(R.id.lampstatus);
        imageView = findViewById(R.id.imageView);
        checkBoxSono = findViewById(R.id.checkboxsono);
        checkBoxNoite = findViewById(R.id.checkboxnoite);

        udpHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    String receivedData = (String) msg.obj;
                    // Process the received data here and update the UI if needed.
                    Log.d("MainActivity", "Received data from UDPThread: " + receivedData);
                }
            }
        };
        startButton = findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int commandType = on ? COMMAND_TURN_OFF : COMMAND_TURN_ON;
                new SendCommandTask().execute(commandType);
            }
        });

        ConfigButton = findViewById(R.id.configButton);
        //fazer botão de configuração.
        ConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, config.class);
                startActivity(intent);
            }
        });


        ButtonCor1 = findViewById(R.id.buttoncor1);
        ButtonCor2 = findViewById(R.id.buttoncor2);
        ButtonCor3 = findViewById(R.id.buttoncor3);
        ButtonCor4 = findViewById(R.id.buttoncor4);
        ButtonCor5 = findViewById(R.id.buttoncor5);
        ButtonCor6 = findViewById(R.id.buttoncor6);
        ButtonCor7 = findViewById(R.id.buttoncor7);
        ButtonCor8 = findViewById(R.id.buttoncor8);

        View.OnClickListener colorButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initial color

                switch (view.getId()){

                    case R.id.buttoncor1:
                        currentColor = 1; //red
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor2:
                        currentColor = 2; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor3:
                        currentColor = 3; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor4:
                        currentColor = 4; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor5:
                        currentColor = 5; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor6:
                        currentColor = 6; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor7:
                        currentColor = 7; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;
                    case R.id.buttoncor8:
                        currentColor = 8; //green
                        new SendCommandTask().execute(COMMAND_COLOR, currentColor);
                        break;


                }
            }
        };



        ButtonCor1.setOnClickListener(colorButtonListener);
        ButtonCor2.setOnClickListener(colorButtonListener);
        ButtonCor3.setOnClickListener(colorButtonListener);
        ButtonCor4.setOnClickListener(colorButtonListener);

        ButtonCor5.setOnClickListener(colorButtonListener);
        ButtonCor6.setOnClickListener(colorButtonListener);
        ButtonCor7.setOnClickListener(colorButtonListener);
        ButtonCor8.setOnClickListener(colorButtonListener);

        //barra de brilho
        brightnessSeekBar = findViewById(R.id.brightnessseekbar);
        brightnessSeekBar.setMax(52); // Define o valor máximo como 52
        brightnessSeekBar.setProgress(currentBrightness); // Define o progresso inicial
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the current brightness value and send the command
                currentBrightness = progress;
                new SendCommandTask().execute(COMMAND_SET_BRIGHTNESS, currentBrightness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        //barra de white
        whitenessSeekBar = findViewById(R.id.whitenessseekbar);
        whitenessSeekBar.setMax(255); // Define o valor máximo como 52
        whitenessSeekBar.setProgress(currentWhiteness); // Define o progresso inicial
        whitenessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the current whiteness value and send the command
                currentWhiteness = progress;
                new SendCommandTask().execute(COMMAND_WHITE, currentWhiteness);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        checkBoxNoite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    limitbright = 40;
                    limitwhite = 20;
                }
                else {
                    limitbright = 255;
                    limitwhite = 255;
                }

            }
        });

        checkBoxSono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean w) {
                if (w) {
                    limitbright = 10;
                    limitwhite = 10;
                }
                else {
                    limitbright = 255;
                    limitwhite = 255;
                }

            }
        });


    }

    private void startUDPThread() {
        if (udpThread == null) {
            udpThread = new UDPThread("255.255.255.255", 30978, 30978); // Multicast IP, Multicast Port, Source Port
            udpThread.setHandler(udpHandler);
            new Thread(udpThread).start();
        }
    }
    private class SendCommandTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... commands) {

            if (commands != null && commands.length > 0) {
                int commandType = commands[0];
                switch (commandType) {
                    case COMMAND_TURN_ON:
                        sendTurnOnCommand();
                        break;
                    case COMMAND_TURN_OFF:
                        sendTurnOffCommand();
                        break;
                    case COMMAND_SET_BRIGHTNESS:
                        int brightness = commands[1]; // Extract the brightness value
                        sendBrightnessCommand(brightness);
                        break;
                    case COMMAND_WHITE:
                        int whiteness = commands[1]; //analisar este indice.
                        sendWhitenessCommand(whiteness);
                        break;
                    case COMMAND_COLOR:
                        int colorcommand = commands[1];
                        sendColorCommand(colorcommand);
                }
            }
            return null;
        }
    }


    private void sendColorCommand(int colorcommand){
        switch (colorcommand){
            case 1:
                byte[] redCommand = createColorCommand(0xFF, 0x00, 0x00); // Vermelho
                udpThread.sendMessage(redCommand, "255.255.255.255", 30977); // Destination IP, Destination Port
                break;
            case 2:
                byte[] greenCommand = createColorCommand(0x00, 0xFF, 0x00); // Verde
                udpThread.sendMessage(greenCommand,"255.255.255.255",30977);
                break;
            case 3:
                byte[] blueCommand = createColorCommand(0x00, 0x00, 0xFF); // Azul
                udpThread.sendMessage(blueCommand,"255.255.255.255",30977);
                break;
            case 4:
                byte[] yellowCommand = createColorCommand(0xFF, 0xFF, 0x00); // Amarelo
                udpThread.sendMessage(yellowCommand,"255.255.255.255",30977);
                break;
            case 5:
                byte[] pinkCommand = createColorCommand(0xFF, 0x00, 0xFF); // Rosa
                udpThread.sendMessage(pinkCommand,"255.255.255.255",30977);
                break;
            case 6:
                byte[] cyanCommand = createColorCommand(0x00, 0xFF, 0xFF); // Cian
                udpThread.sendMessage(cyanCommand,"255.255.255.255",30977);
                break;

            case 7:
                byte[] orangeCommand = createColorCommand(0xFF, 0x80, 0x00); // Laranja
                udpThread.sendMessage(orangeCommand,"255.255.255.255",30977);
                break;

            case 8:
                byte[] rouseCommand = createColorCommand(0xfc, 0x21, 0x52); // Branco xfc\x21\x5e
                udpThread.sendMessage(rouseCommand,"255.255.255.255",30977);
                break;



        }
    }



    private void sendTurnOffCommand() {
        byte[] commandoff = createTurnOffCommand(); // Create the specific command to turn off the lamp
        udpThread.sendMessage(commandoff, "255.255.255.255", 30977); // Destination IP, Destination Port
        on = false;
        if (udpThread.online) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startButton.setText("Ligar");
                    imageView.setImageResource(R.drawable.lampoff);
                    LampStatus.setText("Desligada");

                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LampStatus.setText("Offline");

                }
            });
        }
    }
    private void sendTurnOnCommand() {
        byte[] commandon = createTurnOnCommand(); // Create the specific command to turn on the lamp
        udpThread.sendMessage(commandon, "255.255.255.255", 30977); // Destination IP, Destination Port
        on = true  ;
        if (udpThread.online) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startButton.setText("Desligar");
                    imageView.setImageResource(R.drawable.lamp);
                    LampStatus.setText("Ligada");

                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LampStatus.setText("Offline");

                }
            });
        }


    }
    private void sendBrightnessCommand(int brightness) {
        byte[] commandbr = createBrightnessCommand(brightness); // Create the command to change brightness
        udpThread.sendMessage(commandbr, "255.255.255.255", 30977); // Destination IP, Destination Port
    }

    // create slider
    private void sendWhitenessCommand(int whiteness) {
        byte[] commandwh = createWhiteCommand(whiteness); // Create the command to change brightness
        udpThread.sendMessage(commandwh, "255.255.255.255", 30977); // Destination IP, Destination Port
    }

    private byte[] createTurnOffCommand() {
        //fbeb0000000000e9d4cf00
        byte[] commandoff = {(byte) 0xfb, (byte) 0xeb, 0x00, 0x00, 0x00, 0x00,0x00, (byte) 0xe9, (byte) 0xd4, (byte) 0xcf, 0x00};
        return commandoff;
    }
    private byte[] createTurnOnCommand() {
        //fbeb000000ff00e9d4cf00
//        byte[] commandon = {(byte) 0xfb, (byte) 0xeb, 0x00, 0x00, 0x00, (byte) 0xff, 0x00, (byte) 0xe9, (byte) 0xd4, (byte) 0xcf, 0x00};
//        return commandon;

        byte[] buf = new byte[11];
        buf[0] = -5;
        buf[1] = -21;
        buf[2] = (byte) 0;
        buf[3] = (byte) 0;
        buf[4] = (byte) 0;
        buf[5] = (byte) limitbright; //255 maximum  /bright
        buf[6] = (byte) 0;
        buf[7] = (byte) 233;
        buf[8] = (byte) 212;
        buf[9] = (byte) 207;
        buf[10] = (byte) 0;

        return buf;
    }


    //\xfb\xe9\x00\x01\x04\x02\x34\xe9\xd4\xcf\x00
    private byte[] createBrightnessCommand(int brightness) {

        byte[] buf = new byte[11];
        buf[0] = -5;
        buf[1] = -20;
        buf[2] = (byte) 0;
        buf[3] = (byte) 1;
        buf[4] = (byte) 4;
        buf[5] = (byte) 2;
        buf[6] = (byte) (brightness & 255);
        buf[7] = -23;
        buf[8] = -44;
        buf[9] = -49;
        buf[10] = 0;

        return buf;
    }

    private byte[] createWhiteCommand(int whiteness) {
        byte[] buff = new byte[11];
        buff[0] = -5;
        buff[1] = -21;
        buff[2] = (byte) 0;
        buff[3] = (byte) 0;
        buff[4] = (byte) 0;
        buff[5] = (byte) (whiteness & 255);
        buff[6] = 0;
        buff[7] = (byte) 0xe9;
        buff[8] = (byte) 0xd4;
        buff[9] = (byte) 0xcf;
        buff[10] = 0;
        return buff;
    }

    private byte[] createColorCommand(int red, int green, int blue) {
        byte[] buffcolor = new byte[11];
        buffcolor[0] = -5;
        buffcolor[1] = -21;
        buffcolor[2] = (byte) red;
        buffcolor[3] = (byte) green;
        buffcolor[4] = (byte) blue;
        buffcolor[5] = 0;
        buffcolor[6] = 0;
        buffcolor[7] = (byte) 0xE9;
        buffcolor[8] = (byte) 0xD4;
        buffcolor[9] = (byte) 0xCF;
        buffcolor[10] = 0;
        return buffcolor;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (udpThread != null) {
            udpThread.stop();
        }
    }
}
