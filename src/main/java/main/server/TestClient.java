package main.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TestClient extends Thread {



    @Override
    public void run() {
        //user input
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket("localhost", 8777)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            try {
                Thread.sleep(5000);

                for (int i = 0; i < 10; i++) {
                    writer.println(i * 8);
                    Thread.sleep(500);
                }

            } catch(InterruptedException e) {
                throw new RuntimeException(e);
            }



            while (true) {
                System.out.println("Enter a dim level (0-100): ");
                int dimLevel = scanner.nextInt();

                System.out.println("Sending dim level " + dimLevel);
                writer.println(dimLevel);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
