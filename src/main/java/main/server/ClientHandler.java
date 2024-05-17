package main.server;

import main.ScreenShade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket client;
    private final ScreenShade screenShade;

    public ClientHandler(Socket client, ScreenShade screenShade) {
        this.client = client;
        this.screenShade = screenShade;
    }

    public void run() {
        try {
            InputStream stream = client.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String text;
            while((text = in.readLine()) != null) {
                System.out.println("Received: " + text);

                try {
                    int dimLevelPercent = Integer.parseInt(text);
                    screenShade.setMasterDim(dimLevelPercent);
                } catch(NumberFormatException e) {
                    System.out.println("Invalid number: " + text);
                }
            }

        } catch(IOException e) {
            System.out.println("Error reading from client");
            throw new RuntimeException(e);

        } finally {
            try {
                System.out.printf("Closing connection to %s:%d\n", client.getInetAddress(), client.getPort());
                client.close();
            } catch(IOException e) {
                System.out.println("Error closing client socket");
                throw new RuntimeException(e);
            }
        }

    }
}
