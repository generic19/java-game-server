/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.networking.Communicator;
import com.mycompany.networking.Message;

/**
 *
 * @author basel
 */
public class BaseHandler {
    private final Communicator communicator;
    private BaseHandler next;

    BaseHandler(Communicator communicator) {
        this.communicator = communicator;
    }

    public BaseHandler getNext() {
        return next;
    }
    
    public void handle(Request request) {
        if (next != null) {
            this.next.handle(request);
        }
    }
    
    protected void sendMessage(Message message) {
        communicator.sendMessage(message);
    }
}
