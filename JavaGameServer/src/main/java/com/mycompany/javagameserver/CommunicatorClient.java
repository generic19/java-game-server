package com.mycompany.javagameserver;

import com.mycompany.javagameserver.handling.*;
import com.mycompany.javagameserver.services.ClientService;
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
    AuthHandler authHandler;
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
                        
                        System.out.println("received from " + getClientRepresentation() + " " + obj + ".");

                        Message msg = (Message) obj;

                        Request request = new Request(msg);

                        authHandler.handle(request);
                    } catch (ClassNotFoundException ex) {
                        System.err.println("error reading message from socket.");
                    }
                }
            } catch (IOException ex) {
                ClientService.getService().removeClient(this);
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

    private String getClientRepresentation() {
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
            System.out.println("sending to " + getClientRepresentation() + " " + message + "..");
            
            outputStream.writeObject(message);
            
            System.out.println("sent.");
        } catch (IOException ex) {
            System.err.print("could not send message to " + getClientRepresentation() + ".");
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
