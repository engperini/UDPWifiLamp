# UDPWifiLamp Android App

![image](https://github.com/engperini/UDPWifiLamp/assets/117356668/74585376-f0f4-479b-970b-ccee2694abc7)

## Disclaimer

**Note:** This code is designed for controlling Wi-Fi LED lamps that use the UDP protocol, specifically for lamps compatible with the "C-light" mobile app. Different manufacturers may have their own specific protocols, and this code may not work with lamps from other brands.

## Overview

The UDPWifiLamp Android app is a simple application for controlling Wi-Fi-enabled LED lamps using the UDP (User Datagram Protocol) communication protocol. This app allows users to perform various actions on the lamp, including changing colors, turning the lamp on or off, adjusting brightness, and customizing the lamp's name on the app. Additionally, it provides the ability to change the lamp's Wi-Fi network.

## Table of Contents

1. [MainActivity.java](#mainactivityjava)
2. [UDPThread.java](#udpthreadjava)
3. [License](#license)

---

## MainActivity.java

### Description

`MainActivity.java` is the main activity class of the UDPWifiLamp app. It provides the user interface for controlling the lamp. This class handles user interactions and sends commands to the lamp using the UDP protocol.

### Functions

1. `onCreate(Bundle savedInstanceState)`: Initializes the main activity and sets up the user interface elements. It also establishes a UDP communication thread for sending and receiving commands.

2. `startUDPThread()`: Starts the UDP communication thread for sending and receiving UDP packets.

3. `SendCommandTask`: An AsyncTask class that handles sending different types of commands to the lamp based on user interactions. Supported commands include turning on/off the lamp, adjusting brightness, changing colors, and setting whiteness.

4. Command Methods:
   - `sendTurnOnCommand()`: Sends a command to turn on the lamp.
   - `sendTurnOffCommand()`: Sends a command to turn off the lamp.
   - `sendBrightnessCommand(int brightness)`: Sends a command to adjust the lamp's brightness.
   - `sendWhitenessCommand(int whiteness)`: Sends a command to set the lamp's whiteness.
   - `sendColorCommand(int colorcommand)`: Sends a command to change the lamp's color based on a predefined color index.

5. UI Elements: The class manages various UI elements such as buttons, seek bars, and checkboxes for controlling the lamp's settings.

6. `onDestroy()`: Stops the UDP communication thread when the activity is destroyed to prevent resource leaks.

---

## UDPThread.java

### Description

`UDPThread.java` is a separate thread class responsible for handling UDP communication. It sends and receives UDP packets to and from the lamp. It also parses incoming packets to extract lamp status information.

### Functions

1. `UDPThread(String multicastGroupIP, int multicastPort, int sourcePort)`: Constructor that initializes the UDP communication settings. It creates a MulticastSocket for receiving UDP packets.

2. `run()`: The main run method of the thread. It listens for incoming UDP packets, extracts lamp status information, and updates the UI accordingly.

3. `requestStatus()`: Sends a request for lamp status information to the lamp.

4. `stop()`: Stops the UDP thread and leaves the multicast group when the app is closed.

5. `setHandler(Handler handler)`: Sets the handler for communication between the UDP thread and the main activity.

6. `sendMessage(byte[] message, String destinationIP, int destinationPort)`: Sends a UDP message to the specified IP address and port.

---

## License

This project is licensed under the MIT License, 2023, by PeriniDev.

**Note:** Always ensure that you have the necessary permissions to use and distribute code and follow the licensing terms provided in the MIT License.

---


