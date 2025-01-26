package com.mycompany.javagameserver;

import com.mycompany.javagameserver.handling.*;
import com.mycompany.javagameserver.services.ClientService;
import com.mycompany.networking.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author basel
 */
public class CommunicatorClient implements Client {

    private final Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    private Thread thread;
    private AuthHandler authHandler;
    private MatchingHandler matchingHandler;
    private GameHandler gameHandler;
    
    public CommunicatorClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void start() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("could not open object streams for " + getRepresentation() + ".");
            throw new RuntimeException(ex);
        }
        
        // AuthHandler  -->  MatchingHandler  -->  GameHandler
        authHandler = new AuthHandler();
        matchingHandler = new MatchingHandler();
        gameHandler = new GameHandler();
        
        authHandler.bind(this);
        matchingHandler.bind(this);
        gameHandler.bind(this);
        
        authHandler.setNext(matchingHandler);
        matchingHandler.setNext(gameHandler);

        thread = new Thread(() -> {
            try {
                ClientService.getService().addClient(this);
                
                while (true) {
                    try {
                        Object obj = inputStream.readObject();

                        Request request = new Request((Message) obj);
                        authHandler.handle(request);
                    } catch (ClassNotFoundException ex) {
                        System.err.println("error reading message from " + getRepresentation() + ".");
                    }
                }
            } catch (IOException ex) {
                ClientService.getService().removeClient(this);
                System.out.println("socket for " + getRepresentation() + " closed.");
            }
        });
        
        thread.start();
    }

    @Override
    public void stop() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("could not close socket for " + getRepresentation() + ".");
        }
    }
    
    public String getRepresentation() {
        if (authHandler != null && authHandler.getUsername() != null) {
            return "username=" + authHandler.getUsername();
        } else if (socket != null && socket.getPort() != 0) {
            return "port=" + socket.getPort();
        } else {
            return "disconnected client";
        }
    }

    @Override
    public void sendMessage(Message message) {
        try {
//            System.out.println("client service state before sending: " + ClientService.getService());
            outputStream.writeObject(message);
            
            System.out.println("sent to " + getRepresentation() + " message " + message + ".");
        } catch (IOException ex) {
            System.err.print("could not send to " + getRepresentation() + " message " + message + ".");
        }
    }

    @Override
    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    @Override
    public MatchingHandler getMatchingHandler() {
        return matchingHandler;
    }

    @Override
    public GameHandler getGameHandler() {
        return gameHandler;
    }

    @Override
    public String toString() {
        return "CommunicatorClient{" + "socket=" + socket + ", thread=" + thread + ", authHandler=" + authHandler + ", matchingHandler=" + matchingHandler + ", gameHandler=" + gameHandler + '}';
    }
}
