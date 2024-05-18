package main;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * One Window responsible for dimming one physical Monitor.
 */
public class MonitorDimmer extends JFrame implements Dimmable {

    // The dim level (0-100)
    private int dimLevel;

    private LinkedList<DimListener> listeners = new LinkedList<>();



    /// Converts the dim level (0-100) to a byte (0-255)
    public static int mapToByte(int dimLevel) {
        return (int) (dimLevel * 2.55);
    }


    /**
     * Set the dim level of the window.
     * @param dimLevel The dim level (0-100).
     */
    @Override
    public void setDim(int dimLevel) {
        final int newDimLevel = Math.max(0, Math.min(90, dimLevel));
        if (this.dimLevel == newDimLevel) {
            return;
        }

        //System.out.println("Default Transform: "+ getGraphicsConfiguration().getDefaultTransform());
        SwingUtilities.invokeLater(() -> {
            this.dimLevel = newDimLevel;

            //Hide the window if the dim level is 0
            if(isVisible() == (dimLevel == 0)) {
                if(dimLevel == 0) {
                    System.out.println("Hiding window");
                } else {
                    System.out.println("Showing window");
                }
                setVisible(dimLevel != 0);
            }

            repaint();

            notifyListeners(newDimLevel);
        });
    }

    /**
     * Gets the dim level of the window.
     * @return The dim level (0-100).
     */
    @Override
    public int getDim() {
        return dimLevel;
    }

    @Override
    public void addChangeListener(DimListener listener) {
        listeners.add(listener);
    }

    public MonitorDimmer(int initialDim, GraphicsDevice screen) {
        super(screen.getDefaultConfiguration());

        this.dimLevel = initialDim;

        setUndecorated(true);

        //Mark it as maximized
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setType(Window.Type.UTILITY);
        setAlwaysOnTop(true); // Make sure the frame stays on top



        setBackground(new Color(0,0,0,0));

        JPanel dimPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, mapToByte(dimLevel)));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        dimPanel.setOpaque(false);


        //frame.setOpacity(0.1f);
        add(dimPanel);


        setVisible(true);


        makeTransparent();
    }

    /**
     * Uses the JNA to set extended window styles that prevent the window from grabbing inputs
     * and tells Windows the window might have transparency. (swing probably already sets this?)
     */
    private void makeTransparent() {
        WinDef.HWND hwnd = getWindowHandle(this);
        int windowLong = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);

        // Set extended window styles
        // https://learn.microsoft.com/en-us/windows/win32/winmsg/extended-window-styles
        windowLong = windowLong | WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, windowLong);
    }

    /**
     * Get the window handle from the OS
     */
    private static WinDef.HWND getWindowHandle(Component w) {
        WinDef.HWND hwnd = new WinDef.HWND();
        hwnd.setPointer(Native.getComponentPointer(w));
        return hwnd;
    }

    private void notifyListeners(int dimLevel) {
        for(DimListener listener : listeners) {
            listener.dimChanged(dimLevel);
        }
    }


}

