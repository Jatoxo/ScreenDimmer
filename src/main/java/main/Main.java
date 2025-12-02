package main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/*
    Lightweight application to dim the screen of your computer more than the lowest brightness setting.
    Works by overlaying a partially translucent overlay on top of the screen.
    Works with multiple monitors.

    Includes a server that listens on port 8777 (default) for incoming connections
    and sets the dim level based on the received value.

    Also includes an MQTT client that listens for messages on a topic and sets the dim level based on the received value.
    (See the config file for MQTT configuration)

    //Options:
    // 1. Provide an argument to set the initial dim level (0-100).
    // 2. --no-server to disable the server. //Todo
    // 3. --no-gui to disable the GUI. //Todo
 */

public class Main {
    public static final String CONFIG_FILE = "config.properties";

    static ScreenShadeConfig config;

    public static void main(String[] args) throws IOException {
        // Ensure the config file exists
        ScreenShadeConfig.ensureConfigFile();

        // Load the config
        config = ScreenShadeConfig.loadConfig();

        parseArgs(args);
        createAndShowApp();
    }

    static void parseArgs(String[] args) {
        if (args.length > 0) {
            try {
                config.initialDimLevel = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + args[0]);
            }
        }
    }

    static void createAndShowApp() {
        FlatLaf.setup(new FlatDarkLaf());

        SwingUtilities.invokeLater(() -> {
            ScreenShade screenShade = new ScreenShade(config);
        });
    }





}
