/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
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
            this.thread = new Thread(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    
                    while (!Thread.interrupted()) {
                        Socket clientSocket = serverSocket.accept();
                        CommunicatorClient client = new CommunicatorClient(clientSocket);
                        
                        try {
                            client.start();
                            ClientService.getService().addClient(client);
                        } catch (IOException ex) {
                            System.err.println("Could not start client.");
                            ex.printStackTrace();
                        }
                    }
                    
                    System.out.println("Stopped server.");
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
            thread.interrupt();
        }
}
