/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.networking.Message;

/**
 *
 * @author basel
 */
public class Request {
    private final Message message;

    public Request(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
