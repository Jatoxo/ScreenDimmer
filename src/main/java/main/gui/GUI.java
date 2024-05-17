package main.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import main.ScreenShade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;

public class GUI extends JFrame {
    private ScreenShade screenShade;

    public GUI(ScreenShade screenShade) {
        super("Screen Shade");

        this.screenShade = screenShade;

        //setMinimumSize(new Dimension(380, 370));
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/icon3.png")).getImage());

        setupTray();


        DimmerWindow dimmerWindow = new DimmerWindow(screenShade);
        setContentPane(dimmerWindow.getPanel1());


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
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
                    setVisible(!isVisible());
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
