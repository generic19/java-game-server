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
    MatchingHandler matchingHandler;
    GameHandler gameHandler;
    
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
        matchingHandler = new MatchingHandler();
        gameHandler = new GameHandler();
        authHandler.bind(this);
        matchingHandler.bind(this);
        gameHandler.bind(this);
        authHandler.setNext(matchingHandler);
        matchingHandler.setNext(gameHandler);
        
        thread = new Thread(() -> {
            try {
                while (true) {
                    
                    try {
                        Object obj = inputStream.readObject();
                        
                        Message msg = (Message) obj;
                        
                        Request request = new Request(msg);
                        
                        authHandler.handle(request);
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Error reading message from socket.");
                    }
                }
            } catch (IOException ex) {
                System.out.println("Client socket closed.");
            }
        });
        thread.start();
    }
    
    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Could not close socket.");
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
    public MatchingHandler getMatchingHandler() {
        return matchingHandler;
    }
    
    @Override
    public GameHandler getGameHandler() {
        return gameHandler;
    }
}
