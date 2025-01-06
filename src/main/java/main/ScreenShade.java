package main;

import main.gui.GUI;
import main.server.MqttDimmer;
import main.server.ServerThread;
import org.eclipse.paho.client.mqttv3.MqttException;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the dimming of multiple screens.
 * This class is responsible for creating the MonitorDimmer objects and handling the server thread.
 * It also provides the GUI for the user to control the dim level.
 */
public class ScreenShade {
    private final List<MonitorDimmer> dimmers = new ArrayList<>();

    Dimmable masterDimmable;

    public ScreenShade(ScreenShadeConfig config) {
        setupDimmers(config.initialDimLevel);

        //Create the GUI
        GUI gui = new GUI(this, config.startMinimized);

        if(config.enableServer) {
            System.out.println("Starting TCP Server");
            //Start the server thread
            ServerThread serverThread = new ServerThread(this, 8777);
            serverThread.start();
        } else {
            System.out.println("TCP Server is disabled.");
        }

        if(config.mqttClientId != null
                && config.mqttBroker != null
                && config.mqttTopicSet != null
                && config.mqttTopicState != null)
        {
            System.out.println("Starting MQTT Client");
            try {
                MqttDimmer mqttDimmer = new MqttDimmer(
                        this,
                        config.mqttClientId,
                        config.mqttBroker,
                        config.mqttTopicSet,
                        config.mqttTopicState,
                        config.mqttUsername,
                        config.mqttPassword
                );
            } catch(MqttException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Not starting MQTT Client. Missing configuration.");
        }


        //Run Test client
        //TestClient testClient = new TestClient();
        //testClient.start();
    }

    private void setupDimmers(int initialDimLevel) {
        // Create a dimmer for each screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        for(GraphicsDevice screen : screens) {
            dimmers.add(new MonitorDimmer(initialDimLevel, screen));
        }

        //Create a master dimmer that controls all dimmers
        masterDimmable = new MasterDimmer(dimmers.toArray(new Dimmable[0]));
    }


    /**
     * Set the master dim level for all screens.
     * @param level The dim level (0-100).
     */
    public void setMasterDim(int level) {
        masterDimmable.setDim(level);
    }


    /**
     * Get the master dim level for all screens.
     * @return The dim level (0-100).
     */
    public int getMasterDim() {
        return masterDimmable.getDim();
    }

    public Dimmable getMasterDimmer() {
        return masterDimmable;
    }

    /**
     * Get a specific dimmer (screen).
     * @param index The index of the dimmer.
     * @return The dimmer.
     */
    public Dimmable getDimmer(int index) {
        return dimmers.get(index);
    }

    /**
     * Get all real dimmers (screens).
     * @return An array of dimmers.
     */
    public Dimmable[] getDimmers() {
        return dimmers.toArray(new Dimmable[0]);
    }

    /**
     * Get the number of dimmers (screens).
     * @return The number of dimmers.
     */
    public int getDimmerCount() {
        return dimmers.size();
    }

}
