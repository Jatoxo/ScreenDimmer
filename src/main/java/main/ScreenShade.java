package main;

import main.gui.GUI;
import main.server.ServerThread;

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

    public ScreenShade(int initialDimLevel) {
        setupDimmers(initialDimLevel);

        //Create the GUI
        GUI gui = new GUI(this);

        //Start the server thread
        ServerThread serverThread = new ServerThread(this, 8777);
        serverThread.start();

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
    }


    /**
     * Set the master dim level for all screens.
     * @param level The dim level (0-100).
     */
    public void setMasterDim(int level) {
        for(MonitorDimmer dimmer : dimmers) {
            dimmer.setDim(level);        }
    }

    /**
     * Get the master dim level for all screens.
     * @return The dim level (0-100).
     */
    public int getMasterDim() {
        //Just return the dim level of the first dimmer for now
        //TODO: Implement master dim somehow
        return dimmers.get(0).getDim();
    }

    /**
     * Get the number of dimmers (screens).
     * @return The number of dimmers.
     */
    public int getDimmerCount() {
        return dimmers.size();
    }

}
