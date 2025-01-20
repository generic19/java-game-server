/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package com.mycompany.javagameserver;

import com.mycompany.javagameserver.handling.BaseHandler;
import com.mycompany.javagameserver.handling.Handler;
import com.mycompany.javagameserver.handling.Request;
import com.mycompany.networking.Communicator;
import com.mycompany.networking.CommunicatorImpl;
import com.mycompany.networking.Message;
import java.io.IOException;
import java.net.Socket;

/**                     
 *
 * @author basel
 */
public class CommunicatorClient implements Client {
    private final Socket socket;
    private Communicator communicator;
    String opponentUserName;
    public CommunicatorClient(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void start() throws IllegalStateException, IOException {
        if (communicator != null) {
            throw new IllegalStateException("Cannot call start() on an already running Client.");
        }
        
        communicator = new CommunicatorImpl(socket);
        
        Handler handler = new BaseHandler();
        handler.bind(this);
        
        communicator.setListener(Message.class, ((message, hasError) -> {
            handler.handle(new Request(message));
        }));
    }
    
    @Override
    public void stop() {
        if (communicator != null) {
            communicator.close();
        }
    }
    
    @Override
    public void sendMessage(Message message) throws IllegalStateException {
        if (communicator == null) {
            throw new IllegalStateException("Cannot call sendMessage() on a stopped Client.");
        }
        
        communicator.sendMessage(message);
    }

    @Override
    public void setOpponentUserName(String userName) {
        opponentUserName=userName;
        
        
    }

    @Override
    public String getOpponentUserName() {
        return opponentUserName;
    }
}
