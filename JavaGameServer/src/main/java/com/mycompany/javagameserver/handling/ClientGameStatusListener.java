/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.javagameserver.handling;

import com.mycompany.javagameserver.Client;

/**
 *
 * @author basel
 */
public interface ClientGameStatusListener {

    void onClientStartedGame(Client client);

    void onClientEndedGame(Client client);
    
}
