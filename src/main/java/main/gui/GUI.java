package main.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import main.ScreenShade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private ScreenShade screenShade;

    public GUI(ScreenShade screenShade, boolean startMinimized) {
        super("Screen Shade");

        this.screenShade = screenShade;

        //setMinimumSize(new Dimension(380, 370));
        //setUndecorated(true);
        //setType(Type.UTILITY);




        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/icon3.png")).getImage());

        setupTray();


        DimmerWindow dimmerWindow = new DimmerWindow(screenShade);
        setContentPane(dimmerWindow.getPanel1());


        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        pack();
        if(!startMinimized) {
            setVisible(true);
        }
    }



    private void setupTray() {
        SystemTray tray = SystemTray.getSystemTray();

        TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icon3.png")).getImage());

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Screen Shade");




        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    setState(Frame.NORMAL);
                    setVisible(!isVisible());
                    toFront();
                }
            }
        });

        PopupMenu popup = new PopupMenu();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            System.exit(0);
        });
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);


        try {
            tray.add(trayIcon);
        } catch(AWTException e) {
            throw new RuntimeException(e);
        }
    }

}
