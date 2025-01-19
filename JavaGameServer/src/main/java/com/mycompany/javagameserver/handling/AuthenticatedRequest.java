/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.networking.Message;

/**
 *
 * @author AhmedAli
 */
public class AuthenticatedRequest extends Request{
    
    private String userName;

    public AuthenticatedRequest(String userName, Message message) {
        super(message);
        this.userName = userName;
    }
  
    public String getUserName() {
        return userName;
    }
    
    
    
}
