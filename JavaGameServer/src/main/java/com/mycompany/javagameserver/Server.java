package com.mycompany.javagameserver;

import com.mycompany.javagameserver.services.ClientService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author basel
 */
public class Server {

    private final int port;

    private Thread thread;
    private ServerSocket serverSocket;
    private Listener listener;

    public Server(int port) {
        this.port = port;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void start() {
        thread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);

                if (listener != null) {
                    listener.onServerThreadStarted();
                }

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    CommunicatorClient client = new CommunicatorClient(clientSocket);

                    client.start();
                    ClientService.getService().addClient(client);
                }
            } catch (IOException ex) {
                if (ex instanceof SocketException) {
                    if (listener != null) {
                        listener.onServerThreadStarted();
                    }
                } else {
                    System.err.println("Server error occurred: " + ex.toString());
                    ex.printStackTrace();
                    stop();
                }
            }

            if (listener != null) {
                listener.onServerThreadStopping();
            }
        });

        thread.start();
    }

    public void stop() {
        ClientService.getService().getClients().forEach(client -> client.stop());

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error closing server socket.");
        }

        thread = null;
        serverSocket = null;
    }

    public boolean isRunning() {
        return thread != null && thread.isAlive() && serverSocket != null && !serverSocket.isClosed();
    }

    public interface Listener {

        void onServerThreadStarted();

        void onServerThreadStopping();
    }
}
