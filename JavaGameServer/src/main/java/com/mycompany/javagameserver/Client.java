/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.javagameserver;

import com.mycompany.javagameserver.handling.GameHandler;
import com.mycompany.networking.Message;
import java.io.IOException;

/**
 *
 * @author basel
 */
public interface Client {

    void sendMessage(Message message) throws IllegalStateException;

    void start() throws IllegalStateException, IOException;

    void stop();
    
    GameHandler getGameHandler();
}
