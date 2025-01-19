/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.javagameserver.Client;

/**
 *
 * @author basel
 */
public interface Handler {
    void bind(Client client);
    
    void handle(Request request);
    void setNext(Handler handler);
}
