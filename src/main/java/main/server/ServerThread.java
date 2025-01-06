package main.server;

import main.ScreenShade;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerThread extends Thread {
    private final ScreenShade screenShade;
    private boolean running = true;
    private final int port;

    // We're using a thread pool to handle clients
    // This way we can avoid creating too many threads
    // Because the cli could spam connections quickly
    private final ExecutorService executorService;


    public ServerThread(ScreenShade screenShade, int port) {
        this.screenShade = screenShade;
        this.port = port;

        this.executorService = Executors.newCachedThreadPool();
    }

    public void stopServer() {
        running = false;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server: Listening on port " + port + "...");

            while (running) {
                try {
                    Socket client = serverSocket.accept();

                    System.out.println("Accepted connection from: " + client.getInetAddress() + ":" + client.getPort());
                    handleClient(client);

                } catch(IOException e) {
                    System.out.println("Error accepting connection");
                }

            }


        } catch(IOException e) {
            System.out.println("Could not open server socket");
            throw new RuntimeException(e);
        } finally {
            System.out.println("Server thread shutting down");
            executorService.shutdown();
        }
    }

    /**
     * Handle a client connection.
     * @param client The client socket.
     */
    private void handleClient(Socket client) {
        ClientHandler clientHandler = new ClientHandler(client, screenShade);
        executorService.submit(clientHandler);


        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;

        System.out.printf(
                "Threads (Active/Current/Max): (%d/%d/%d)%n",
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getPoolSize(),
                threadPoolExecutor.getLargestPoolSize()
        );

    }


}
