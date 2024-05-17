package main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;

/*
    Lightweight application to dim the screen of your computer more than the lowest brightness setting.
    Works by overlaying a partially translucent overlay on top of the screen.
    Works with multiple monitors.

    Also includes a server that listens on port 8777 (default) for incoming connections
    and sets the dim level based on the received value.

    //Options:
    // 1. Provide an argument to set the initial dim level (0-100).
    // 2. --no-server to disable the server.
    // 3. --no-gui to disable the GUI.
 */

public class Main {

    //If an argument is provided, use it as the initial dim level
    static int initialDimLevel = 20;

    public static void main(String[] args) {
        parseArgs(args);
        createAndShowApp();
    }

    static void parseArgs(String[] args) {
        if (args.length > 0) {
            try {
                initialDimLevel = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number: " + args[0]);
            }
        }
    }

    static void createAndShowApp() {
        FlatLaf.setup(new FlatDarkLaf());

        SwingUtilities.invokeLater(() -> {
            ScreenShade screenShade = new ScreenShade(initialDimLevel);
        });
    }
}
