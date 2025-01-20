package com.mycompany.javagameserver;

import com.mycompany.javagameserver.handling.*;
import com.mycompany.networking.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author basel
 */
public class CommunicatorClient implements Client {
    private final Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Thread thread;
    boolean serverOn = true;
    
    public CommunicatorClient(Socket socket) {
        this.socket = socket;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            // TODO : release all players resources
            System.out.println("TODO : release all players resources");
        }
    }
    
    @Override
    public void start() {
        // AuthHandler  -->  MatchingHandler  -->  GameHandler
        Handler authHandler = new AuthHandler();
        
        authHandler.bind(this);
//        handler.setNext(matchingHandler);
        
        thread = new Thread(() -> {
            while (serverOn) {
                
                try {
                    Object obj = inputStream.readObject();
                    
                    Message msg = (Message) obj;
                    
                    Request request = new Request(msg);
                    
                    authHandler.handle(request);                    
                } catch (IOException ex) {
                    // close responding to messages
                    serverOn = false;
                    stop();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(CommunicatorClient.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
        });
        thread.start();
    }
    
    @Override
    public void stop() {
        if (thread.isAlive()) {
            thread.stop();
        }
    }
    
    @Override
    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException ex) {
            // to be handled
        }
    }

    @Override
    public GameHandler getGameHandler() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
