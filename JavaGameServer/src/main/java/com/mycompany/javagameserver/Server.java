package com.mycompany.javagameserver;

import com.mycompany.javagameserver.services.ClientService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author basel
 */
public class Server {
    private final Thread thread;
        
        public Server(int port) {
            thread = new Thread(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        CommunicatorClient client = new CommunicatorClient(clientSocket);
                        
                        client.start();
                        ClientService.getService().addClient(client);
                    }
                } catch (IOException ex) {
                    System.err.println("Server error occurred: " + ex.toString());
                    ex.printStackTrace();
                    stop();
                }
            });
        }
        
        public void start() {
            thread.start();
        }
        
        public void stop() {
            ClientService.getService().getClients().forEach(client -> client.stop());
            thread.stop();
        }
}
