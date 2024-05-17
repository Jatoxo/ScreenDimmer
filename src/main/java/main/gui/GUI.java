package main.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import main.ScreenShade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

public class GUI extends JFrame {
    private ScreenShade screenShade;

    public GUI(ScreenShade screenShade) {
        super("Screen Shade");

        this.screenShade = screenShade;

        setMinimumSize(new Dimension(300, 350));
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());

        setupTray();


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupTray() {
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icon.png")).getImage());

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Screen Shade");
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                screenShade.setMasterDim(screenShade.getMasterDim() + e.getWheelRotation());
            }
        });


        try {
            tray.add(trayIcon);
        } catch(AWTException e) {
            throw new RuntimeException(e);
        }
    }

}
